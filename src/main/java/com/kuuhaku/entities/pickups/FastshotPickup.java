package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Ship;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;

public class FastshotPickup extends Pickup {
	public FastshotPickup(Entity owner) {
		super(owner, "fastshot");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setFireRate(ship.getFireRate() + 0.5);
	}
}
