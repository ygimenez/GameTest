package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Ship;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class HealthPickup extends Pickup {
	public HealthPickup(Entity owner) {
		super(owner, "health");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setHp(ship.getHp() + 25);
	}
}
