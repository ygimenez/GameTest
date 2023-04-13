package com.kuuhaku.ui;

import com.kuuhaku.interfaces.IElement;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Delta;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToggleElement extends MouseAdapter implements IElement<ToggleElement> {
	private final Set<ActionListener> listeners = new HashSet<>();
	private final Rectangle bounds = new Rectangle();
	private final Canvas context;
	private final Deque<String> options = new ArrayDeque<>();

	private Delta<Boolean> hover = new Delta<>(false);
	private boolean disabled;

	public ToggleElement(Canvas context) {
		this.context = context;
		context.addMouseListener(this);
		context.addMouseMotionListener(this);
	}

	@Override
	public String getText() {
		return options.peek();
	}

	@Override
	public ToggleElement setText(String text) {
		int max = options.size();
		for (int i = 0; i < max && !getText().equalsIgnoreCase(text); i++) {
			options.add(options.pop());
		}

		return this;
	}

	public ToggleElement setOptions(String... options) {
		this.options.clear();
		this.options.addAll(List.of(options));
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

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public ToggleElement setDisabled(boolean disabled) {
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
			options.add(options.pop());
			AssetManager.playCue("menu_click");
			for (ActionListener listener : listeners) {
				listener.actionPerformed(new ActionEvent(ToggleElement.this, e.getID(), getText()));
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
		g2d.setStroke(new BasicStroke(isHovered() ? 3 : 1));
		g2d.setFont(context.getFont().deriveFont(isHovered() ? Font.BOLD : Font.PLAIN, 25));
		g2d.setColor(disabled ? Color.GRAY : Color.WHITE);

		setLocation(x, y);
		g2d.draw(bounds);
		Utils.drawAlignedString(g2d, getText(),
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
