package com.kuuhaku;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public abstract class LeaderboardsManager {
	private static final Set<Map.Entry<String, Integer>> scores = new TreeSet<>(Map.Entry.comparingByValue(Comparator.reverseOrder()));

	static {
		try {
			File file = new File("leaderboards.csv");
			if (file.exists()) {
				List<String> lines = Files.readAllLines(file.toPath());
				for (String line : lines) {
					try {
						scores.add(Map.entry(line.substring(0, 3), Integer.parseInt(line.split(",")[1])));
					} catch (NumberFormatException ignore) {
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<Map.Entry<String, Integer>> getScores() {
		return scores;
	}

	public static void addScore(String player, int score) {
		scores.add(Map.entry(player.toUpperCase().substring(0, 3), score));
	}

	public static void saveScores() {
		try {
			File file = new File("leaderboards.csv");
			if (file.exists() || file.createNewFile()) {
				Files.writeString(file.toPath(),
						scores.stream()
								.map(e -> e.getKey() + "," + e.getValue())
								.collect(Collectors.joining("\n"))
				);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
