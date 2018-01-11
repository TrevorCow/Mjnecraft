package com.trevorcow.mjnecraft.bot;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;

public class BotEventListener extends SessionAdapter {

	private MjnecraftBot bot;

	public BotEventListener(MjnecraftBot bot) {
		this.bot = bot;
	}

	@Override
	public void connected(ConnectedEvent event) {
		bot.onConnected(event);
	}

	@Override
	public void disconnected(DisconnectedEvent event) {
		bot.onDisconnected(event);
	}

	@Override
	public void packetReceived(PacketReceivedEvent event) {
		Packet packet = event.getPacket();
		if (packet instanceof ServerChatPacket) {
			ServerChatPacket p = (ServerChatPacket) packet;
			bot.onChat(p);
		} else if (packet instanceof ServerJoinGamePacket) {
			ServerJoinGamePacket p = (ServerJoinGamePacket) packet;
			bot.onJoinGame(p);
		}
	}

	@Override
	public void packetSent(PacketSentEvent event) {

	}
}
