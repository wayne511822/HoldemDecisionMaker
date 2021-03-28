package com.enums;

public enum CardSuit {

	CLUB(0),
	DIAMOND(1),
	HEART(2),
	SPADE(3);
	
	public int suitNumber;
	
	private CardSuit(int suitNumber) {
		this.suitNumber = suitNumber;
	}
	
}
