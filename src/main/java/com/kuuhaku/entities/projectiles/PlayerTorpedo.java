package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;

@Metadata(sprite = "torpedo")
public class PlayerTorpedo extends Projectile {
	private final Player owner;
	private int fuse = getRuntime().millisToTick(1000);

	public PlayerTorpedo(Player source) {
		super(source, 0, source.getFireRate() / 2, 0);
		this.owner = source;
	}

	@Override
	public void update() {
		super.move();
		fuse--;

		for (Entity entity : getRuntime().getEntities()) {
			if (fuse <= 0 || (entity instanceof IDamageable && hit(entity))) {
				AssetManager.playCue("explode");

				int blasts = owner.getBullets() * 4;
				float step = 360f / blasts;
				for (int i = 0; i < blasts; i++) {
					getRuntime().spawn(new TorpedoBlast(this, step * i));
				}

				dispose();
				break;
			}
		}
	}

	public Player getOwner() {
		return owner;
	}
}
