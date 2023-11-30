package com.kuuhaku.entities.pickups;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Pickup;
import com.kuuhaku.interfaces.Managed;

@Managed
public class DamagePickup extends Pickup {
	public DamagePickup(Entity source) {
		super(source, "damage");
	}

	@Override
	public void addBonus(Player player) {
		player.setDamage(player.getDamage() + 10);
	}
}
