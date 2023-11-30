package com.kuuhaku.utils;

import com.kuuhaku.view.GameRuntime;

public class Interp {
	private final GameRuntime runtime;
	private final int from, to, duration, loops;
	private long start;
	private int looped;
	private boolean stopped = true;

	public Interp(GameRuntime runtime, int from, int to, int duration, int loops) {
		this.runtime = runtime;
		this.from = from;
		this.to = to;
		this.duration = duration;
		this.loops = loops;
		this.start = runtime.getTick();
	}

	public void start() {
		start = runtime.getTick();
		looped = 0;
		stopped = false;
	}

	public int get() {
		long tick = runtime.getTick();
		if (tick > start + duration) {
			if (loops < 0 || loops > ++looped) {
				start = runtime.getTick();
			} else {
				stopped = true;
				return to;
			}
		}

		return from + Utils.clamp((int) ((tick - start) * (to - from) / duration), 0, to - from);
	}

	public boolean isStopped() {
		return stopped;
	}
}
