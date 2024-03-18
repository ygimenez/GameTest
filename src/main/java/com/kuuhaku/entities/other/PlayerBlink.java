package com.kuuhaku.entities.other;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.utils.Utils;

import java.awt.geom.Point2D;

public class PlayerBlink extends Entity implements IDynamic, ICollide {
	private final Entity source;

	public PlayerBlink(Entity source) {
		super(source.getRuntime(), null, new Sprite(source.getRuntime(), "blink", 5, 1, 10, 0, false));
		this.source = source;

		Point2D.Float pos = source.getGlobalCenter();
		getCoordinates().setPosition(pos.x, pos.y);
	}

	@Override
	public void update() {
		if (getSprite().hasFinished()) dispose();
	}

	@Override
	public boolean hit(Entity other) {
		return false;
	}
}
