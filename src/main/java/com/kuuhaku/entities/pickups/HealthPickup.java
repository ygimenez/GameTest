package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class HealthPickup extends Pickup {
	public HealthPickup(Entity source) {
		super(source, "health");
	}

	@Override
	public void addBonus(Player player) {
		player.setHp(player.getHp() + 25);
	}
}
