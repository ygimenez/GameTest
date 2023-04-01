package com.kuuhaku;

public class Game {
	private final Renderer renderer = new Renderer();
	private final GameRuntime runtime = new GameRuntime(renderer, 60);

	public Game() {
		runtime.run();
	}
}
