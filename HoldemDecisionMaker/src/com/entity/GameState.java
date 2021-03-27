package com.entity;

public enum GameState {

	STATE_BASE_GAME(0),	 // 主遊戲
	STATE_BONUS_GAME(1); // 比倍遊戲

	private int id;

	private GameState(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
