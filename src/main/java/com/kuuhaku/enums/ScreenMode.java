package com.kuuhaku.enums;

import java.util.Arrays;

public enum ScreenMode {
	WINDOWED, BORDERLESS;

	public static String[] modes() {
		return Arrays.stream(values())
				.map(m -> m.name().toLowerCase())
				.toArray(String[]::new);
	}
}
