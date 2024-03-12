package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;

@Managed
@Metadata(sprite = "speed")
public class SpeedPickup extends Pickup {
	public SpeedPickup(Entity source) {
		super(source);
	}

	@Override
	public void addBonus(Player player) {
		player.setSpeed(player.getSpeed() + 0.25f);
	}
}
