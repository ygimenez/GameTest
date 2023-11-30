package com.kuuhaku.entities.decoration;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.utils.Utils;

import java.awt.*;

public class Wind extends Entity implements IDynamic, IParticle {
	private final Player reference;

	public Wind(Player reference) {
		super(reference.getRuntime());
		this.reference = reference;

		Rectangle safe = getRuntime().getSafeArea();
		getBounds().setAngle(Math.toRadians(180));
		getBounds().setSize(0, (int) (10 * reference.getSpeed()));
		getBounds().setPosition(safe.x + Utils.rng().nextDouble(safe.width), 0);
	}

	@Override
	public void update() {
		getBounds().translate(0, reference.getSpeed() * 2);
	}

	@Override
	public int getColor() {
		return 0x50FFFFFF;
	}
}
