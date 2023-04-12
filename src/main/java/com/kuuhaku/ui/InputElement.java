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

public class InputElement extends MouseAdapter implements IElement<InputElement>, IKeyListener {
	private final Set<ActionListener> listeners = new HashSet<>();
	private final Rectangle bounds = new Rectangle();
	private final Canvas context;

	private String text = "";
	private BiFunction<String, String, String> validator = (a, b) -> b;
	private Delta<Boolean> focused = new Delta<>(false);
	private Delta<Boolean> hover = new Delta<>(false);
	private boolean disabled, blip;
	private int blipTime;

	public InputElement(Canvas context) {
		this.context = context;
		context.addMouseListener(this);
		context.addMouseMotionListener(this);
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public InputElement setText(String text) {
		this.text = validator.apply(this.text, text);
		for (ActionListener listener : listeners) {
			listener.actionPerformed(new ActionEvent(InputElement.this, 0, this.text));
		}

		return this;
	}

	public InputElement setValidator(BiFunction<String, String, String> validator) {
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
	public InputElement setDisabled(boolean disabled) {
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
			text = validator.apply(text, text.substring(0, text.length() - 1));
			for (ActionListener listener : listeners) {
				listener.actionPerformed(new ActionEvent(InputElement.this, e.getID(), text));
			}
		} else if (!KeyEvent.getKeyText(e.getKeyCode()).isEmpty()) {
			text = validator.apply(text, text + e.getKeyChar());
			for (ActionListener listener : listeners) {
				listener.actionPerformed(new ActionEvent(InputElement.this, e.getID(), text));
			}
		}
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) {
		g2d.setStroke(new BasicStroke(isFocused() ? 3 : 1));
		g2d.setFont(context.getFont().deriveFont(isFocused() ? Font.BOLD : Font.PLAIN, 25));
		g2d.setColor(disabled ? Color.GRAY : Color.WHITE);

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

		setLocation(x, y);
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
