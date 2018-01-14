package trevorcow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.trevorcow.mjnecraft.bot.MjnecraftBot;
import com.trevorcow.mjnecraft.util.ChatMessage;
import com.trevorcow.mjnecraft.util.Host;

public class HiveMind {

	public static MasterBot master;
	public static List<SlaveBot> slaves = new ArrayList<>();

	public static void main(String[] args) {

		Host localhost = new Host("localhost", 25565);

		master = new MasterBot(localhost, "[Bot] Master");

		int amount = 16;
		for (int i = 0; i < amount; i++) {
			slaves.add(new SlaveBot(localhost, "[Bot] " + (i + 1)));
		}

		master.connect();
		for (SlaveBot cb : slaves) {
			cb.connect();
		}
	}

	public static final int[][] square = { //
			{ 1, 1, 1, 1, 1 }, //
			{ 1, 0, 0, 0, 1 }, //
			{ 1, 0, 0, 0, 1 }, //
			{ 1, 0, 0, 0, 1 }, //
			{ 1, 1, 1, 1, 1 }, //
	};

	public static List<double[]> makePicture(int[][] picture) {
		List<double[]> pictureCoords = new ArrayList<>();
		for (int y = 0; y < picture.length; y++) {
			for (int x = 0; x < picture[y].length; x++) {
				if (picture[y][x] == 1) {
					pictureCoords.add(new double[] { x, y });
				}
			}
		}
		return pictureCoords;
	}

	public static class MasterBot extends MjnecraftBot {

		public MasterBot(Host host, String username) {
			super(host, username);
		}

		@Override
		public void onChat(ChatMessage message) {
			message.applyRegex("vanilla", Pattern.compile("<(.+)> (.+)"));

			if (message.isValidMessage()) {
				List<double[]> draw = null;
				switch (message.message) {
				case "draw square":
					draw = makePicture(square);

					break;
				case "draw circle":

					draw = new ArrayList<>();

					double TWO_PI = 2 * Math.PI;
					double slice = 16d;
					double dtheta = TWO_PI / slice;
					double rad = 4;

					for (double i = 0; i < slice; i += 1) {
						double x = rad * Math.cos(dtheta * i);
						double y = rad * Math.sin(dtheta * i);

						draw.add(new double[] { x, y });
					}
					break;
				}
				if (draw != null) {
					final List<double[]> fdraw = draw;
					for (int i = 0; i < draw.size(); i++) {
						final int fi = i;
						new Thread(new Runnable() {
							@Override
							public void run() {
								slaves.get(fi).move(fdraw.get(fi)[0], 0, fdraw.get(fi)[1]);
							}
						}).start();
					}
				}
			}
		}

	}

	public static class SlaveBot extends MjnecraftBot {

		public SlaveBot(Host host, String username) {
			super(host, username);
		}

		public void move(double dx, double dy, double dz) {
			int xdir = (int) Math.signum(dx);
			int ydir = (int) Math.signum(dy);
			int zdir = (int) Math.signum(dz);

			double moveAmount = .1;

			double i = 0;
			while (i < Math.abs(dx)) {
				i += moveAmount;
				updateMove(moveAmount * xdir, 0, 0);
			}
			i = 0;
			while (i < Math.abs(dy)) {
				i += moveAmount;
				updateMove(0, moveAmount * ydir, 0);
			}
			i = 0;
			while (i < Math.abs(dz)) {
				i += moveAmount;
				updateMove(0, 0, moveAmount * zdir);
			}
		}

		public void updateMove(double dx, double dy, double dz) {
			sendPacket(new ClientPlayerPositionPacket(playerTracker.isOnGround, playerTracker.lastx += dx, playerTracker.lasty += dy, playerTracker.lastz += dz));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
