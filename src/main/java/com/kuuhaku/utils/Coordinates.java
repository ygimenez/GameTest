package com.kuuhaku.utils;

import java.awt.*;

public class Coordinates {
	private double x, y;
	private int width, height;
	private double angle = 0;

	public Coordinates() {
	}

	public Coordinates(Rectangle bound) {
		x = bound.x;
		y = bound.y;
		width = bound.width;
		height = bound.height;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getAngle() {
		return angle;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void translate(double dx, double dy) {
		x += dx;
		y += dy;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public void rotate(double theta) {
		angle += theta;
	}

	public boolean intersect(Coordinates other) {
		return intersect(other.x, other.width, other.y, other.height);
	}

	public boolean intersect(Rectangle other) {
		return intersect(other.x, other.width, other.y, other.height);
	}

	private boolean intersect(double x, int width, double y, int height) {
		if (this.width == 0 || this.height == 0) return false;
		else if (this.x > x + width || x > this.x + this.width) return false;
		else if (this.y > y + height || y > this.y + this.height) return false;

		return true;
	}
}
