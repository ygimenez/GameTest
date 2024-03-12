package com.kuuhaku.entities.players;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.projectiles.PlayerOrb;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.view.GameRuntime;

import java.util.*;

@Metadata(sprite = "carrier", hp = 300)
public class Carrier extends Player {
	private final Queue<PlayerOrb> spentOrbs = new ArrayDeque<>();
	private int orbs;
	private float orbAngle;

	public Carrier(GameRuntime runtime) {
		super(runtime, 1.5f, 3, 75, 0.75f);
	}

	@Override
	public void update() {
		super.update();

		orbAngle += getFireRate();
		if (getAtkCooldown().use()) {
			if (orbs < getBullets()) {
				getRuntime().spawn(new PlayerOrb(this, orbs++));
			} else if (!spentOrbs.isEmpty()) {
				spentOrbs.poll().setSpent(false);
			}
		}
	}

	@Override
	protected void shoot() {

	}

	@Override
	protected void special() {
		AssetManager.playCue("ship_fire");

		Set<Entity> orbs = new HashSet<>(getChildren());
		for (Entity child : orbs) {
			if (child instanceof PlayerOrb orb) {
				orb.setParent(null);
				orb.setSpeed(2);
			}
		}

		this.orbs = 0;
	}

	public Queue<PlayerOrb> getSpentOrbs() {
		return spentOrbs;
	}

	public float getOrbAngle() {
		return orbAngle;
	}

	public int getOrbs() {
		return orbs;
	}
}