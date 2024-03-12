package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.entities.players.Carrier;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

@Metadata(sprite = "orb")
public class PlayerOrb extends Projectile {
	private final Carrier owner;
	private final int orb;
	private boolean spent;
	private int bounces = 5;

	public PlayerOrb(Carrier source, int orb) {
		super(source, source.getDamage(), 0, 0);
		this.owner = source;
		this.orb = orb;

		setParent(source);
		getCoordinates().setAnchor(0.5f, 0.5f);
	}

	@Override
	public void update() {
		if (getParent() == null) {
			move();
			for (Entity entity : getRuntime().getEntities()) {
				if (entity instanceof IDamageable d && hit(entity)) {
					AssetManager.playCue("hit");
					d.damage(getDamage());

					if (bounces > 0) {
						float[] norm = getImpactNormal(entity);
						if (norm[0] != 0) {
							getCoordinates().setAngle((float) (2 * Math.PI - getAngle()));
						} else {
							getCoordinates().setAngle((float) (Math.PI - getAngle()));
						}

						bounces--;
					} else {
						dispose();
					}

					break;
				}
			}
		} else {
			if (!spent) {
				for (Entity entity : getRuntime().getEntities()) {
					if (entity instanceof IDamageable d && hit(entity)) {
						AssetManager.playCue("hit");
						d.damage(getDamage());
						spent = true;
						owner.getSpentOrbs().add(this);
						break;
					}
				}
			}

			int radius = owner.getRadius() * 5;
			float angleOffset = (360f / owner.getBullets()) * orb + owner.getOrbAngle();
			getCoordinates().setAngle((float) Math.toRadians(-angleOffset + 180));
			getCoordinates().setPosition(
					owner.getWidth() / 2f + Utils.fsin((float) Math.toRadians(angleOffset)) * radius,
					owner.getHeight() / 2f + Utils.fcos((float) Math.toRadians(angleOffset)) * radius
			);
		}
	}

	@Override
	protected void move() {
		if (bounces > 0) {
			float[] pos = getPosition();
			float[] vector = Utils.angToVec(getAngle());
			Rectangle safe = getRuntime().getSafeArea();
			if (!Utils.between(pos[0] + vector[0] * getSpeed(), safe.x + getWidth() / 2f, safe.x + safe.width - getWidth() / 2f)) {
				getCoordinates().setAngle((float) (2 * Math.PI - getAngle()));
				bounces--;
			}

			if (!Utils.between(pos[1] + vector[1] * getSpeed(), safe.y + getHeight() / 2f, safe.y + safe.height - getHeight() / 2f)) {
				getCoordinates().setAngle((float) (Math.PI - getAngle()));
				bounces--;
			}
		}

		super.move();
	}

	@Override
	public BufferedImage getImage() {
		if (spent) return null;

		return super.getImage();
	}

	public void setSpent(boolean spent) {
		this.spent = spent;
	}
}
