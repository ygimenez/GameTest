package com.kuuhaku.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Coordinates {
	private final AffineTransform reference = new AffineTransform();

	private final float[] pos;
	private final int[] size;
	private final float[] anchor = {0.5f, 0.5f};
	private float angle;

	private Shape boundaries;
	private AffineTransform parent = new AffineTransform();

	public Coordinates() {
		this.pos = new float[2];
		this.size = new int[2];
		this.boundaries = new Rectangle2D.Float(0, 0, 1, 1);
	}

	public Coordinates(Rectangle bound) {
		this.pos = new float[]{bound.x, bound.y};
		this.size = new int[]{bound.width, bound.height};
		this.boundaries = new Rectangle2D.Float(bound.x, bound.y, bound.width, bound.height);
	}

	public float[] getPosition() {
		return pos;
	}

	public void setPosition(float x, float y) {
		pos[0] = x;
		pos[1] = y;
	}

	public void setPosition(Coordinates coords) {
		pos[0] = coords.pos[0];
		pos[1] = coords.pos[1];
	}

	public void translate(float dx, float dy) {
		setPosition(pos[0] + dx, pos[1] + dy);
	}

	public Point2D.Float getCenter() {
		return new Point2D.Float(pos[0] - (size[0] * anchor[0]) + getWidth() / 2f, pos[1] - (size[1] * anchor[1]) + getHeight() / 2f);
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

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void rotate(float theta) {
		setAngle(angle + theta);
	}

	public float[] getAnchor() {
		return new float[] {
				size[0] * anchor[0],
				size[1] * anchor[1]
		};
	}

	public void setAnchor(float x, float y) {
		anchor[0] = x;
		anchor[1] = y;
	}

	public AffineTransform getTransform() {
		reference.setTransform(parent);
		reference.translate(pos[0] - (size[0] * anchor[0]), pos[1] - (size[1] * anchor[1]));
		reference.rotate(this.angle, (size[0] * anchor[0]), (size[1] * anchor[1]));

		return reference;
	}

	public void update() {
		boundaries = getTransform().createTransformedShape(new Rectangle2D.Float(
				0, 0, size[0], size[1]
		));
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

	public void setParent(Coordinates parent) {
		if (parent == null) {
			AffineTransform trans = getTransform();
			setPosition((float) trans.getTranslateX(), (float) trans.getTranslateY());

			this.parent = new AffineTransform();
		} else {
			this.parent = parent.reference;
		}
	}
}
