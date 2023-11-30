package com.kuuhaku.ui;

import com.kuuhaku.utils.Utils;
import com.kuuhaku.interfaces.IElement;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Set;

public class Label implements IElement<Label, String> {
	private final IElement<?, ?> parent;
	private final Canvas context;

	private Color color = Color.WHITE;
	private String text = "";

	public Label(Canvas context, IElement<?, ?> parent) {
		this.parent = parent;
		this.context = context;
	}

	@Override
	public String getValue() {
		return text;
	}

	@Override
	public Label setValue(String value) {
		this.text = value;
		return this;
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Label setColor(Color color) {
		this.color = color;
		return this;
	}

	@Override
	public boolean isHovered() {
		return false;
	}

	@Override
	public boolean isDisabled() {
		return parent == null || parent.isDisabled();
	}

	@Override
	public Label setDisabled(boolean disabled) {
		return this;
	}

	@Override
	public Set<ActionListener> getListeners() {
		return null;
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) {
		g2d.setStroke(new BasicStroke(isHovered() ? 3 : 1));
		g2d.setFont(context.getFont().deriveFont(isHovered() ? Font.BOLD : Font.PLAIN, 25));
		g2d.setColor(isDisabled() ? Color.GRAY : Color.WHITE);

		if (parent == null) {
			g2d.drawString(text, x, y);
		} else {
			Utils.drawAlignedString(g2d, text,
					parent.getX() - 10,
					parent.getY() + parent.getHeight() / 2,
					Utils.ALIGN_LEFT, Utils.ALIGN_CENTER
			);
		}
	}
}
