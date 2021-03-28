package com.enums;

public enum GameState {

	STATE_PRE_FLOP(0),	 
	STATE_THE_FLOP(1),
	STATE_THE_TURN(2),
	STATE_THE_RIVER(3);

	private int id;

	private GameState(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
