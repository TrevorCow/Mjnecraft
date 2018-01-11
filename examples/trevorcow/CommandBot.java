package trevorcow;

import java.util.regex.Pattern;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.trevorcow.mjnecraft.bot.MjnecraftBot;
import com.trevorcow.mjnecraft.util.ChatMessage;
import com.trevorcow.mjnecraft.util.Host;

public class CommandBot extends MjnecraftBot {

	public CommandBot(Host host, String username, String password) {
		super(host, username, password);
	}

	@Override
	public void onChat(ChatMessage message) {
		message.applyRegex("vanilla", Pattern.compile("<(.+)> (.+)")); // Matches the name and message from a vanilla chat message (First group name, second group messgae)
		message.applyRegex("vanilla_server", Pattern.compile("\\[(.+)\\] (.+)")); // Matches the name and message from a vanilla server message

		if (message.message != null) {
			if (!message.sender.equals(getName())) {
				if (message.message.startsWith("!")) {
					String[] args = message.message.split(" ");
					switch (args[0].substring(1)) {
					case "chat":
						StringBuilder chatBuilder = new StringBuilder();
						for (int i = 1; i < args.length; i++) {
							chatBuilder.append(args[i] + " ");
						}
						chat(chatBuilder.toString());
						break;
					case "countto":
						try {
							int countto = Integer.parseInt(args[1]);
							for (int i = 1; i <= countto; i++) {
								chat("[" + i + "/" + countto + "]");
							}
						} catch (NumberFormatException e) {
							chat(args[1] + " is not a valid number");
						}
						break;
					case "respawn":
						sendPacket(new ClientRequestPacket(ClientRequest.RESPAWN));
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		CommandBot bot = new CommandBot(new Host("localhost"), "CommandBot", null);
		bot.connect();
	}
}
