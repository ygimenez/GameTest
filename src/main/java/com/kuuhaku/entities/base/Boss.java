package com.kuuhaku.entities.base;

import com.kuuhaku.entities.pickups.HealthPickup;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.view.GameRuntime;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class Boss extends Enemy {
	private boolean enraged;

	public Boss(GameRuntime runtime, Entity parent, int cooldown) {
		super(runtime, parent, cooldown);
	}

	@Override
	public void damage(int value) {
		super.damage(value);
		if (getHp() <= 0) {
			onDeath();
			AssetManager.playCue("boss_explode");
			CompletableFuture.runAsync(
					() -> AssetManager.playCue("boss_win"),
					CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
			);
		} else if (!enraged && isEnraged()) {
			onEnrage();
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
		return getHp() <= getBaseHp() / 2;
	}

	protected void onEnrage() {

	}
}
