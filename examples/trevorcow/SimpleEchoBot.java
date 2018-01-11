package trevorcow;

import java.util.regex.Pattern;

import com.trevorcow.mjnecraft.bot.MjnecraftBot;
import com.trevorcow.mjnecraft.util.ChatMessage;
import com.trevorcow.mjnecraft.util.Host;

public class SimpleEchoBot extends MjnecraftBot {

	public SimpleEchoBot(Host host, String username, String password) {
		super(host, username, password);
	}

	@Override
	public void onChat(ChatMessage message) {
		System.err.println("Raw chat messsage: " + message.chatmessage);
		message.applyRegex("vanilla", Pattern.compile("<(.+)> (.+)")); // Matches the name and message from a vanilla chat message (First group name, second group messgae)
		message.applyRegex("vanilla_server", Pattern.compile("\\[(.+)\\] (.+)")); // Matches the name and message from a vanilla server message
		System.out.println(message.sender + " said: " + message.message);

		if (message.message != null)
			if (!message.sender.equals(getName()))
				chat(message.message);
	}

	public static void main(String[] args) {
		SimpleEchoBot bot = new SimpleEchoBot(new Host("localhost"), "EchoBot", null);
		bot.connect();
	}
}
