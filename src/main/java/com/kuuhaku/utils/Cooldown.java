package com.kuuhaku.utils;

import com.kuuhaku.view.GameRuntime;

public class Cooldown {
	private final GameRuntime runtime;
	private int time, offset;
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

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public float getCharge() {
		long tick = runtime.getTick() - pauseOffset;
		return Utils.clamp((float) (tick - lastUse) / (time + offset), 0, 1);
	}

	public boolean use() {
		if (paused || runtime.isGameover()) return false;

		long tick = runtime.getTick() - pauseOffset;
		if (tick - lastUse > time + offset) {
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

	public void spend() {
		lastUse = runtime.getTick() - pauseOffset;
	}

	public void reset() {
		lastUse = 0;
	}
}
