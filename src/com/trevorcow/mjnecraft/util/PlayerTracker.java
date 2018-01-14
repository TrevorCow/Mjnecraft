package com.trevorcow.mjnecraft.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.trevorcow.mjnecraft.bot.MjnecraftBot;

public class PlayerTracker extends SessionAdapter {

	public MjnecraftBot bot;
	public double lastx;
	public double lasty;
	public double lastz;
	public float lastyaw;
	public float lastpitch;
	public boolean isReady = false;
	public boolean isOnGround = true;

	public List<Column> loadedColumns = new ArrayList<>();

	public PlayerTracker(MjnecraftBot bot) {
		this.bot = bot;

		bot.session.addListener(this);
	}

	@Override
	public void packetReceived(PacketReceivedEvent event) {
		Packet p = event.getPacket();
		if (p instanceof ServerKeepAlivePacket) {
			ServerKeepAlivePacket pk = (ServerKeepAlivePacket) p;

			// bot.sendPacket(new ServerKeepAlivePacket(pk.getPingId()));
		} else if (p instanceof ServerPlayerPositionRotationPacket) {
			ServerPlayerPositionRotationPacket pk = (ServerPlayerPositionRotationPacket) p;
			lastx = pk.getX();
			lasty = pk.getY();
			lastz = pk.getZ();
			lastyaw = pk.getYaw();
			lastpitch = pk.getPitch();
			isReady = true;
			isOnGround = true;
			bot.sendPacket(new ClientTeleportConfirmPacket(pk.getTeleportId()));
			bot.sendPacket(new ClientPlayerPositionPacket(isOnGround, lastx, lasty, lastz));
		} else if (p instanceof ServerChunkDataPacket) {
			ServerChunkDataPacket pk = (ServerChunkDataPacket) p;

			loadedColumns.add(pk.getColumn());
		} else if (p instanceof ServerUnloadChunkPacket) {
			ServerUnloadChunkPacket pk = (ServerUnloadChunkPacket) p;
			for (Iterator<Column> it = loadedColumns.iterator(); it.hasNext();) {
				Column c = it.next();
				if (c.getX() == pk.getX() && c.getZ() == pk.getZ()) {
					it.remove();
				}
			}
		} else if (p instanceof ServerBlockChangePacket) {
			ServerBlockChangePacket pk = (ServerBlockChangePacket) p;

			int chunkx = pk.getRecord().getPosition().getX() / 16;
			int chunky = pk.getRecord().getPosition().getY() / 16;
			int chunkz = pk.getRecord().getPosition().getZ() / 16;

			int bx = pk.getRecord().getPosition().getX() % 16;
			int by = pk.getRecord().getPosition().getY() % 16;
			int bz = pk.getRecord().getPosition().getZ() % 16;

			for (Column c : loadedColumns) {
				if (c.getX() != chunkx || c.getZ() != chunkz)
					continue;
				Chunk chunk = c.getChunks()[chunky];
				if (chunk == null)
					continue;
				BlockStorage bs = chunk.getBlocks();
				if (bs == null)
					continue;
				try {
					bs.set(bx, by, bz, pk.getRecord().getBlock());
				} catch (IndexOutOfBoundsException e) {
				}
			}
			isOnGround = isOnGround();
		}
	}

	public boolean isOnGround() {
		if (!isReady)
			return false;
		BlockState bs = getBlockAt(lastx, ((int) (lasty + 1.001)) - 1, lastz);
		if (bs == null || bs.getId() == 0) {
			return false;
		}
		return true;

		// for (Column c : new ArrayList<>(loadedColumns)) {
		// int ilastx = (int) lastx;
		// int ilasty = (int) lasty;
		// int lastybelow = (int) (((int) (lasty + 1.00001)) - 1);
		// int ilastz = (int) lastz;
		//
		// int chunkx = ilastx / 16;
		// int chunky = lastybelow / 16;
		// int chunkz = ilastz / 16;
		// if (c.getX() != chunkx || c.getZ() != chunkz) {
		// continue;
		// }
		//
		// Chunk[] chunks = c.getChunks();
		// Chunk in = chunks[chunky];
		// if (in == null)
		// continue;
		// BlockStorage blocks = in.getBlocks();
		// if (blocks == null)
		// continue;
		// int bx = ilastx % 16;
		// int by = lastybelow % 16;
		// int bz = ilastz % 16;
		//
		// BlockState bs;
		// for (double x = -.3; x <= .3; x += .3) {
		// for (double z = -.3; z <= 1; z += .3) {
		// bs = blocks.get((int) (lastx + x), by, (int) (lastz + z));
		// if (bs.getId() != 0) {
		// return true;
		// }
		// }
		// }
		//
		// // System.out.println(bs.getId() + " at: " + bx + ", " + (by - 1) + ", " + bz);
		// }
		// return false;
	}

	public BlockState getBlockAt(double x, double y, double z) {
		int ix = (int) x;
		int iy = (int) y;
		int iz = (int) z;

		int chunkx = ix / 16;
		int chunky = iy / 16;
		int chunkz = iz / 16;

		for (Iterator<Column> it = loadedColumns.iterator(); it.hasNext();) {
			Column c = it.next();
			if (c.getX() != chunkx || c.getZ() != chunkz)
				continue;
			Chunk chunk = c.getChunks()[chunky];
			if (chunk == null)
				continue;
			BlockStorage bs = chunk.getBlocks();
			if (bs == null)
				continue;
			return bs.get(ix % 16, iy % 16, iz % 16);
		}
		return null;
	}

	@Override
	public String toString() {
		return bot.getName() + ":[" + lastx + ", " + lasty + ", " + lastz + "]";
	}
}
