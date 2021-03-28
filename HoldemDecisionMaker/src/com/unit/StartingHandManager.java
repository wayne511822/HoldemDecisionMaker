package com.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.entity.Card;
import com.entity.CardProperties;
import com.entity.StartingHand;
import com.enums.CardSuit;
import com.enums.HandStrengthLevel;
import com.enums.SuitState;



public class StartingHandManager {
	
	private List<StartingHand>	startingHandList = new ArrayList<>();
	
	
	public StartingHandManager() {
		
		loadStartingHandLevelCSV();
	}
	
	
	private void loadStartingHandLevelCSV() {
		
		HandStrengthLevel[] values = HandStrengthLevel.values();
		SuitState[] states = SuitState.values();
		
		String path = "flie/StartingHandLevel.csv";		
		CsvReader reader = null;
		
		try {
			reader = new CsvReader(path);
			
			reader.readHeaders();
			
			while (reader.readRecord()) {
				
				for (int i = 0; i < values.length; i++) {
					
					int num1 = 0;
					int num2 = 0;
					int suitState = 0;
					
					String strNum1 = values[i].name() + "_Num1";
					String strNum2 = values[i].name() + "_Num2";
					String strSuitState = values[i].name() + "_SuitState";
					
					if (!reader.get(strNum1).equals("")) 
						num1 = Integer.valueOf(reader.get(strNum1));
					if (!reader.get(strNum2).equals("")) 
						num2 = Integer.valueOf(reader.get(strNum2));
					if (!reader.get(strSuitState).equals("")) 
						suitState = Integer.valueOf(reader.get(strSuitState));
					
					if (num1 != 0 && num2 != 0) {
						StartingHand startingHand = new StartingHand(num1, num2, states[suitState], values[i]);
						startingHandList.add(startingHand);
					}
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {		
			reader.close();
		}
			
	}
	
	public int parseHandCardLevel(Card card1, Card card2) {
		
		int num1 = new CardProperties.CardsNumberComarator().max(card1, card2).number;
		int num2 = card1.number == num1 ? card2.number : card1.number;
		SuitState state = card1.suit == card2.suit ? SuitState.SuitSame : SuitState.SuitDiff;
		
		StartingHand startingHand = startingHandList.stream()
													.filter(e -> e.getSuitState() == state)
													.filter(e -> e.getHandNumber()[0] == num1 && e.getHandNumber()[1] == num2)
													.findFirst()
													.get();
		
		
		return startingHand.getHandStrengthLevel().getLevel();
	}
	
	private void testparseHandCardLevel() {
		
		Card card1 = new Card(6, CardSuit.SPADE);
		Card card2 = new Card(12, CardSuit.SPADE);
		
		int parseHandCardLevel = parseHandCardLevel(card1, card2);
		System.out.println(parseHandCardLevel);
	}

	public static void main(String[] args) {
		StartingHandManager shm = new StartingHandManager();
		
		shm.testparseHandCardLevel();
	}
}
