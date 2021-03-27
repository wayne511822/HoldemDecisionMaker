package com.entity;

import java.util.Collections;
import java.util.Stack;

@SuppressWarnings("serial")
public class CardDeck extends Stack<Card> {

	public void shuffle() {
		Collections.shuffle(this);
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("[");
		
		for (int i = 0; i < this.size(); i++) {	
			
			Card card = this.get(i);
			strb.append(card).append(" ");
			
			if ((i + 1) % 4 == 0) {
				strb.append("\n");
			}
		}
		strb.append("]\n");
		return strb.toString();
	}
}
