package com.kuuhaku.manager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

public abstract class SettingsManager {
	private static final Properties props = new Properties();
	private static final File file;

	static {
		file = new File("config.cfg");
		try {
			if (file.exists() || file.createNewFile()) {
				props.load(Files.newInputStream(file.toPath()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		return get(key, "");
	}

	public static String get(String key, String defaultValue) {
		String prop = props.getProperty(key);
		if (prop == null) {
			set(key, defaultValue);
			return get(key, defaultValue);
		}

		return prop;
	}

	public static void set(String key, Object value) {
		props.setProperty(key, String.valueOf(value));

		try (OutputStream os = Files.newOutputStream(file.toPath())) {
			props.store(os, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
