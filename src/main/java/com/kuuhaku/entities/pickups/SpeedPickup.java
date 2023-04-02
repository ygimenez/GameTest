package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Ship;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class SpeedPickup extends Pickup {
	public SpeedPickup(Entity owner) {
		super(owner, "speed");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setSpeed(ship.getSpeed() + 0.1);
	}
}
