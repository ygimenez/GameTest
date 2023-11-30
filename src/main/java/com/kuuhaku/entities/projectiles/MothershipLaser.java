package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.utils.Utils;

import java.awt.geom.Point2D;

public class MothershipLaser extends Bullet {
	private double angle = 450;

	public MothershipLaser(Entity source) {
		super(source, new Sprite(source.getRuntime(), "laser", 4, 1, 20, true), 100, 0, 180);
	}

	@Override
	public void update() {
		getBounds().setAngle(getSource().getAngle() + Math.toRadians(angle / 10));

		int radius = Math.max(getSource().getWidth(), getSource().getHeight()) / 2;
		Point2D center = getSource().getCenter();
		getBounds().setPosition(
				center.getX() - getWidth() / 2d - Utils.fsin(getAngle()) * radius,
				center.getY() + Utils.fcos(getAngle()) * getHeight() + Utils.fcos(getAngle()) * radius
		);

		super.update();
//		if (--angle <= -450) super.setHp(0);
//		else super.update();
	}

	@Override
	public void setHp(int hp) {
	}
}
