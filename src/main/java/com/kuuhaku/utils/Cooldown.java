package com.kuuhaku.utils;

import com.kuuhaku.view.GameRuntime;

public class Cooldown {
	private final GameRuntime runtime;
	private int time;
	private long lastUse, pauseOffset;
	private boolean paused;

	public Cooldown(GameRuntime runtime, int time) {
		this.runtime = runtime;
		this.time = runtime.millisToTick(time);
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean use() {
		if (paused || runtime.isGameover()) return false;

		long tick = runtime.getTick() - pauseOffset;
		if (tick - lastUse > time) {
			lastUse = tick;
			return true;
		}

		return false;
	}

	public void pause() {
		paused = true;
		pauseOffset = runtime.getTick();
	}

	public void resume() {
		paused = false;
		pauseOffset = runtime.getTick() - pauseOffset;
	}
}
