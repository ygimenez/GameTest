package com.kuuhaku.interfaces;

public interface IDamageable {
	int getBaseHp();

	void setBaseHp(int hp);

	int getHp();

	void damage(int value);
}
