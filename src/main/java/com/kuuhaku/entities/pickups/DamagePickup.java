package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Ship;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class DamagePickup extends Pickup {
	public DamagePickup(Entity owner) {
		super(owner, "damage");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setDamage(ship.getDamage() + 10);
	}
}
