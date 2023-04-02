package com.kuuhaku;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.util.concurrent.CompletableFuture;

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

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(val, max));
	}

	public static float toDecibels(float prcnt) {
		return 20 * (float) Math.log10(prcnt);
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

	public static void transition(Clip from, Clip to) {
		CompletableFuture.runAsync(() -> {
			fadeTo(from, 0);
			fadeTo(to, 0.25f);
		});
	}

	public static void fadeTo(Clip clip, float target) {
		FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		target = toDecibels(target);

		if (gain.getValue() > target) {
			while (gain.getValue() > target) {
				try {
					gain.setValue(gain.getValue() - 0.2f);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					break;
				}
			}
		} else {
			while (gain.getValue() < target) {
				try {
					gain.setValue(gain.getValue() + 0.2f);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					break;
				}
			}
		}
	}
}
