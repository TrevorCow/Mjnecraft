package trevorcow;

import com.trevorcow.mjnecraft.bot.MjnecraftBot;
import com.trevorcow.mjnecraft.util.ChatMessage;
import com.trevorcow.mjnecraft.util.Host;

public class SimpleEchoBot extends MjnecraftBot {

	public SimpleEchoBot(Host host, String username, String password) {
		super(host, username, password);
	}

	@Override
	public void onChat(ChatMessage message) {
		// TODO Auto-generated method stub
		super.onChat(message);
	}

	public static void main(String[] args) {
		SimpleEchoBot bot = new SimpleEchoBot(new Host("localhost"), "username", "password");
	}
}
