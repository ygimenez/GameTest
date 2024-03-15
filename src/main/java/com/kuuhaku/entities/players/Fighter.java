package com.kuuhaku.entities.players;

import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.projectiles.PlayerProjectile;
import com.kuuhaku.entities.projectiles.PlayerTorpedo;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.view.GameRuntime;

@Managed
@Metadata(sprite = "fighter", hp = 200)
public class Fighter extends Player {
	public Fighter(GameRuntime runtime) {
		super(runtime, 3, 0.5f, 1, 50, 1);
	}

	@Override
	protected void shoot() {
		AssetManager.playCue("ship_fire");
		for (int i = 0; i < getBullets(); i++) {
			float step = 30f / (getBullets() + 1);

			getRuntime().spawn(new PlayerProjectile(this, -30f / 2 + step * (i + 1)));
		}
	}

	@Override
	protected boolean special() {
		AssetManager.playCue("ship_fire");
		getRuntime().spawn(new PlayerTorpedo(this));
		return true;
	}

	@Override
	public String getDescription() {
		return """
				Standard-issue UDF fighter, paired with a trusty MK-I Hull Breaker torpedo allows this nimble ship to defeat any foe.
				
				Recommended for new players.
				""";
	}
}
