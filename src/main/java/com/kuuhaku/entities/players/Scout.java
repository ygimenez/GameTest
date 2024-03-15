package com.kuuhaku.entities.players;

import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.projectiles.PlayerBoomerang;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.view.GameRuntime;

@Managed
@Metadata(sprite = "scout", hp = 100)
public class Scout extends Player {
	private PlayerBoomerang lastBoom;

	public Scout(GameRuntime runtime) {
		super(runtime, 2, 2, 2, 30, 1.3f);
	}

	@Override
	protected void shoot() {
		AssetManager.playCue("ship_fire");
		getRuntime().spawn(lastBoom = new PlayerBoomerang(this, 0));
	}

	@Override
	protected boolean special() {
		if (lastBoom != null && !lastBoom.toBeRemoved()) {
			getCoordinates().setPosition(lastBoom.getCoordinates());
			return true;
		}

		return false;
	}

	@Override
	public String getDescription() {
		return """
				Standard-issue UDF fighter, paired with a trusty MK-I Hull Breaker torpedo allows this nimble ship to defeat any foe.
								
				Recommended for new players.
				""";
	}
}
