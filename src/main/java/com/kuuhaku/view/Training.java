package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.ui.Button;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Training implements IMenu {
	private final Set<Enemy> enemies = new TreeSet<>(Comparator.comparingInt(Enemy::getCost));
	private final Renderer renderer;

	public Training(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void switchTo(IMenu from) {
		Button back = new Button(renderer)
				.setSize(150, 50)
				.setValue("BACK");

		GameRuntime gr = new GameRuntime(renderer);
		for (Class<?> klass : Utils.getAnnotatedClasses(Managed.class, "com.kuuhaku.entities.enemies")) {
			try {
				Enemy e = (Enemy) klass.getConstructor(GameRuntime.class).newInstance(gr);
				enemies.add(e);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException ex) {
				ex.printStackTrace();
			}
		}

		Map<Enemy, Button> buttons = new LinkedHashMap<>();
		for (Enemy enemy : enemies) {
			Button btn = new Button(renderer)
					.setSize(100, 100)
					.addListener(e -> {
						new GameRuntime(renderer, enemy.getClass()).switchTo(from);
						back.dispose();

						for (Button b : buttons.values()) {
							b.dispose();
						}
					});

			buttons.put(enemy, btn);
		}

		back.addListener(e -> {
			from.switchTo(null);
			back.dispose();

			for (Button btn : buttons.values()) {
				btn.dispose();
			}
		});

		renderer.render(g2d -> {
			g2d.setColor(Color.BLACK);
			g2d.fill(renderer.getBounds());

			int i = 0;
			for (Map.Entry<Enemy, Button> e : buttons.entrySet()) {
				Enemy enemy = e.getKey();
				Button btn = e.getValue();

				int y = 100 + 210 * (i / 6);
				int sections = Math.min(buttons.size() - 6 * (i / 6), 6);

				int gap = (renderer.getWidth() - 40) / sections;
				int x = 20 + gap * (i % 6) + (gap / 2 - btn.getWidth() / 2);

				e.getValue().render(g2d, x, y);
				g2d.drawImage(enemy.getImage(),
						btn.getX() + btn.getWidth() / 2 - enemy.getWidth() / 2,
						btn.getY() + btn.getHeight() / 2 - enemy.getHeight() / 2,
						null
				);

				i++;
			}

			back.render(g2d, 10, 10);
		});
	}
}
