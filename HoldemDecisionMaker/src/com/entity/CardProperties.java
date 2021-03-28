package com.entity;

import java.util.Comparator;


public class CardProperties {

	public static class CardsStandardComparator implements Comparator<Card> {
		
		@Override
		public int compare(Card o1, Card o2) {

			if (o1.number != o2.number) {
				return o1.number - o2.number;
			}
			return o1.suit.suitNumber - o2.suit.suitNumber;
		}
		
	}
	
	public static class CardsNumberComarator implements Comparator<Card> {

		@Override
		public int compare(Card o1, Card o2) {
			if (o1.number == 1 || o2.number == 1) {
				return o1.number == 1 ? 1 : -1;
				
			}else {
				
				return Integer.compare(o1.number, o2.number);
			}
		}
		
		public Card max(Card o1, Card o2) {
			
			if (compare(o1, o2) == 1) {
				return o1;
				
			}else if (compare(o1, o2) == -1) {
				return o2;
				
			}
			return o1;
		}
	}
	
	public static class CardsNumberComparator implements Comparator<Integer> {

		@Override
		public int compare(Integer o1, Integer o2) {
			
			if (o1 == 1 || o2 == 1) {
				return o1 == 1 ? 1 : -1;
				
			}else {
				
				return Integer.compare(o1, o2);
			}
		}
		
	}
}
