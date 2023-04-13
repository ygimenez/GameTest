package com.kuuhaku.enums;

import java.awt.*;
import java.util.Arrays;

public enum ScreenSize {
	R_800x600(new Rectangle(800, 600)),
	R_1024x768(new Rectangle(1024, 768)),
	R_1280x720(new Rectangle(1280, 720)),
	R_1280x800(new Rectangle(1280, 800)),
	R_1366x768(new Rectangle(1366, 768)),
	R_1440x900(new Rectangle(1440, 900)),
	R_1680x1050(new Rectangle(1680, 1050)),
	R_1920x1080(new Rectangle(1920, 1080));

	private final Rectangle bounds;

	ScreenSize(Rectangle bounds) {
		this.bounds = bounds;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public static String[] sizes(GraphicsDevice screen) {
		return Arrays.stream(values())
				.map(m -> m.name().substring(2).toLowerCase())
				.toArray(String[]::new);
	}
}
