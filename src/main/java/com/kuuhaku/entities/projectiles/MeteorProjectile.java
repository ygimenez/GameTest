package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.interfaces.ITrackable;
import com.kuuhaku.interfaces.Metadata;

@Metadata(sprite = "meteor")
public class MeteorProjectile extends Projectile implements ITrackable {
	private static final int TOP_TO_BOTTOM = 0;
	private static final int BOTTOM_TO_TOP = 1;
	private static final int LEFT_TO_RIGHT = 2;
	private static final int RIGHT_TO_LEFT = 3;

	private final int direction;

	public MeteorProjectile(Entity source, int x, int y, int direction) {
		super(source, 50, 0.5f, 0);
		this.direction = direction;

		getCoordinates().setPosition(x, y);
		getCoordinates().setAnchor(0.5f, 0.5f);
	}

	@Override
	protected void move() {
		getCoordinates().rotate((float) Math.toRadians(getSpeed() / 2));

		switch (direction) {
			case TOP_TO_BOTTOM -> getCoordinates().translate(0, getSpeed());
			case BOTTOM_TO_TOP -> getCoordinates().translate(0, -getSpeed());
			case LEFT_TO_RIGHT -> getCoordinates().translate(getSpeed(), 0);
			case RIGHT_TO_LEFT -> getCoordinates().translate(-getSpeed(), 0);
		}
	}
}
