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
		super(parent.getRuntime(), null);
		this.owner = parent;

		Point2D.Float coords = parent.toLocal(x, y);
		getCoordinates().setPosition(coords.x, coords.y);
	}

	@Override
	public void update() {
		getCoordinates().translate(0, owner.getSpeed());
		if ((opacity -= 2) == 0) {
			dispose();
		}
	}

	@Override
	public int getColor() {
		return (opacity << 24) | (getRuntime().getForeground().getRGB() & 0xFFFFFF);
	}
}
