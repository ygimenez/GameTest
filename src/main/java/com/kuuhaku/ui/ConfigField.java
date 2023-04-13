package com.kuuhaku.ui;

import com.kuuhaku.enums.InputType;
import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.manager.SettingsManager;
import com.kuuhaku.utils.Utils;

import java.awt.*;

public class ConfigField {
	private final String id;
	private final IElement<?> field;
	private final LabelElement label;
	private final InputType type;

	private int maxLength = -1;

	public ConfigField(Canvas context, String id, InputType type) {
		this.id = id;
		this.type = type;

		if (type == InputType.TOGGLE) {
			this.field = new ToggleElement(context)
					.addListener(e -> SettingsManager.set(id, e.getActionCommand()));
		} else {
			this.field = new InputElement(context)
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
						case PERCENT -> {
							if (newVal.isBlank()) {
								yield "0";
							}

							try {
								int val = Integer.parseInt(newVal);
								yield String.valueOf(Utils.clamp(val, 0, 100));
							} catch (NumberFormatException e) {
								yield oldVal;
							}
						}
						default -> throw new IllegalStateException();
					})
					.addListener(e -> SettingsManager.set(id, e.getActionCommand()))
					.setText(SettingsManager.get(id));
		}

		this.label = new LabelElement(context, field);
	}

	public String getId() {
		return id;
	}

	public IElement<?> getField() {
		return field;
	}

	public LabelElement getLabel() {
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
