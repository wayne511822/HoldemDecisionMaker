package com.entity;

public enum HandValues {

	RoyalFlush(9),		//�P��j��
	StraightFlush(8),	//�P�ᶶ
	FourOfAKind(7),		//�K��
	FullHouse(6),		//��Ī
	Flush(5),			//�P��
	Straigh(4),			//���l
	ThreeOfAKind(3),	//�T��
	TwoPairs(2),		//���
	OnePair(1),			//�@��
	HighCard(0);		//���P
	
	public int valueID;
	
	private HandValues(int valueID) {
		this.valueID = valueID;
	}

}
