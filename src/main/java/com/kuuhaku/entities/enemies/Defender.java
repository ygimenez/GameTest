package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.projectiles.EnemyProjectile;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

@Metadata(sprite = "snake")
public class Defender extends Enemy {
	private final Mothership owner;
	private float angle, radius, fac;
	private float angleInc = 1;
	private int tgtRadius = 90;
	private Point2D.Float origin, center;

	public Defender(Mothership parent) {
		super(parent.getRuntime(), parent, 1000);
		this.owner = parent;
		setHp(this.owner.getBaseHp() / 20);

		getCoordinates().setPosition(parent.getWidth() / 2f, parent.getHeight() / 2f);
	}

	@Override
	protected void move() {
		if (owner.toBeRemoved()) {
			damage(getHp());
			return;
		}

		angle += angleInc;

		if (owner.isEnraged()) {
			Rectangle safe = getRuntime().getSafeArea();
			if (getParent() != null) {
				origin = getParent().getGlobalCenter();
				center = (Point2D.Float) origin.clone();
				tgtRadius = (int) (Math.sqrt(safe.width * safe.width + safe.height * safe.height) / 2);
				angleInc /= 4;
				setParent(null);
			}

			if (fac < 1) {
				fac += 0.005f;
				radius = Math.max(0, radius - 3);
			} else {
				fac = 1;
			}

			Utils.moveTowards(origin, center, safe.width / 2f, safe.height / 2f, fac);
			getCoordinates().setPosition(
					Utils.clamp(center.x + Utils.fsin((float) Math.toRadians(angle * getSpeedMult())) * radius, 0, safe.width),
					Utils.clamp(center.y + Utils.fcos((float) Math.toRadians(angle * getSpeedMult())) * radius, 0, safe.height)
			);
		} else {
			getCoordinates().setPosition(
					getParent().getWidth() / 2f + Utils.fsin((float) Math.toRadians(angle * getSpeedMult())) * radius,
					getParent().getHeight() / 2f + Utils.fcos((float) Math.toRadians(angle * getSpeedMult())) * radius
			);
		}

		if (radius < tgtRadius) {
			radius++;
		}
	}

	@Override
	public void damage(int value) {
		if (radius < tgtRadius) return;
		super.damage(value);
		owner.damage(value / 2);
	}

	@Override
	protected void shoot() {
		if (radius == tgtRadius && owner.isEnraged() && Utils.rng().nextFloat() > 0.75f) {
			AssetManager.playCue("enemy_fire");
			getRuntime().spawn(new EnemyProjectile(this, 1, Utils.angBetween(this, getRuntime().getRandomPlayer())));
		}
	}
}
