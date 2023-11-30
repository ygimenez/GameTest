package com.kuuhaku.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Coordinates {
	private final double[] pos;
	private final int[] size;
	private double angle;

	private AffineTransform reference = new AffineTransform();
	protected Shape boundaries;

	public Coordinates() {
		this.pos = new double[2];
		this.size = new int[2];
	}

	public Coordinates(Rectangle bound) {
		this.pos = new double[]{bound.x, bound.y};
		this.size = new int[]{bound.width, bound.height};
		this.boundaries = new Rectangle2D.Double(bound.x, bound.y, bound.width, bound.height);
	}

	public double[] getPosition() {
		return pos;
	}

	public Point2D getCenter() {
		double[] pos = getPosition();
		return new Point2D.Double(pos[0] + getWidth() / 2d, pos[1] + getHeight() / 2d);
	}

	public int getWidth() {
		return size[0];
	}

	public int getHeight() {
		return size[1];
	}

	public void setSize(int width, int height) {
		size[0] = width;
		size[1] = height;
	}

	public void setPosition(double x, double y) {
		pos[0] = x;
		pos[1] = y;
		update();
	}

	public void translate(double dx, double dy) {
		setPosition(pos[0] + dx, pos[1] + dy);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
		update();
	}

	public void rotate(double theta) {
		setAngle(angle + theta);
	}

	private void update() {
		AffineTransform at = (AffineTransform) reference.clone();
		at.rotate(angle, getWidth() / 2d, getHeight() / 2d);
		boundaries = at.createTransformedShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
	}

	public Shape getCollision() {
		return boundaries;
	}

	public boolean intersect(Coordinates other) {
		return intersect(other.boundaries);
	}

	public boolean intersect(Shape other) {
		return boundaries.intersects(other.getBounds2D());
	}

	public AffineTransform getReference() {
		return reference;
	}

	public void setReference(Coordinates reference) {
		this.reference = reference.getReference();
	}
}
