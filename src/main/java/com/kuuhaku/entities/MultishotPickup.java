package com.kuuhaku.entities;

public class MultishotPickup extends Pickup {
	public MultishotPickup(Entity owner) {
		super(owner, "multishot.png");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setBullets(ship.getBullets() + 1);
	}
}
