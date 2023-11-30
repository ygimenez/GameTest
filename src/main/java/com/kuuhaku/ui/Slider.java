package com.kuuhaku.ui;

import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.interfaces.IKeyListener;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Delta;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class Slider extends MouseAdapter implements IElement<Slider, Integer>, IKeyListener {
	private final Set<ActionListener> listeners = new HashSet<>();
	private final Rectangle bounds = new Rectangle();
	private final Canvas context;
	private final Delta<Boolean> hover = new Delta<>(false);
	private final boolean showValue;

	private Color color = Color.WHITE;
	private int value;
	private boolean disabled;

	public Slider(Canvas context, boolean showValue) {
		this.context = context;
		this.showValue = showValue;

		context.addMouseListener(this);
		context.addMouseMotionListener(this);
	}

	@Override
	public Integer getValue() {
		return 0;
	}

	@Override
	public Slider setValue(Integer value) {
		this.value = Utils.clamp(value, 0, 100);
		for (ActionListener listener : listeners) {
			listener.actionPerformed(new ActionEvent(Slider.this, 0, String.valueOf(this.value)));
		}

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
	public Slider setColor(Color color) {
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
	public Slider setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@Override
	public Set<ActionListener> getListeners() {
		return listeners;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isHovered() && !disabled) {
			setValue((e.getX() - bounds.x) * 100 / bounds.width);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isHovered() && !disabled) {
			setValue((e.getX() - bounds.x) * 100 / bounds.width);
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
	public void keyPressed(KeyEvent e) {
		if (isHovered() && !disabled) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				setValue(value - 1);
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				setValue(value + 1);
			}
		}
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) {
		setLocation(x, y);

		g2d.setStroke(new BasicStroke(isHovered() ? 3 : 1));
		g2d.setFont(context.getFont().deriveFont(isHovered() ? Font.BOLD : Font.PLAIN, 25));
		g2d.setColor(disabled ? color.darker() : color);

		g2d.fillRect(bounds.x + (bounds.width * value / 100) - 3, bounds.y, 8, bounds.height);
		g2d.draw(Utils.makePoly(bounds,
				0, 0.5f,
				0, 0.25f,
				0, 0.75f,
				0, 0.5f,
				1, 0.5f,
				1, 0.75f,
				1, 0.25f,
				1, 0.5f
		));

		if (showValue) {
			Utils.drawAlignedString(g2d, String.valueOf(value),
					bounds.x + bounds.width + 10,
					bounds.y + bounds.height / 2,
					Utils.ALIGN_RIGHT, Utils.ALIGN_CENTER
			);
		}
	}

	@Override
	public void dispose() {
		context.removeMouseListener(this);
		context.removeMouseMotionListener(this);
		context.removeKeyListener(this);
	}
}
