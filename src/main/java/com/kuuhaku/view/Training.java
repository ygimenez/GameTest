package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.ui.ButtonElement;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Training implements IMenu {
	private final Set<Enemy> enemies = new TreeSet<>(Comparator.comparingInt(Enemy::getPoints));
	private final Renderer renderer;

	public Training(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void switchTo(IMenu from) {
		ButtonElement back = new ButtonElement(renderer)
				.setSize(150, 50)
				.setText("BACK");

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

		Map<Enemy, ButtonElement> buttons = new LinkedHashMap<>();
		for (Enemy enemy : enemies) {
			ButtonElement btn = new ButtonElement(renderer)
					.setSize(100, 100)
					.addListener(e -> {
						new GameRuntime(renderer, enemy.getClass()).switchTo(this);
						back.dispose();

						for (ButtonElement b : buttons.values()) {
							b.dispose();
						}
					});

			buttons.put(enemy, btn);
		}

		back.addListener(e -> {
			from.switchTo(null);
			back.dispose();

			for (ButtonElement btn : buttons.values()) {
				btn.dispose();
			}
		});

		renderer.render(g2d -> {
			g2d.setColor(Color.BLACK);
			g2d.fill(renderer.getBounds());

			int i = 0;
			for (Map.Entry<Enemy, ButtonElement> e : buttons.entrySet()) {
				int y = 100 + 210 * (i / 6);
				int sections = Math.min(buttons.size() - 6 * y, 6);

				int gap = (renderer.getWidth() - 40) / sections;
				int x = 20 + gap * (i % 6) + (gap / 2 - e.getKey().getWidth() / 2);

				g2d.drawImage(e.getKey().getSprite(), x, y, null);
				e.getValue().setLocation(x, y);

				i++;
			}

			back.render(g2d, 10, 10);
		});
	}
}
