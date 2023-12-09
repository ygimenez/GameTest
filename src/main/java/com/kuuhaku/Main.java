package com.kuuhaku;

import com.kuuhaku.view.Game;

import java.io.File;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		Game game = new Game();
		game.switchTo(null);
	}
}
