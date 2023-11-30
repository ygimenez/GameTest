package com.kuuhaku.ui;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Delta;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.interfaces.IElement;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class Button extends MouseAdapter implements IElement<Button, String> {
	private final Set<ActionListener> listeners = new HashSet<>();
	private final Rectangle bounds = new Rectangle();
	private final Canvas context;
	private final Delta<Boolean> hover = new Delta<>(false);

	private Color color = Color.WHITE;
	private String text = "";
	private boolean disabled;

	public Button(Canvas context) {
		this.context = context;
		context.addMouseListener(this);
		context.addMouseMotionListener(this);
	}

	@Override
	public String getValue() {
		return text;
	}

	@Override
	public Button setValue(String value) {
		this.text = value;
		return this;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Button setColor(Color color) {
		this.color = color;
		return this;
	}

	@Override
	public boolean isHovered() {
		return hover.get();
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public Button setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@Override
	public Set<ActionListener> getListeners() {
		return listeners;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isHovered() && !disabled) {
			AssetManager.playCue("menu_click");
			for (ActionListener listener : listeners) {
				listener.actionPerformed(new ActionEvent(Button.this, e.getID(), text));
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		hover.set(!disabled && bounds.contains(e.getPoint()));
		if (hover.get() && hover.changed()) {
			AssetManager.playCue("menu_move");
		}
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) {
		setLocation(x, y);

		g2d.setStroke(new BasicStroke(isHovered() ? 3 : 1));
		g2d.setFont(context.getFont().deriveFont(isHovered() ? Font.BOLD : Font.PLAIN, 25));
		g2d.setColor(disabled ? color.darker() : color);

		g2d.draw(bounds);
		Utils.drawAlignedString(g2d, text,
				bounds.x + bounds.width / 2,
				bounds.y + bounds.height / 2,
				Utils.ALIGN_CENTER, Utils.ALIGN_CENTER
		);
	}

	@Override
	public void dispose() {
		context.removeMouseListener(this);
		context.removeMouseMotionListener(this);
	}
}
