package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.utils.Utils;

@Metadata(sprite = "boomerang")
public class PlayerBoomerang extends Projectile {
	final float moveAng;

	public PlayerBoomerang(Player source, float angle) {
		super(source, source.getDamage() * 2 / (source.getBullets() + 1), source.getFireRate(), angle);
		moveAng = angle;
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	protected void move() {
		float[] vector = Utils.angToVec(getAngle());
		getCoordinates().translate(vector[0] * moveAng, vector[1] * moveAng);
	}
}
