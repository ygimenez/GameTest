package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.players.Fighter;
import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.ui.Button;
import com.kuuhaku.ui.Navigator;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ShipSelector implements IMenu {
	private final Set<Player> ships = new TreeSet<>(
			Comparator.comparing(p -> p instanceof Fighter).thenComparing(p -> p.getClass().getSimpleName())
	);
	private final Renderer renderer;
	private final boolean training;
	private Player selected;

	private Button back, start;
	private final Map<Player, Button> buttons = new LinkedHashMap<>();

	public ShipSelector(Renderer renderer, boolean training) {
		this.renderer = renderer;
		this.training = training;
	}

	@Override
	public void switchTo() {
		back = new Button(renderer)
				.setSize(150, 50)
				.setValue("BACK");

		start = new Button(renderer)
				.setSize(150, 50)
				.setValue("START");

		GameRuntime gr = new GameRuntime(renderer);
		for (Class<?> klass : Utils.getAnnotatedClasses(Managed.class, "com.kuuhaku.entities.players")) {
			try {
				Player e = (Player) klass.getConstructor(GameRuntime.class).newInstance(gr);
				ships.add(e);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException ex) {
				ex.printStackTrace();
			}
		}

		for (Player ship : ships) {
			Button btn = new Button(renderer)
					.setSize(100, 100)
					.addListener(e -> selected = ship);

			buttons.put(ship, btn);
		}

		back.addListener(e -> Navigator.pop());

		start.addListener(e -> {
			if (training) {
				Navigator.append(new Training(renderer, selected));
			} else {
				Navigator.append(new GameRuntime(renderer, selected.getClass(), null));
			}
		});

		renderer.render(g2d -> {
			int i = 0;
			Fighter baseline = null;

			for (Map.Entry<Player, Button> e : buttons.entrySet()) {
				Player ship = e.getKey();
				if (ship instanceof Fighter f) {
					baseline = f;
				}

				Button btn = e.getValue();
				btn.setHighlighted(ship.equals(selected));

				int y = 10 + btn.getHeight() * (i / 5);
				int x = renderer.getWidth() - (btn.getWidth() + 10) * (1 + i % 5);

				e.getValue().render(g2d, x, y);
				g2d.drawImage(ship.getImage(),
						btn.getX() + btn.getWidth() / 2 - ship.getWidth() / 2,
						btn.getY() + btn.getHeight() / 2 - ship.getHeight() / 2,
						null
				);

				i++;
			}

			if (selected != null) {
				assert baseline != null;

				g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 30));
				g2d.drawString(selected.getClass().getSimpleName(), 10, renderer.getHeight() / 3);

				g2d.setFont(renderer.getFont().deriveFont(Font.PLAIN, 20));
				Utils.drawMultilineString(g2d, selected.getDescription(), 10, renderer.getHeight() / 3 + 30, renderer.getWidth() / 2);

				int armor = (int) (Utils.clamp((float) selected.getBaseHp() / baseline.getBaseHp() / 2, 0.1f, 1) * 10);
				int damage = (int) (Utils.clamp((float) selected.getDamage() / baseline.getDamage() / 2, 0.1f, 1) * 10);
				int bullets = (int) (Utils.clamp((float) selected.getBullets() / baseline.getBullets() / 2, 0.1f, 1) * 10);
				int firerate = (int) (Utils.clamp((float) baseline.getAtkCooldown().getTime() / selected.getAtkCooldown().getTime() / 2, 0.1f, 1) * 10);
				int sprate = (int) (Utils.clamp((float) baseline.getSpCooldown().getTime() / selected.getSpCooldown().getTime() / 2, 0.1f, 1) * 10);
				int speed = (int) (Utils.clamp(selected.getSpeed() / baseline.getSpeed() / 2, 0.1f, 1) * 10);

				g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 20));
				g2d.drawString("Armor:        [" + ("|".repeat(armor) + " ".repeat(10 - armor)) + "]", 10, renderer.getHeight() - 110);
				g2d.drawString("Damage:       [" + ("|".repeat(damage) + " ".repeat(10 - damage)) + "]", 10, renderer.getHeight() - 90);
				g2d.drawString("Bullets:      [" + ("|".repeat(bullets) + " ".repeat(10 - bullets)) + "]", 10, renderer.getHeight() - 70);
				g2d.drawString("Fire rate:    [" + ("|".repeat(firerate) + " ".repeat(10 - firerate)) + "]", 10, renderer.getHeight() - 50);
				g2d.drawString("Special rate: [" + ("|".repeat(sprate) + " ".repeat(10 - sprate)) + "]", 10, renderer.getHeight() - 30);
				g2d.drawString("Speed:        [" + ("|".repeat(speed) + " ".repeat(10 - speed)) + "]", 10, renderer.getHeight() - 10);
			}

			start.setDisabled(selected == null);

			back.render(g2d, 10, 10);
			start.render(g2d, 10, 20 + back.getHeight());
		});
	}

	@Override
	public void dispose() {
		IElement.dispose(back, start);
		IElement.dispose(buttons.values());
		buttons.clear();
	}
}
