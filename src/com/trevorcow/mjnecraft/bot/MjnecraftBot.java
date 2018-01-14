package com.trevorcow.mjnecraft.bot;

import java.net.Proxy;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.trevorcow.mjnecraft.util.ChatMessage;
import com.trevorcow.mjnecraft.util.Host;
import com.trevorcow.mjnecraft.util.PlayerTracker;

public class MjnecraftBot {

	public Host host;
	public MinecraftProtocol protocol;
	public Client client;
	public Session session;
	public Proxy proxy = Proxy.NO_PROXY;

	public PlayerTracker playerTracker;

	public MjnecraftBot(Host host, String username, String password) {
		this.host = host;
		if (password == null) {
			protocol = new MinecraftProtocol(username);
		} else {
			try {
				protocol = new MinecraftProtocol(username, password, false);
			} catch (RequestException e) {
				e.printStackTrace();
				System.err.println("Error logging in with: " + username + ":" + password);
				System.exit(0);
			}
		}
		client = new Client(host.hostAddr, host.port, protocol, new TcpSessionFactory(proxy));
		session = client.getSession();

		session.setFlag(MinecraftConstants.AUTH_PROXY_KEY, proxy);

		session.addListener(new BotEventListener(this));

		playerTracker = new PlayerTracker(this);

	}

	public MjnecraftBot(Host host, String username) {
		this(host, username, null);
	}

	/**
	 * Call to connect bot
	 */
	public void connect() {
		client.getSession().connect();
	}

	/**
	 * @return Bot's current name
	 */
	public String getName() {
		return protocol.getProfile().getName();
	}

	/**
	 * Sends packet as bot
	 */
	public void sendPacket(Packet p) {
		client.getSession().send(p);
	}

	/**
	 * Sends the string as a chat message
	 */
	public void chat(String message) {
		session.send(new ClientChatPacket(message));
	}

	/**
	 * Fired when the bot successfully connects to the server
	 */
	public void onConnected(ConnectedEvent event) {
		System.out.println("Connected to: " + host);
	}

	/**
	 * Fired when the bot disconnects to the server
	 */
	public void onDisconnected(DisconnectedEvent event) {
		System.out.println("Disconnected: " + Message.fromString(event.getReason()).getFullText());
		if (event.getCause() != null) {
			event.getCause().printStackTrace();
		}
	}

	/**
	 * Fired when the bot receives a chat message packet
	 */
	public void onChatPacket(ServerChatPacket packet) {
		ChatMessage message = ChatMessage.fromChatPacket(packet);
		if (message.chatmessage != null)
			onChat(message);
	}

	/**
	 * Fired when the bot receives a chat message
	 */
	public void onChat(ChatMessage message) {
	}

	public void onJoinGame(ServerJoinGamePacket packet) {
	}
}
