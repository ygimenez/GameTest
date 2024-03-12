package com.kuuhaku.entities.decoration;

import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.utils.Utils;

import java.awt.*;

public class Wind extends Entity implements IDynamic, IParticle {
	private final Player reference;

	public Wind(Player reference) {
		super(reference.getRuntime(), null);
		this.reference = reference;

		Rectangle safe = getRuntime().getSafeArea();
		getCoordinates().setAngle((float) Math.toRadians(180));
		getCoordinates().setSize(0, (int) (10 * reference.getSpeed()));
		getCoordinates().setPosition(safe.x + Utils.rng().nextFloat(safe.width), 0);
	}

	@Override
	public void update() {
		getCoordinates().translate(0, reference.getSpeed() * 2);
	}

	@Override
	public int getColor() {
		return (0x50 << 24) | (getRuntime().getForeground().getRGB() & 0xFFFFFF);
	}
}
