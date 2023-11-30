package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class MultishotPickup extends Pickup {
	public MultishotPickup(Entity source) {
		super(source, "multishot");
	}

	@Override
	public void addBonus(Player player) {
		player.setBullets(player.getBullets() + 1);
	}
}
