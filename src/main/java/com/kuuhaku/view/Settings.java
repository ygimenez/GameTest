package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.enums.InputType;
import com.kuuhaku.enums.ScreenMode;
import com.kuuhaku.enums.ScreenSize;
import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.manager.SettingsManager;
import com.kuuhaku.ui.Button;
import com.kuuhaku.ui.ConfigField;
import com.kuuhaku.ui.Navigator;
import com.kuuhaku.ui.Toggle;
import com.kuuhaku.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Settings implements IMenu {
	private static final String[] settings = {"pmaster_volume", "peffect_volume", "pmusic_volume", "twindow_mode", "twindow_size", "nframerate"};
	private final Renderer renderer;

	private Button back;
	private final List<ConfigField> fields = new ArrayList<>();

	public Settings(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void switchTo() {
		back = new Button(renderer)
				.setSize(150, 50)
				.setValue("BACK");

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
				field.getLabel().setValue(field.getLabel().getValue() + " " + Utils.capitalize(lbl));
			}

			if (field.getField() instanceof Toggle btn) {
				if (id.equals("window_mode")) {
					btn.setOptions(ScreenMode.modes())
							.addListener(e -> renderer.updateScreenMode(ScreenMode.valueOf(e.getActionCommand().toUpperCase())))
							.setValue(SettingsManager.get(id));
				} else if (id.equals("window_size")) {
					btn.setOptions(ScreenSize.sizes(renderer.getDevice()))
							.addListener(e -> {
								renderer.getWindow().setBounds(ScreenSize.valueOf("R_" + e.getActionCommand()).getBounds());
								renderer.getWindow().setLocationRelativeTo(null);
							})
							.setValue(SettingsManager.get(id))
							.setDisabled(!SettingsManager.get("window_mode").equalsIgnoreCase(ScreenMode.WINDOWED.name()));
				}
			}

			fields.add(field);
		}

		back.addListener(e -> Navigator.pop());

		renderer.render(g2d -> {
			int i = 0;
			for (ConfigField field : fields) {
				if (field.getId().equals("window_size") && field.getField() instanceof Toggle btn) {
					btn.setDisabled(!SettingsManager.get("window_mode").equalsIgnoreCase(ScreenMode.WINDOWED.name()));
				}

				field.render(g2d, renderer.getWidth() / 2 - field.getField().getWidth() / 2, 100 + 50 * i++);
			}

			back.render(g2d, 10, 10);
		});
	}

	@Override
	public void dispose() {
		renderer.updateSettings();
		back.dispose();
		for (ConfigField field : fields) {
			field.dispose();
		}

		fields.clear();
	}
}
