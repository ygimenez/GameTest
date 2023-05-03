package com.kuuhaku.utils;

import com.kuuhaku.view.GameRuntime;

public class Cooldown {
	private final GameRuntime parent;
	private int time;
	private long lastUse, pauseOffset;
	private boolean paused;

	public Cooldown(GameRuntime parent, int time) {
		this.parent = parent;
		this.time = time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean use() {
		if (paused || parent.isGameover()) return false;

		long tick = parent.getTick() - pauseOffset;
		if (tick - lastUse > time) {
			lastUse = tick;
			return true;
		}

		return false;
	}

	public void pause() {
		paused = true;
		pauseOffset = parent.getTick();
	}

	public void resume() {
		paused = false;
		pauseOffset = parent.getTick() - pauseOffset;
	}
}
