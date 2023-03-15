package com.kuuhaku.entities;

public class HealthPickup extends Pickup {
	public HealthPickup(Entity owner) {
		super(owner, "health.png");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setHp(ship.getHp() + 25);
	}
}
