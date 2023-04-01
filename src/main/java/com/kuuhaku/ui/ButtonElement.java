package com.kuuhaku.ui;

import com.kuuhaku.AssetManager;
import com.kuuhaku.Utils;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class ButtonElement extends MouseAdapter {
	private final Set<ActionListener> listeners = new HashSet<>();
	private final Rectangle bounds = new Rectangle();
	private final Canvas context;

	private String text;
	private boolean hover, changed, disabled;

	public ButtonElement(Canvas context) {
		this.context = context;
		context.addMouseListener(this);
		context.addMouseMotionListener(this);
	}

	public String getText() {
		return text;
	}

	public ButtonElement setText(String text) {
		this.text = text;
		return this;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public int getWidth() {
		return bounds.width;
	}

	public int getHeight() {
		return bounds.height;
	}

	public ButtonElement setSize(int width, int height) {
		bounds.setSize(width, height);
		return this;
	}

	public int getX() {
		return bounds.x;
	}

	public int getY() {
		return bounds.y;
	}

	public ButtonElement setLocation(int x, int y) {
		bounds.setLocation(x, y);
		return this;
	}

	public boolean isHovered() {
		return hover;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public ButtonElement setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	public Set<ActionListener> getListeners() {
		return listeners;
	}

	public ButtonElement addListener(ActionListener evt) {
		listeners.add(evt);
		return this;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (hover) {
			if (disabled) {
				AssetManager.playCue("menu_invalid");
			} else {
				AssetManager.playCue("menu_click");
				for (ActionListener listener : listeners) {
					listener.actionPerformed(new ActionEvent(ButtonElement.this, e.getID(), null));
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		hover = bounds.contains(e.getPoint());
		if (hover && !disabled && !changed) {
			AssetManager.playCue("menu_move");
			changed = true;
		}

		if (!hover) {
			changed = false;
		}
	}

	public void render(Graphics2D g2d, int x, int y) {
		g2d.setStroke(new BasicStroke(hover && !disabled ? 3 : 1));
		g2d.setFont(context.getFont().deriveFont(hover && !disabled ? Font.BOLD : Font.PLAIN, 25));
		g2d.setColor(disabled ? Color.GRAY : Color.WHITE);

		setLocation(x, y);
		g2d.draw(bounds);
		Utils.drawAlignedString(g2d, text,
				bounds.x + bounds.width / 2,
				bounds.y + bounds.height / 2,
				Utils.ALIGN_CENTER, Utils.ALIGN_CENTER
		);
	}

	public void dispose() {
		context.removeMouseListener(this);
		context.removeMouseMotionListener(this);
	}
}
