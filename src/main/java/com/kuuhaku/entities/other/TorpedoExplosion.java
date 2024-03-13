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

public class TorpedoExplosion extends Entity implements IDynamic, ICollide {
	private final Projectile source;

	public TorpedoExplosion(Projectile source) {
		super(source.getRuntime(), null, new Sprite(source.getRuntime(), "artillery", 8, 2, 15, 8, false));
		this.source = source;

		Point2D.Float pos = source.getGlobalCenter();
		getCoordinates().setPosition(pos.x, pos.y);
	}

	@Override
	public void update() {
		if (getSprite().getFrame() == 15) dispose();
		else {
			for (Entity entity : getRuntime().getEntities()) {
				if (entity instanceof IDamageable d && hit(entity)) {
					d.damage(source.getDamage());
					break;
				}
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;
		else if (!Utils.between(getSprite().getFrame(), 8, 13)) return false;

		return other instanceof Player != source.getSource() instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}
}
