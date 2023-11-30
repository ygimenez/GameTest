package com.kuuhaku.ui;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Delta;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.interfaces.IKeyListener;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class Input extends MouseAdapter implements IElement<Input, String>, IKeyListener {
	private final Set<ActionListener> listeners = new HashSet<>();
	private final Rectangle bounds = new Rectangle();
	private final Canvas context;
	private final Delta<Boolean> focused = new Delta<>(false);
	private final Delta<Boolean> hover = new Delta<>(false);

	private Color color = Color.WHITE;
	private String text = "";
	private BiFunction<String, String, String> validator = (a, b) -> b;
	private boolean disabled, blip;
	private int blipTime;

	public Input(Canvas context) {
		this.context = context;
		context.addMouseListener(this);
		context.addMouseMotionListener(this);
	}

	@Override
	public String getValue() {
		return text;
	}

	@Override
	public Input setValue(String value) {
		this.text = validator.apply(this.text, value);
		for (ActionListener listener : listeners) {
			listener.actionPerformed(new ActionEvent(Input.this, 0, this.text));
		}

		return this;
	}

	public Input setValidator(BiFunction<String, String, String> validator) {
		if (validator == null) {
			this.validator = (a, b) -> b;
			return this;
		}

		this.validator = validator;
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
	public Input setColor(Color color) {
		this.color = color;
		return this;
	}

	@Override
	public boolean isHovered() {
		return hover.get();
	}

	public boolean isFocused() {
		return focused.get();
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public Input setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@Override
	public Set<ActionListener> getListeners() {
		return listeners;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		focused.set(hover.get());
		if (focused.changed()) {
			if (focused.get()) {
				context.addKeyListener(this);
			} else {
				context.removeKeyListener(this);
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
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !text.isEmpty()) {
			setValue(text.substring(0, text.length() - 1));
		} else if (!KeyEvent.getKeyText(e.getKeyCode()).isEmpty()) {
			setValue(text + e.getKeyChar());
		}
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) {
		setLocation(x, y);

		g2d.setStroke(new BasicStroke(isFocused() ? 3 : 1));
		g2d.setFont(context.getFont().deriveFont(isFocused() ? Font.BOLD : Font.PLAIN, 25));
		g2d.setColor(disabled ? color.darker() : color);

		if (isFocused()) {
			FontMetrics fm = g2d.getFontMetrics();
			if (blip) {
				int offset = bounds.x + 10 + fm.stringWidth(text);
				g2d.drawLine(offset, bounds.y + 10, offset, bounds.y + bounds.height - 10);
				blipTime++;
			} else {
				blipTime--;
			}

			if (blipTime > 25) {
				blip = false;
			} else if (blipTime < 0) {
				blip = true;
			}
		} else {
			blip = true;
			blipTime = 0;
		}

		g2d.draw(bounds);
		Utils.drawAlignedString(g2d, text,
				bounds.x + 10,
				bounds.y + bounds.height / 2,
				Utils.ALIGN_RIGHT, Utils.ALIGN_CENTER
		);
	}

	@Override
	public void dispose() {
		context.removeMouseListener(this);
		context.removeMouseMotionListener(this);
		context.removeKeyListener(this);
	}
}
