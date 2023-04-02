package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Ship;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class MultishotPickup extends Pickup {
	public MultishotPickup(Entity owner) {
		super(owner, "multishot");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setBullets(ship.getBullets() + 1);
	}
}
