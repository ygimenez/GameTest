package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class SpeedPickup extends Pickup {
	public SpeedPickup(Entity source) {
		super(source, "speed");
	}

	@Override
	public void addBonus(Player player) {
		player.setSpeed(player.getSpeed() + 0.1);
	}
}
