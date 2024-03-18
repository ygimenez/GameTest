package com.kuuhaku;

import com.kuuhaku.ui.Navigator;
import com.kuuhaku.view.Game;

public class Main {
	public static final String VERSION = "0.0.3-ALPHA";

	public static void main(String[] args) {
		Navigator.append(new Game());
	}
}
