package com.kuuhaku.utils;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.enums.SoundType;
import com.kuuhaku.view.GameRuntime;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;
import java.util.stream.DoubleStream;

public abstract class Utils {
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_TOP = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;
	public static final int ALIGN_BOTTOM = 2;

	public static final double[] SIN = new double[360];
	public static final double[] COS = new double[360];

	static {
		for (int i = 0; i < 360; i++) {
			SIN[i] = Math.sin(Math.toRadians(i));
			COS[i] = Math.cos(Math.toRadians(i));
		}
	}

	public static boolean between(int val, int min, int max) {
		return val >= min && val <= max;
	}

	public static boolean between(double val, double min, double max) {
		return val >= min && val <= max;
	}

	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(val, max));
	}

	public static double clamp(double val, double min, double max) {
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
			sleep(runtime.tickToMillis(1));
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

	public static Polygon makePoly(int... xy) {
		if (xy.length % 2 != 0) throw new IllegalArgumentException("Supplied coordinate count must be even.");

		Polygon poly = new Polygon();
		for (int i = 0; i < xy.length; i += 2) {
			poly.addPoint(xy[i], xy[i + 1]);
		}

		return poly;
	}

	public static Polygon makePoly(Rectangle bounds, double... xy) {
		AtomicInteger i = new AtomicInteger();
		Polygon out = makePoly(DoubleStream.of(xy)
				.mapToInt(d -> (int) ((i.getAndIncrement() % 2 == 0 ? bounds.width : bounds.height) * d))
				.toArray());

		out.translate(bounds.x, bounds.y);
		return out;
	}

	public static double fsin(double rad) {
		int ang = (int) (Math.toDegrees(rad) % 360);
		if (ang < 0) ang = 360 + ang;

		return SIN[ang];
	}

	public static double fcos(double rad) {
		int ang = (int) (Math.toDegrees(rad) % 360);
		if (ang < 0) ang = 360 + ang;

		return COS[ang];
	}

	public static double[] angToVec(double angle) {
		angle -= Math.toRadians(90);
		return new double[]{-fcos(angle), -fsin(angle)};
	}

	public static RandomGenerator rng() {
		return ThreadLocalRandom.current();
	}

	public static double angBetween(Entity a, Entity b) {
		Point2D center = a.getCenter();
		Point2D target = b.getCenter();

		double dx = target.getX() - center.getX();
		double dy = target.getY() - center.getY();
		double deg = (Math.toDegrees(Math.atan2(dy, dx)) + 270) % 360;

		return deg;
	}

	public static int direction(double value) {
		if (value < 0) return -1;
		else if (value > 0) return 1;
		return 0;
	}

	public static double bezier(double t) {
		double abs = Math.abs(t);
		return abs * abs * (3.0 - 2.0 * abs) * direction(t);
	}

	public static double round(double value, int precision) {
		return BigDecimal.valueOf(value)
				.setScale(precision, RoundingMode.HALF_EVEN)
				.doubleValue();
	}
}
