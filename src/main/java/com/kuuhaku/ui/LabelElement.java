package com.kuuhaku.ui;

import com.kuuhaku.utils.Utils;
import com.kuuhaku.interfaces.IElement;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Set;

public class LabelElement implements IElement<LabelElement> {
	private final IElement<?> parent;
	private final Canvas context;

	private String text = "";

	public LabelElement(Canvas context, IElement<?> parent) {
		this.parent = parent;
		this.context = context;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public LabelElement setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public Rectangle getBounds() {
		return null;
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
	public LabelElement setDisabled(boolean disabled) {
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
