package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class FastshotPickup extends Pickup {
	public FastshotPickup(Entity source) {
		super(source, "fastshot");
	}

	@Override
	public void addBonus(Player player) {
		player.setFireRate(player.getFireRate() + 0.5);
	}
}
