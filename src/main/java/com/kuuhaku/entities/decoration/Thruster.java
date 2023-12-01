package com.kuuhaku.entities.decoration;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.interfaces.IDynamic;

public class Thruster extends Entity implements IDynamic {
	private int accel = 0;

	public Thruster(Entity parent) {
		super(parent.getRuntime(), parent, new Sprite(parent.getRuntime(), "thruster", 4, 2, 20, true));
		getCoordinates().setPosition(getParent().getWidth() / 2f, 0);
		getCoordinates().setAnchor(0.5f, 1);
	}

	@Override
	public void update() {
		if (accel < 0) {
			getSprite().setTrack(-2);
		} else {
			getSprite().setTrack(accel > 0 ? 1 : 0);
		}
	}

	public void setAccel(int value) {
		this.accel = value;
	}

	@Override
	public boolean isCullable() {
		return false;
	}
}
