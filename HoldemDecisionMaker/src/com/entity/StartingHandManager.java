package com.entity;

import java.util.ArrayList;
import java.util.List;


public class StartingHandManager {

	List<Card> hands = new ArrayList<>();
	
	private enum SuitState {
		SuitSame(1),
		SuitDiff(0);
		
		private int value;
		
		private SuitState(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
}
