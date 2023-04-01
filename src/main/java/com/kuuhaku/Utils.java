package com.kuuhaku;

import java.awt.*;
import java.awt.font.GlyphVector;

public abstract class Utils {
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_TOP = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;
	public static final int ALIGN_BOTTOM = 2;

	public static boolean between(int val, int min, int max) {
		return val >= min && val <= max;
	}

	public static boolean between(double val, double min, double max) {
		return val >= min && val <= max;
	}

	public static void drawAlignedString(Graphics2D g2d, String str, int x, int y, int alignment) {
		drawAlignedString(g2d, str, x, y, alignment, ALIGN_BOTTOM);
	}

	public static void drawAlignedString(Graphics2D g2d, String str, int x, int y, int alignmentX, int alignmentY) {
		GlyphVector gv = g2d.getFont().createGlyphVector(g2d.getFontRenderContext(), str);
		Rectangle rect = gv.getPixelBounds(null, x, y);

		x = switch (alignmentX) {
			case ALIGN_CENTER -> x - rect.width / 2;
			case ALIGN_RIGHT -> x - rect.width;
			default -> x;
		};

		y = switch (alignmentY) {
			case ALIGN_CENTER -> y + rect.height / 2;
			case ALIGN_BOTTOM -> y + rect.height;
			default -> y;
		};

		g2d.drawString(str, x, y);
	}
}
