package com.kuuhaku;

import com.kuuhaku.view.GameRuntime;

public class Cooldown {
	private final GameRuntime parent;
	private int time;
	private long lastUse = 0;

	public Cooldown(GameRuntime parent, int time) {
		this.parent = parent;
		this.time = time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean use() {
		long tick = parent.getTick();
		if (tick - lastUse > time) {
			lastUse = tick;
			return true;
		}

		return false;
	}
}
