package com.kuuhaku.entities.base;

import com.kuuhaku.entities.pickups.HealthPickup;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.view.GameRuntime;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class Boss extends Enemy {
	private boolean enraged = false;

	public Boss(GameRuntime runtime, String sprite, int hp, int fireRate, int bullets) {
		super(runtime, sprite, hp, fireRate, bullets);
	}

	public Boss(GameRuntime runtime, Sprite sprite, int hp, int fireRate, int bullets) {
		super(runtime, sprite, hp, fireRate, bullets);
	}

	@Override
	public void setHp(int hp) {
		super.setHp(hp);

		if (getHp() <= 0) {
			onDeath();
			AssetManager.playCue("boss_explode");
			CompletableFuture.runAsync(
					() -> AssetManager.playCue("boss_win"),
					CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
			);
		} else if (getHp() <= getBaseHp() / 2 && !enraged) {
			onEnrage();
			getRuntime().spawn(new HealthPickup(this));
			getRuntime().spawn(new HealthPickup(this));
			enraged = true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getRuntime().setBoss(null);
	}

	protected void onDeath() {

	}

	public boolean isEnraged() {
		return enraged;
	}

	protected void onEnrage() {

	}
}
