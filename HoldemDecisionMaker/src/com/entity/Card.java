package com.entity;

import com.enums.CardSuit;

public class Card {

	public int number;
	public CardSuit suit;
	
	public Card(int number, CardSuit suit) {
		this.number = number;
		this.suit = suit;
	}

	@Override
	public String toString() {
		return String.valueOf(suit) + "_" + number;
 	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (number != other.number)
			return false;
		if (suit != other.suit)
			return false;
		return true;
	}
 
}
