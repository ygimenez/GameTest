package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.Metadata;

import java.awt.geom.Point2D;

@Metadata(sprite = "blast")
public class TorpedoBlast extends Projectile {
	private final PlayerTorpedo source;
	private int time = getRuntime().millisToTick(75);

	public TorpedoBlast(PlayerTorpedo source, float angle) {
		super(source.getOwner(), 0, source.getSpeed() * 2, angle);
		this.source = source;

		Point2D.Float center = source.getGlobalCenter();
		getCoordinates().setPosition(center.x, center.y);
		getCoordinates().setAnchor(0.5f, 0.5f);
	}

	@Override
	public void update() {
		move();

		if (--time <= 0) dispose();
		else for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof IDamageable d && hit(entity)) {
				d.damage(source.getOwner().getDamage() / 20);
				break;
			}
		}
	}
}
