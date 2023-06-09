package com.kuuhaku.interfaces;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Set;

public interface IElement<T extends IElement<T>> {
	String getText();

	T setText(String text);

	Rectangle getBounds();

	default int getWidth() {
		return getBounds().width;
	}

	default int getHeight() {
		return getBounds().height;
	}

	default T setSize(int width, int height) {
		getBounds().setSize(width, height);
		return (T) this;
	}

	default int getX() {
		return getBounds().x;
	}

	default int getY() {
		return getBounds().y;
	}

	default T setLocation(int x, int y) {
		getBounds().setLocation(x, y);
		return (T) this;
	}

	boolean isHovered();

	boolean isDisabled();

	T setDisabled(boolean disabled);

	Set<ActionListener> getListeners();

	default T addListener(ActionListener evt) {
		getListeners().add(evt);
		return (T) this;
	}

	void render(Graphics2D g2d, int x, int y);

	default void dispose() {
	}
}
