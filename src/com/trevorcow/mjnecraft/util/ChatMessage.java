package com.trevorcow.mjnecraft.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diogonunes.jcdp.color.api.Ansi.FColor;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TranslationMessage;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatMessage {

	public String chatmessage; // The raw chat message
	public String sender;// Will only be set after you apply a regex AND it matched
	public String message; // Will only be set after you apply a regex AND it matched
	public String translationType; // Will be 'custom' if it wasn't a translation packet.
	public String appliedRegexName; // Will only be set after you apply a regex AND it matched

	public ChatMessage() {
		chatmessage = null;
		sender = null;
		message = null;
		translationType = null;
		appliedRegexName = null;
	}

	public static ChatMessage fromChatPacket(ServerChatPacket packet) {
		Message m = packet.getMessage();

		ChatMessage chatmessageobj = new ChatMessage();
		String chatmessage = null;

		String tranlationType = null;

		System.out.println(m.toJsonString());

		if (m instanceof TranslationMessage) { // This SHOULD only fire if it's mostly vanilla chat message
			TranslationMessage tm = (TranslationMessage) m;

			tranlationType = tm.getTranslationKey(); // Used if you want to figure out what type of message (chat, announcement, server, etc...)
			chatmessageobj.translationType = tranlationType;

			System.out.println(tranlationType);

			chatmessage = formatMessageWith(m.toJson().getAsJsonObject());
		} else { // This SHOULD fire for all custom JSON messages from 3rd party servers.
			JsonObject jmessage = m.toJson().getAsJsonObject();
			chatmessageobj.translationType = "custom";
			if (jmessage.has("extra")) {
				StringBuilder sb = new StringBuilder();
				parseExtra(jmessage.get("extra").getAsJsonArray(), sb); // Recursively filter out 'extra' text from the JSON message.
				chatmessage = sb.toString();
			} else if (jmessage.has("text")) {
				chatmessage = jmessage.get("text").getAsString();
			}
		}

		if (chatmessage == null) { // Can be used for debugging. This should never fire
			System.err.println("Chat was null: " + m.toJsonString());
		} else {
			chatmessage = chatmessage.trim();
			if (!chatmessage.equalsIgnoreCase("")) { // Some servers send blank chat messages (For detecting hacked clients and stuff) so you can just ignore them
				chatmessageobj.chatmessage = chatmessage;
			}
		}
		return chatmessageobj;
	}

	private static String formatMessageWith(JsonObject message) {
		if (!message.has("translate"))
			return null;
		String transKey = message.get("translate").getAsString();
		JsonArray withRecur = message.get("with").getAsJsonArray();
		if (Lang.LANG_US.containsKey(transKey)) {
			Object[] with = new Object[withRecur.size()];
			int i = 0;
			for (JsonElement je : withRecur) {
				if (je.isJsonObject()) {
					if (je.getAsJsonObject().has("translate")) {
						with[i] = formatMessageWith(je.getAsJsonObject());
					} else if (je.getAsJsonObject().has("extra")) {
						StringBuilder extras = new StringBuilder();
						parseExtra(je.getAsJsonObject().get("extra").getAsJsonArray(), extras);
						with[i] = extras.toString();
					} else if (je.getAsJsonObject().has("text")) {
						with[i] = je.getAsJsonObject().get("text").getAsString();
					}
					i++;
				}
			}
			if (message.getAsJsonObject().has("color")) {
				return "\u00A7" + Util.ConsoleColor.getId(Util.ConsoleColor.getColorByName(message.getAsJsonObject().get("color").getAsString())) + String.format(Lang.LANG_US.get(transKey), with);
			}
			return String.format(Lang.LANG_US.get(transKey), with);
		}
		return null;
	}

	private static void parseExtra(JsonArray jo, StringBuilder chat) {
		for (JsonElement ex : jo) {
			if (ex.isJsonObject()) {
				if (ex.getAsJsonObject().has("extra"))
					parseExtra(ex.getAsJsonObject().get("extra").getAsJsonArray(), chat);
			}
			if (ex.getAsJsonObject().has("color")) {
				chat.append(Util.ConsoleColor.getColorByName(ex.getAsJsonObject().get("color").getAsString()));
			}
			chat.append(ex.getAsJsonObject().get("text").getAsString());
		}
	}

	/**
	 * Sets the sender and message of custom messages using the pattern provided. <br>
	 * $1 = sender <br>
	 * $2 = message
	 * 
	 * @param p
	 *            The pattern
	 */
	public void applyRegex(String patternName, Pattern p, boolean shouldStrip) {
		Matcher m;
		if (shouldStrip) {
			m = p.matcher(trimColorCodes(chatmessage));
		} else {
			m = p.matcher(chatmessage);
		}
		if (m.find()) {
			sender = m.group(1);
			message = m.group(2);
			appliedRegexName = patternName;
		}
	}

	public void applyRegex(String patternName, Pattern p) {
		applyRegex(patternName, p, true);
	}

	public void applyFirstRegex(Map<String, Pattern> patterns) {
		for (String pname : patterns.keySet()) {
			applyRegex(pname, patterns.get(pname));
			if (isValidMessage())
				return;
		}
	}

	/**
	 * Tests if this message has a valid sender and message
	 */
	public boolean isValidMessage() {
		return (sender != null && message != null);
	}

	public String getStripedChatMessage() {
		return trimColorCodes(chatmessage);
	}

	public String getConsoleFormattedMessage() {
		String consoleMessage = chatmessage;
		consoleMessage.replaceAll("\u00A71", FColor.BLUE.getCode());
		return consoleMessage;
	}

	public String trimColorCodes(String trim) {
		if (trim == null || trim.length() < 0)
			return null;
		StringBuilder trimed = new StringBuilder();
		for (int i = 0; i < trim.toCharArray().length; i++) {
			if (trim.charAt(i) == '\u00A7') {
				i += 1;
			} else {
				trimed.append(trim.charAt(i));
			}
		}
		return trimed.toString();
	}
}
