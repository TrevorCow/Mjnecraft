package com.trevorcow.mjnecraft.util;

import java.util.Random;

import com.trevorcow.mjnecraft.util.Util.ConsoleColor;

public class Util {

	private static final String RANDOM_CHARS = "`1234567890-=qwertyuiop[]\\asdfghjkl;'zxcvbnm,./~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?";
	private static final Random RANDOM = new Random();

	public static <T> String stringFromArray(T[] array, String dilimiter) {
		StringBuilder sb = new StringBuilder();
		for (T t : array) {
			sb.append(t.toString() + dilimiter);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public static String formatForConsole(String toFormat) {
		String formatted = "";
		boolean obfuscated = false;
		char[] toFormatChars = toFormat.toCharArray();
		for (int i = 0; i < toFormatChars.length; i++) {
			if (toFormatChars[i] == '\u00A7') {
				i++;
				ConsoleColor color = ConsoleColor.getById(toFormatChars[i]);
				if (color.shouldUseChar()) {
					obfuscated = false;
					formatted += color.toString();
				} else {
					if (color.equals(ConsoleColor.OBFUSCATED)) {
						obfuscated = true;
					}
				}
				continue;
			}
			if (obfuscated)
				formatted += getRandomChar();
			else
				formatted += toFormatChars[i];
		}
		formatted += ConsoleColor.RESET;
		return formatted;
	}

	public static String getRandomChar() {
		return RANDOM_CHARS.charAt(RANDOM.nextInt(RANDOM_CHARS.length())) + "";

	}

	public static enum ConsoleColor {

		BLACK("30"), // Colors
		DARK_BLUE("34"), //
		DARK_GREEN("32"), //
		DARK_AQUA("36"), //
		DARK_RED("31"), //
		DARK_PURPLE("35"), //
		GOLD("33"), //
		GRAY("37"), //
		DARK_GRAY("90"), //
		BLUE("94"), //
		GREEN("92"), //
		AQUA("96"), //
		RED("91"), //
		LIGHT_PURPLE("95"), //
		YELLOW("93"), //
		WHITE("97"),

		OBFUSCATED("", false), //
		BOLD("1"), //
		STRIKETHROUGH("9"), //
		UNDERLINE("4"), //
		ITALIC("3"), //
		RESET("0"), //
		;

		public static final String PREFIX = "\033[";
		public static final String SUFFIX = "m";
		private String code;
		private boolean useChar;

		private ConsoleColor(String code, boolean useChar) {
			this.code = code;
			this.useChar = useChar;
		}

		private ConsoleColor(String code) {
			this(code, true);
		}

		public boolean shouldUseChar() {
			return useChar;
		}

		public static ConsoleColor getById(char id) {
			switch (id) {
			case '0':
				return ConsoleColor.BLACK;
			case '1':
				return ConsoleColor.DARK_BLUE;
			case '2':
				return ConsoleColor.DARK_GREEN;
			case '3':
				return ConsoleColor.DARK_AQUA;
			case '4':
				return ConsoleColor.DARK_RED;
			case '5':
				return ConsoleColor.DARK_PURPLE;
			case '6':
				return ConsoleColor.GOLD;
			case '7':
				return ConsoleColor.GRAY;
			case '8':
				return ConsoleColor.DARK_GRAY;
			case '9':
				return ConsoleColor.BLUE;
			case 'a':
				return ConsoleColor.GREEN;
			case 'b':
				return ConsoleColor.AQUA;
			case 'c':
				return ConsoleColor.RED;
			case 'd':
				return ConsoleColor.LIGHT_PURPLE;
			case 'e':
				return ConsoleColor.YELLOW;
			case 'f':
				return ConsoleColor.WHITE;
			case 'k':
				return ConsoleColor.OBFUSCATED;
			case 'l':
				return ConsoleColor.BOLD;
			case 'm':
				return ConsoleColor.STRIKETHROUGH;
			case 'n':
				return ConsoleColor.UNDERLINE;
			case 'o':
				return ConsoleColor.ITALIC;
			default:
				return ConsoleColor.RESET;
			}
		}

		@Override
		public String toString() {
			return PREFIX + code + ";" + SUFFIX;
		}
	}
}
