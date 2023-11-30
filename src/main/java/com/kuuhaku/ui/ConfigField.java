package com.kuuhaku.ui;

import com.kuuhaku.enums.InputType;
import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.manager.SettingsManager;

import java.awt.*;

public class ConfigField {
	private final String id;
	private final IElement<?, ?> field;
	private final Label label;
	private final InputType type;

	private int maxLength = -1;

	public ConfigField(Canvas context, String id, InputType type) {
		this.id = id;
		this.type = type;

		this.field = switch (type) {
			case TOGGLE -> new Toggle(context).setValue(SettingsManager.get(id));
			case PERCENT -> new Slider(context, true).setValue(Integer.parseInt(SettingsManager.get(id)));
			default -> new Input(context)
					.setValidator((oldVal, newVal) -> switch (type) {
						case TEXT -> maxLength == -1 || newVal.length() <= maxLength ? newVal : oldVal;
						case NUMERIC -> {
							if (newVal.isBlank()) {
								yield "0";
							}

							try {
								yield String.valueOf(Integer.parseInt(newVal));
							} catch (NumberFormatException e) {
								yield oldVal;
							}
						}
						default -> throw new IllegalStateException();
					}).setValue(SettingsManager.get(id));
		};

		this.field.addListener(e -> SettingsManager.set(id, e.getActionCommand()));
		this.label = new Label(context, field);
	}

	public String getId() {
		return id;
	}

	public IElement<?, ?> getField() {
		return field;
	}

	public Label getLabel() {
		return label;
	}

	public InputType getType() {
		return type;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void render(Graphics2D g2d, int x, int y) {
		field.render(g2d, x, y);
		label.render(g2d, x, y);
	}

	public void dispose() {
		field.dispose();
		label.dispose();
	}
}
