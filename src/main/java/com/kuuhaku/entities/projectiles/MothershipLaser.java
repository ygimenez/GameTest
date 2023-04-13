package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.ITrackable;

import java.awt.*;

public class MothershipLaser extends Bullet implements ITrackable {
	public MothershipLaser(Entity owner, int x, double angle) {
		super(owner, "mothership_laser", 50, 0.5, angle);

		Rectangle safe = getParent().getSafeArea();
		getBounds().setPosition(x, safe.y - 50);
	}
}
