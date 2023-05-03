package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.enums.InputType;
import com.kuuhaku.enums.ScreenMode;
import com.kuuhaku.enums.ScreenSize;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.manager.SettingsManager;
import com.kuuhaku.ui.ButtonElement;
import com.kuuhaku.ui.ConfigField;
import com.kuuhaku.ui.ToggleElement;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Settings implements IMenu {
	private static final String[] settings = {"pmaster_volume", "peffect_volume", "pmusic_volume", "twindow_mode", "twindow_size", "nframerate"};
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
			String id = s.substring(1);

			ConfigField field = new ConfigField(renderer, id, switch (s.charAt(0)) {
				case 's' -> InputType.TEXT;
				case 'n' -> InputType.NUMERIC;
				case 'p' -> InputType.PERCENT;
				case 't' -> InputType.TOGGLE;
				default -> throw new IllegalStateException();
			});
			field.getField().setSize(200, 40);

			for (String lbl : id.split("_")) {
				field.getLabel().setText(field.getLabel().getText() + " " + Utils.capitalize(lbl));
			}

			if (field.getField() instanceof ToggleElement btn) {
				if (id.equals("window_mode")) {
					btn.setOptions(ScreenMode.modes())
							.addListener(e -> renderer.updateScreenMode(ScreenMode.valueOf(e.getActionCommand().toUpperCase())))
							.setText(SettingsManager.get(id));
				} else if (id.equals("window_size")) {
					btn.setOptions(ScreenSize.sizes(renderer.getDevice()))
							.addListener(e -> {
								renderer.getWindow().setBounds(ScreenSize.valueOf("R_" + e.getActionCommand()).getBounds());
								renderer.getWindow().setLocationRelativeTo(null);
							})
							.setText(SettingsManager.get(id))
							.setDisabled(!SettingsManager.get("window_mode").equalsIgnoreCase(ScreenMode.WINDOWED.name()));
				}
			}

			fields.add(field);
		}

		back.addListener(e -> {
			renderer.updateSettings();
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
				if (field.getId().equals("window_size") && field.getField() instanceof ToggleElement btn) {
					btn.setDisabled(!SettingsManager.get("window_mode").equalsIgnoreCase(ScreenMode.WINDOWED.name()));
				}

				field.render(g2d, renderer.getWidth() / 2 - field.getField().getWidth() / 2, 100 + 50 * i++);
			}

			back.render(g2d, 10, 10);
		});
	}
}
