package com.kuuhaku.entities.players;

import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.other.PlayerBlink;
import com.kuuhaku.entities.projectiles.PlayerBoomerang;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Coordinates;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.awt.geom.Point2D;

@Managed
@Metadata(sprite = "scout", hp = 100)
public class Scout extends Player {
	private PlayerBoomerang lastBoom;

	public Scout(GameRuntime runtime) {
		super(runtime, 2, 2, 1, 30, 1.3f);
	}

	@Override
	protected void shoot() {
		AssetManager.playCue("ship_fire");
		getRuntime().spawn(lastBoom = new PlayerBoomerang(this, 0));
	}

	@Override
	protected boolean special() {
		if (lastBoom != null && lastBoom.isVisible() && !lastBoom.toBeRemoved()) {
			Rectangle safe = getRuntime().getSafeArea();
			Point2D.Float blinkPos = lastBoom.getGlobalCenter();

			if (!Utils.between(blinkPos.x, safe.x + getWidth(), safe.x + safe.width - getWidth())) {
				return false;
			} else if (!Utils.between(blinkPos.y, safe.y + getHeight(), safe.y + safe.height - getHeight())) {
				return false;
			}

			getRuntime().spawn(new PlayerBlink(this));
			getCoordinates().setPosition(lastBoom.getCoordinates());
			return true;
		}

		return false;
	}

	@Override
	public String getDescription() {
		return """
				Lightweight next-tech scout ship, intended for stealth missions where time is key. Onboard Pulsar 8L-1NK technology allows it to seemingly disappear from sight, only to reappear right behind enemy lines.
				
				Projectiles cannot be increased, but instead gain extra size and damage.
				""";
	}
}
