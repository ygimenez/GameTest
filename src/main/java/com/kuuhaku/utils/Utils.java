package com.kuuhaku.utils;

import com.kuuhaku.enums.SoundType;
import com.kuuhaku.view.GameRuntime;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(val, max));
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(val, max));
	}

	public static float toDecibels(float prcnt) {
		return 20 * (float) Math.log10(prcnt);
	}

	public static float toDecibels(SoundType type, float prcnt) {
		return 20 * (float) Math.log10(prcnt * type.getVolume());
	}

	public static void drawAlignedString(Graphics2D g2d, String str, int x, int y, int alignment) {
		drawAlignedString(g2d, str, x, y, alignment, ALIGN_BOTTOM);
	}

	public static void drawAlignedString(Graphics2D g2d, String str, int x, int y, int alignmentX, int alignmentY) {
		FontMetrics fm = g2d.getFontMetrics();

		x = switch (alignmentX) {
			case ALIGN_CENTER -> x - fm.stringWidth(str) / 2;
			case ALIGN_LEFT -> x - fm.stringWidth(str);
			default -> x;
		};

		y = switch (alignmentY) {
			case ALIGN_CENTER -> y + fm.getHeight() / 4;
			case ALIGN_BOTTOM -> y + fm.getHeight() / 2;
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
		target = toDecibels(SoundType.MASTER, target);

		if (gain.getValue() > target) {
			while (gain.getValue() > target) {
				try {
					gain.setValue(gain.getValue() - 0.2f);
					sleep(10);
				} catch (IllegalArgumentException e) {
					break;
				}
			}
		} else {
			while (gain.getValue() < target) {
				try {
					gain.setValue(gain.getValue() + 0.2f);
					sleep(10);
				} catch (IllegalArgumentException e) {
					break;
				}
			}
		}
	}

	public static List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation, String packageName) {
		List<Class<?>> out = new ArrayList<>();
		List<Class<?>> classes = getClasses(packageName);

		for (Class<?> clazz : classes) {
			if (clazz.isAnnotationPresent(annotation)) {
				out.add(clazz);
			}
		}

		return out;
	}

	public static List<Class<?>> getClasses(String packageName) {
		List<Class<?>> out = new ArrayList<>();
		String path = packageName.replace(".", "/");
		ClassLoader classLoader = Utils.class.getClassLoader();

		try {
			for (URL resource : Collections.list(classLoader.getResources(path))) {
				File file = new File(resource.toURI());

				if (file.isDirectory()) {
					out.addAll(findClasses(packageName, file));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	private static List<Class<?>> findClasses(String packageName, File directory) throws ClassNotFoundException {
		List<Class<?>> out = new ArrayList<>();

		if (!directory.exists()) {
			return out;
		}

		File[] files = directory.listFiles();
		if (files == null) return List.of();

		for (File file : files) {
			if (file.isDirectory()) {
				out.addAll(findClasses(packageName + "." + file.getName(), file));
			} else if (file.getName().endsWith(".class")) {
				String className = file.getName().substring(0, file.getName().length() - 6);
				out.add(Class.forName(packageName + "." + className));
			}
		}

		return out;
	}

	public static void sleep(long millis) {
		sleep(millis, 0);
	}

	public static void sleep(long millis, int nanos) {
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException ignore) {
		}
	}

	public static void await(GameRuntime runtime, long ticks) {
		long target = runtime.getTick() + ticks;
		while (runtime.getTick() < target) {
			sleep(10);
		}
	}

	public static String capitalize(String str) {
		if (str.isBlank()) {
			return str;
		} else if (str.length() < 2) {
			return str.toUpperCase();
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static double vecToAng(Point2D a, Point2D b) {
		return Math.toDegrees(Math.atan2(a.getY() - b.getY(), a.getX() - b.getX()));
	}
}
