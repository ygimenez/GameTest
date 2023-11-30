package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.ITrackable;

import java.awt.*;

public class TrackableBullet extends EnemyBullet implements ITrackable {
	public TrackableBullet(Entity source, double speed, double angle, Point position) {
		super(source, speed, angle);
		getBounds().setPosition(position.x, position.y);
	}
}
