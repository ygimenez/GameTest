package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

import java.awt.geom.Point2D;

@Metadata(sprite = "boomerang")
public class PlayerBoomerang extends Projectile {
	float moveAng, speedMult = 1;
	int hitCooldown;

	public PlayerBoomerang(Player source, float angle) {
		super(source, (int) (source.getDamage() * (1 + 0.25f * (source.getBullets() - 1))), source.getFireRate(), angle);
		moveAng = getAngle();

		float scale = 1 + 0.5f * (source.getBullets() - 1);
		getSprite().setScale(scale, scale);
	}

	@Override
	public void update() {
		getCoordinates().setAngle((float) Math.toRadians(Math.toDegrees(getAngle()) + 5));
		if (hitCooldown > 0) {
			hitCooldown--;
		}

		move();
		for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof IDamageable d && hit(entity)) {
				boolean isSource = entity.equals(getSource());

				if (hitCooldown == 0 && !isSource) {
					AssetManager.playCue("hit");
					d.damage(getDamage());
					hitCooldown = getRuntime().millisToTick(100);
				}

				if (isSource && speedMult <= 0.9) {
					dispose();
				}

				break;
			}
		}
	}

	@Override
	protected void move() {
		float ang;
		if (speedMult > 0) {
			ang = moveAng;
		} else {
			ang = (float) Math.toRadians(Utils.angBetween(getSource().getGlobalCenter(), getGlobalCenter()));
		}

		float[] vector = Utils.angToVec(ang);
		getCoordinates().translate(vector[0] * getSpeed() * speedMult, vector[1] * getSpeed() * speedMult);

		speedMult -= 0.005f;
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;

		return getCoordinates().intersect(other.getCoordinates());
	}
}
