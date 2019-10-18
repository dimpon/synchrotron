package org.synchrotron.core;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AnsiColor {

	DARK_BLACK("\u001b[30m"),
	DARK_RED("\u001b[31m"),
	DARK_GREEN("\u001b[32m"),
	DARK_YELLOW("\u001b[33m"),
	DARK_BLUE("\u001b[34m"),
	DARK_MAGENTA("\u001b[35m"),
	DARK_CYAN("\u001b[36m"),
	DARK_WHITE("\u001b[37m"),

	BRIGHT_BLACK("\u001b[30;1m"),
	BRIGHT_RED("\u001b[31;1m"),
	BRIGHT_GREEN("\u001b[32;1m"),
	BRIGHT_YELLOW("\u001b[33;1m"),
	BRIGHT_BLUE("\u001b[34;1m"),
	BRIGHT_MAGENTA("\u001b[35;1m"),
	BRIGHT_CYAN("\u001b[36;1m"),
	BRIGHT_WHITE("\u001b[37;1m"),
	RESET("\u001b[0m");

	final String color;

	@Override
	public String toString() {
		return color;
	}
}
