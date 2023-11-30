package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.ITrackable;

import java.awt.*;

public class TrackableProjectile extends EnemyProjectile implements ITrackable {
	public TrackableProjectile(Entity source, float speed, float angle, Point position) {
		super(source, speed, angle);
		getCoordinates().setPosition(position.x, position.y);
	}
}
