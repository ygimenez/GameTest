package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.ITrackable;

import java.awt.*;

public class MothershipBarrage extends Bullet implements ITrackable {
	public MothershipBarrage(Entity owner, boolean left, int y, double angle) {
		super(owner, "enemy_bullet", 25, 1, angle);

		Rectangle safe = getParent().getSafeArea();
		getBounds().setPosition(left ? safe.x - 50 : safe.x + safe.width + 50, y);
	}
}
