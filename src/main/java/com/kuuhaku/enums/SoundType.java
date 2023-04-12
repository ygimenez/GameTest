package com.kuuhaku.enums;

import com.kuuhaku.manager.SettingsManager;

public enum SoundType {
	MASTER, EFFECT, MUSIC;

	public float getVolume() {
		float vol = Integer.parseInt(SettingsManager.get(name().toLowerCase() + "_volume", "75")) / 100f;

		if (this != MASTER) {
			return vol * MASTER.getVolume();
		}

		return vol;
	}
}
