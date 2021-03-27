package com.entity;

public enum HandValues {

	RoyalFlush(9),		//同花大順
	StraightFlush(8),	//同花順
	FourOfAKind(7),		//鐵支
	FullHouse(6),		//葫蘆
	Flush(5),			//同花
	Straigh(4),			//順子
	ThreeOfAKind(3),	//三條
	TwoPairs(2),		//兩對
	OnePair(1),			//一對
	HighCard(0);		//散牌
	
	public int valueID;
	
	private HandValues(int valueID) {
		this.valueID = valueID;
	}

}
