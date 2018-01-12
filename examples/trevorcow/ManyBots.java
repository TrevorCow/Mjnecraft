package trevorcow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.trevorcow.mjnecraft.bot.MjnecraftBot;
import com.trevorcow.mjnecraft.util.ChatMessage;
import com.trevorcow.mjnecraft.util.Host;

public class ManyBots {

	public static List<ChildBot> bots = new ArrayList<>();

	public static void main(String[] args) {

		Host localhost = new Host("localhost", 25565);

		int amount = 15;
		for (int i = 0; i < amount; i++) {
			bots.add(new ChildBot(localhost, "[Bot] " + getRandomName(10)));
		}

		for (ChildBot cb : bots) {
			cb.connect();
		}
	}

	public static String getRandomName(int length) {
		String use = "`1234567890-=qwertyuiop[]\\asdfghjkl;'zxcvbnm,./~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?";
		Random r = new Random();

		StringBuilder name = new StringBuilder();
		for (int i = 0; i < length; i++) {
			name.append(use.charAt(r.nextInt(use.length())));
		}
		return name.toString();
	}

	public static class ChildBot extends MjnecraftBot {

		public ChildBot(Host host, String username) {
			super(host, username);
		}

		@Override
		public void onChat(ChatMessage message) {
			message.applyRegex("vanilla", Pattern.compile("<(.+)> (.+)"));

			if (message.isValidMessage()) {
				for (ChildBot b : bots) {
					if (message.sender.equals(b.getName()))
						return;
				}
				if (message.message.startsWith("!")) {
					String[] args = message.message.split(" ");
					switch (args[0].substring(1)) {
					case "chat":
						StringBuilder chatBuilder = new StringBuilder();
						for (int i = 1; i < args.length; i++) {
							chatBuilder.append(args[i]);
						}
						chat(chatBuilder.toString());
						break;
					case "spam":
						for (int i = 0; i < 10; i++)
							chat(getRandomName(32));
						break;
					}
				}
			}
		}

	}
}
