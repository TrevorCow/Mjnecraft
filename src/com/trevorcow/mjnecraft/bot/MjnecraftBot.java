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

public class MjnecraftBot {

	public Host host;
	public MinecraftProtocol protocol;
	public Client client;
	public Session session;
	public Proxy proxy = Proxy.NO_PROXY;

	public MjnecraftBot(Host host, String username, String password) {
		this.host = host;
		if (password == null) {
			protocol = new MinecraftProtocol(username);
		} else {
			try {
				protocol = new MinecraftProtocol(username, password, false);
			} catch (RequestException e) {
				e.printStackTrace();
				protocol = new MinecraftProtocol(username);
			}
		}
		client = new Client(host.hostAddr, host.port, protocol, new TcpSessionFactory(proxy));
		session = client.getSession();

		session.setFlag(MinecraftConstants.AUTH_PROXY_KEY, proxy);

		session.addListener(new BotEventListener(this));

	}

	public void connect() {
		client.getSession().connect();
	}

	public String getName() {
		return protocol.getProfile().getName();
	}

	public void sendPacket(Packet p) {
		client.getSession().send(p);
	}

	public void chat(String message) {
		session.send(new ClientChatPacket(message));
	}

	public void onConnected(ConnectedEvent event) {
		System.out.println("Connected to: " + host);
	}

	public void onDisconnected(DisconnectedEvent event) {
		System.out.println("Disconnected: " + Message.fromString(event.getReason()).getFullText());
		if (event.getCause() != null) {
			event.getCause().printStackTrace();
		}
	}

	public void onChat(ServerChatPacket packet) {
		ChatMessage message = ChatMessage.fromChatPacket(packet);
		if (message.chatmessage != null)
			onChat(message);
	}

	public void onChat(ChatMessage message) {
	}

	public void onJoinGame(ServerJoinGamePacket packet) {
	}
}
