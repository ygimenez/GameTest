package com.kuuhaku.view;

import com.kuuhaku.ui.ConfigField;
import com.kuuhaku.Renderer;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.enums.InputType;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.ui.ButtonElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Settings implements IMenu {
	private static final String[] settings = {"master_volume", "effect_volume", "music_volume"};
	private final Renderer renderer;

	public Settings(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void switchTo(IMenu from) {
		ButtonElement back = new ButtonElement(renderer)
				.setSize(150, 50)
				.setText("BACK");

		List<ConfigField> fields = new ArrayList<>();
		for (String s : settings) {
			ConfigField field = new ConfigField(renderer, s, InputType.NUMERIC);
			field.getInput().setSize(100, 40);

			for (String lbl : s.split("_")) {
				field.getLabel().setText(field.getLabel().getText() + " " + Utils.capitalize(lbl));
			}

			fields.add(field);
		}

		back.addListener(e -> {
			from.switchTo(null);
			back.dispose();

			for (ConfigField field : fields) {
				field.dispose();
			}
		});

		renderer.render(g2d -> {
			g2d.setColor(Color.BLACK);
			g2d.fill(renderer.getBounds());

			int i = 0;
			for (ConfigField field : fields) {
				field.render(g2d, renderer.getWidth() / 2 - field.getInput().getWidth() / 2, 100 + 50 * i++);
			}

			back.render(g2d, 10, 10);
		});
	}
}
