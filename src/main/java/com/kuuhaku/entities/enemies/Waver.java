package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.projectiles.EnemyProjectile;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

@Managed
@Metadata(sprite = "waver", hp = 150)
public class Waver extends Enemy {
	private int angle;

	public Waver(GameRuntime runtime) {
		super(runtime, null, 5000);
	}

	@Override
	public void move() {
		getCoordinates().translate(Utils.fsin((float) Math.toRadians(angle++ / 2f)), 0.3f);
	}

	@Override
	public void shoot() {
		AssetManager.playCue("enemy_fire");
		getRuntime().spawn(new EnemyProjectile(this, 1, Utils.angBetween(this, getRuntime().getRandomPlayer())));
	}
}
