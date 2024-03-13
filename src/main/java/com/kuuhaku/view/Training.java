package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Player;
import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.ui.Button;
import com.kuuhaku.ui.Navigator;
import com.kuuhaku.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Training implements IMenu {
	private final Set<Enemy> enemies = new TreeSet<>(Comparator.comparingInt(Enemy::getCost));
	private final Renderer renderer;
	private final Player ship;

	private Button back;
	private final Map<Enemy, Button> buttons = new LinkedHashMap<>();

	public Training(Renderer renderer, Player ship) {
		this.renderer = renderer;
		this.ship = ship;
	}

	@Override
	public void switchTo() {
		back = new Button(renderer)
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

		for (Enemy enemy : enemies) {
			Button btn = new Button(renderer)
					.setSize(100, 100)
					.addListener(e -> Navigator.append(new GameRuntime(renderer, ship.getClass(), enemy.getClass())));

			buttons.put(enemy, btn);
		}

		back.addListener(e -> Navigator.pop());

		renderer.render(g2d -> {
			int i = 0;
			for (Map.Entry<Enemy, Button> e : buttons.entrySet()) {
				Enemy enemy = e.getKey();
				Button btn = e.getValue();

				int y = 10 + btn.getHeight() * (i / 5);
				int x = renderer.getWidth() - (btn.getWidth() + 10) * (1 + i % 5);

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

	@Override
	public void dispose() {
		back.dispose();
		IElement.dispose(buttons.values());
		buttons.clear();
	}
}
