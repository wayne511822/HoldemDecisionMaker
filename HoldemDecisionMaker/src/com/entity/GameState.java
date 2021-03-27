package com.entity;

public enum GameState {

	STATE_BASE_GAME(0),	 // �D�C��
	STATE_BONUS_GAME(1); // �񭿹C��

	private int id;

	private GameState(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
