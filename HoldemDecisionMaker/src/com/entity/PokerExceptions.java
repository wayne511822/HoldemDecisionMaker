package com.entity;

public class PokerExceptions {
	
	@SuppressWarnings("serial")
	public static class CardCountWrongException extends RuntimeException {
		
		public CardCountWrongException(String msg) {
			super(msg);
		}
	}

}
