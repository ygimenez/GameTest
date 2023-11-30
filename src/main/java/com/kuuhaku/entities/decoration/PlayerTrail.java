package com.kuuhaku.entities.decoration;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;

import java.awt.geom.Point2D;

public class PlayerTrail extends Entity implements IDynamic, IParticle {
	private final Player owner;
	private int opacity = 200;

	public PlayerTrail(Player parent, int x, int y) {
		super(parent.getRuntime());
		this.owner = parent;

		Point2D coords = parent.localToGlobal(x, y);
		getBounds().setPosition(coords.getX(), coords.getY());
	}

	@Override
	public void update() {
		getBounds().translate(0, owner.getSpeed());
		if (--opacity == 0) {
			setHp(0);
		}
	}

	@Override
	public int getColor() {
		return (opacity << 24) | 0xFFFFFF;
	}
}
