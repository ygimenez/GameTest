package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;

@Managed
@Metadata(sprite = "health")
public class HealthPickup extends Pickup {
	public HealthPickup(Entity source) {
		super(source);
	}

	@Override
	public void addBonus(Player player) {
		player.setHp(player.getHp() + 25);
	}
}
