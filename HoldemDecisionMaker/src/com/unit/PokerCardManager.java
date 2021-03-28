package com.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.entity.*;
import com.entity.CardProperties.CardsNumberComparator;
import com.enums.CardSuit;
import com.enums.HandValues;

public class PokerCardManager {

	private double[] handValueScore = {0, 1.9, 10.7, 24.1, 129.8, 259.2, 353.7, 2122.2, 36784.8, 331063.2};
	
	private CardDeck mainDeck = new CardDeck();
	private CardDeck discards = new CardDeck();
	public Card[] handCard = new Card[2];
	
	private List<Card> communityCard = new ArrayList<>();
	private List<Card> cardList = new ArrayList<>();
	
	private HandValues nowHandCardType;
	
	public PokerCardManager( ) {
		initMainDeck();
	}
	
	public void initMainDeck() {
		mainDeck = new CardDeck();
		CardSuit[] cardSuits = CardSuit.values();
		
		for (int i = 1; i <= 13; i++) {
			for (int j = 0; j < 4; j++) {
				mainDeck.push(new Card(i, cardSuits[j]));
			}
		}
	}
	
	public Card drawCard() {
		return mainDeck.pop();
	}
	
	public void discardCard(Card card) {
		discards.push(card);
	}
	
	public void shuffleMainDeck() {
		mainDeck.shuffle();
	}
	
	public void discardHandCard() {
		
		for (int i = 0; i < handCard.length; i++) {
			if (handCard[i] != null)
				discards.push(handCard[i]);
		}
		handCard = new Card[2];
	}

	public Card[] buildHandCard() {
		
		for (int i = 0; i < handCard.length; i++) {
			handCard[i] = drawCard();
		}		
		return handCard;
	}
	
	public void  probCalculation(List<Card> cardList) {
		
		long start = System.currentTimeMillis();
		
		List<Card> mainDeckList = new ArrayList<>(mainDeck);
		List<Card> copyList = cardList.stream().collect(Collectors.toList());
		HashMap<HandValues, Integer> result = new HashMap<>();
		
		recursiveCalculateHandCardProb(result, copyList, mainDeckList, 0, 5);
		
		int sum = result.entrySet().stream().mapToInt(e -> e.getValue()).sum();
		double scoreSum = 0;
		
		HandValues[] values = HandValues.values();
		
		for (int i = 0; i < values.length; i++) {
			HandValues key = values[i];
			
			if (result.containsKey(key)) {
				
				int count = result.get(key);
				double score = handValueScore[key.valueID] * (double)count;
				System.out.println(key + ": " + count + ",\tscore = " + score + ",\t " + ((double) count / (double) sum * 100) + "%");
				scoreSum += score;
			}
		}
		
		
		long end = System.currentTimeMillis();
		
		System.out.println("sum = " + sum + ", Spend time = " + (end - start) + ", score = " + scoreSum);
	}
	
	private void recursiveCalculateHandCardProb(HashMap<HandValues, Integer> result, List<Card> cardList, List<Card> mainDeck, int startIndex, int limit) {
		
		if (cardList.size() == limit) {
			HandValues handValue = checkHandValues(cardList);	
		
			if (result.containsKey(handValue)) {
				int storage = result.get(handValue);
				result.put(handValue, ++storage);
				
			}else {
				result.put(handValue, 1);
			}
			
			return;
		}
		
		for (int i = startIndex; i < mainDeck.size(); i++) {
			startIndex++;
			Card card = mainDeck.get(i);
			
			if (!cardList.contains(card) ) {
				cardList.add(card);

				recursiveCalculateHandCardProb(result, cardList, mainDeck, startIndex, limit);
				cardList.remove(card);
			}
		}
	}
	
	public HandValues checkHandValues(List<Card> cardList) {
		
		if (cardList.size() > 7 || cardList.size() < 5) 
			throw new PokerExceptions.CardCountWrongException("checkHandValues cardList size error, size:" + cardList.size());
		
		
		Map<HandValues, List<Card>> resultCollection = new HashMap<>();

		checkPairs(cardList, resultCollection);
		checkFlush(cardList, resultCollection);
		checkStraight(cardList, resultCollection);
	
		if (resultCollection.size() == 0) {
			return HandValues.HighCard;
			
		}else {
			Entry<HandValues, List<Card>> maxHandValue = resultCollection
														 .entrySet()
														 .stream()
														 .max((e1, e2) ->Integer.compare(e1.getKey().valueID, e2.getKey().valueID))
														 .get();

			return maxHandValue.getKey();
		}
	}
	
	private void checkPairs(List<Card> cardList, Map<HandValues, List<Card>> result) {
		
		Map<Integer, Integer> numberCountMap = buildNumberCountMap(cardList);
		
		Map<Integer, Integer> afterFilter = numberCountMap
											.entrySet()
											.stream()
											.filter(e -> e.getValue() >= 2)
											.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		
		if (afterFilter.size() > 2) {
			int minNum = afterFilter
					  	 .entrySet()
					  	 .stream()
					  	 .filter(e -> e.getValue() <= 2)
					  	 .mapToInt(Map.Entry::getKey)
					  	 .filter(num -> num > 1)
					  	 .min()
					  	 .orElse(0);
			
			if (minNum != 0)
				afterFilter.remove(minNum);		
		}
	
		
		int maxCount = afterFilter
					   .entrySet()
					   .stream()
					   .mapToInt(Map.Entry::getValue)
					   .max()
					   .orElse(0);
		
		
		List<Card> temp = new ArrayList<>();
		
		if (maxCount == 4) {
			
			Set<Integer> cardNums = afterFilter.keySet();
			for (Integer num : cardNums) {
				
				if (afterFilter.get(num) == 4) {
				
					for (Card card : cardList) {
						
						if (card.number == num) {
							temp.add(card);
						}
					}
					
					fillCardToFive(temp, cardList);
					break;
				}
			}
			
			result.put(HandValues.FourOfAKind, temp);
				
		}else if (maxCount == 3) {
			
			if (afterFilter.size() == 1) {
				
				Set<Integer> cardNums = afterFilter.keySet();
				for (Integer num : cardNums) {
					
					for (Card card : cardList) {
						
						if (card.number == num) {
							temp.add(card);
						}
					}
				}
				
				fillCardToFive(temp, cardList);
				result.put(HandValues.ThreeOfAKind, temp);
				
			}else if (afterFilter.size() == 2) {
	
				Object[] nums = afterFilter.keySet().toArray();
				int num1 = (int) nums[0];
				int num2 = (int) nums[1];
				
				for (Card card : cardList) {
					
					if (card.number == num1 || card.number == num2)
						temp.add(card);
				}

				if (temp.size() > 5) {
					
					int remove = num1 == 1 ? num2 : (num1 < num2 ? num1 : num2);
					
					for (Card card : temp) {
						if (card.number == remove) {
							temp.remove(card);
							break;
						}
					}
				}
		
				result.put(HandValues.FullHouse, temp);
			}
			
		}else if (maxCount == 2) {
			
			if (afterFilter.size() == 2) {
				
				Object[] nums = afterFilter.keySet().toArray();
				int num1 = (int) nums[0];
				int num2 = (int) nums[1];
				
				for (Card card : cardList) {
					
					if (card.number == num1 || card.number == num2) 
						temp.add(card);
				}
			
				fillCardToFive(temp, cardList);
				result.put(HandValues.TwoPairs, temp);
			
			}else if (afterFilter.size() == 1) {
				
				Set<Integer> cardNums = afterFilter.keySet();
				for (Integer num : cardNums) {
					
					for (Card card : cardList) {
							
						if (card.number == num) 
							temp.add(card);
					}
				}
				
				fillCardToFive(temp, cardList);
				result.put(HandValues.OnePair, temp);
			}
		}
	}
	
	private void checkFlush(List<Card> cardList, Map<HandValues, List<Card>> result) {

		Map<CardSuit, Integer> suitCountMap = buildSuitCountMap(cardList);
		
		Set<CardSuit> suits = suitCountMap.keySet();
		for (CardSuit suit : suits) {
			
			if (suitCountMap.get(suit) >= 5) {
				
				List<Card> sameSuitCards = cardList.stream().filter(card -> card.suit == suit).collect(Collectors.toList());
				
				Collections.sort(sameSuitCards, new CardProperties.CardsNumberComarator());
				
				checkStraight(sameSuitCards, result);
				
				if (result.containsKey(HandValues.Straigh)) {
					List<Card> straightFlush = result.get(HandValues.Straigh);
					result.remove(HandValues.Straigh);
					
					if (straightFlush.get(4).number == 1) 
						result.put(HandValues.RoyalFlush, straightFlush);
					else	
						result.put(HandValues.StraightFlush, straightFlush);
					
					break;
				}
				
				
				while (sameSuitCards.size() > 5) {
					sameSuitCards.remove(0);
				}
			
				result.put(HandValues.Flush, sameSuitCards);
				break;
			}
		}
	}
	
	private void checkStraight(List<Card> cardList, Map<HandValues, List<Card>> result) {
		
		List<Card> sortedCards = cardList.stream().sorted(new CardProperties.CardsStandardComparator()).collect(Collectors.toList());
		List<Card> temp = new ArrayList<>();
		
		for (int i = 0; i < sortedCards.size(); i++) {

			if (i == 0 || sortedCards.get(i).number - temp.get(temp.size() - 1).number == 1) {
				temp.add(sortedCards.get(i));
				
			}else if (sortedCards.get(i).number - temp.get(temp.size() - 1).number == 0) {	
				continue;
				
			}else if (temp.size() >= 5) {
				break;
				
			}else {
				temp = new ArrayList<>();
				temp.add(sortedCards.get(i));
			}
		}
		
		if (temp.get(temp.size() - 1).number == 13 && sortedCards.get(0).number == 1)
			temp.add(sortedCards.get(0));
		
			
		if (temp.size() >= 5) {
			
			while (temp.size() > 5) {
				temp.remove(0);
			}
			
			result.put(HandValues.Straigh, temp);
		}
	}
	
	private void fillCardToFive(List<Card> target, List<Card> source) {
		
		List<Card> filted = source
							.stream()
							.filter(card -> !target.contains(card))
							.sorted(new CardProperties.CardsNumberComarator())
							.collect(Collectors.toList());

		
		while (target.size() < 5) {
			
				Card card = filted.get(filted.size() - 1);
				target.add(card);
				filted.remove(card);
		}
	}
	
	private Map<Integer, Integer> buildNumberCountMap(List<Card> cardList) {
		Map<Integer, Integer> sameNumberMap = new HashMap<>();
		
		Map<Integer, Long> collect = cardList
									 .stream()
									 .map(card -> card.number)
									 .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		
		Set<Integer> keys = collect.keySet();
		for (Integer key : keys) {
			sameNumberMap.put(key, collect.get(key).intValue());
		}
		
		return sameNumberMap;
	}
	
	private Map<CardSuit, Integer> buildSuitCountMap(List<Card> cardList) {
		Map<CardSuit, Integer> sameSuitMap = new HashMap<>();
		
		Map<CardSuit, Long> collect = cardList
									  .stream()
									  .map(card -> card.suit)
									  .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		
		Set<CardSuit> keys = collect.keySet();
		for (CardSuit key : keys) {
			sameSuitMap.put(key, collect.get(key).intValue());
		}
		
		return sameSuitMap;
	}

	private void sameNumberMapAddCard(Map<Integer, Integer> sameNumberMap, Card card) {
		
		if (sameNumberMap.containsKey(card.number)) {
			
			int count = sameNumberMap.get(card.number);
			sameNumberMap.put(card.number, ++count);
			
		}else {
			sameNumberMap.put(card.number, 1);
		}
	}
	
	private void sameSuitMapAddCard(Map<CardSuit, Integer> sameSuitMap, Card card) {
		
		if (sameSuitMap.containsKey(card.suit)) {
			
			int count = sameSuitMap.get(card.suit);
			sameSuitMap.put(card.suit, ++count);
			
		}else {
			sameSuitMap.put(card.suit, 1);
		}
	}
	
	private void testcheckPairs() {
		
		List<Card> cardList = new ArrayList<>();
		cardList.add(new Card(5, CardSuit.DIAMOND));
		cardList.add(new Card(7, CardSuit.SPADE));
		cardList.add(new Card(12, CardSuit.CLUB));
		cardList.add(new Card(9, CardSuit.HEART));
		cardList.add(new Card(2, CardSuit.CLUB));
		cardList.add(new Card(11, CardSuit.DIAMOND));
		cardList.add(new Card(11, CardSuit.SPADE));
		
		Map<HandValues, List<Card>> result = new HashMap<>();
		
		checkPairs(cardList, result);
		
		Set<HandValues> keySet = result.keySet();
		for (HandValues value : keySet) {
			System.out.println(value + ": " + result.get(value));
		}
	}
	
	private void testcheckFlush() {
		
		List<Card> cardList = new ArrayList<>();
		cardList.add(new Card(13, CardSuit.DIAMOND));
		cardList.add(new Card(8, CardSuit.DIAMOND));
		cardList.add(new Card(12, CardSuit.DIAMOND));
		cardList.add(new Card(9, CardSuit.DIAMOND));
		cardList.add(new Card(10, CardSuit.DIAMOND));
		cardList.add(new Card(11, CardSuit.DIAMOND));
		cardList.add(new Card(1, CardSuit.DIAMOND));
		
		Map<HandValues, List<Card>> result = new HashMap<>();
		
		checkFlush(cardList, result);
		
		Set<HandValues> keySet = result.keySet();
		for (HandValues value : keySet) {
			System.out.println(value + ": " + result.get(value));
		}
	}
	
	private void testcheckStraight() {
		List<Card> cardList = new ArrayList<>();
		cardList.add(new Card(13, CardSuit.DIAMOND));
		cardList.add(new Card(9, CardSuit.DIAMOND));
		cardList.add(new Card(9, CardSuit.DIAMOND));
		cardList.add(new Card(12, CardSuit.DIAMOND));
		cardList.add(new Card(9, CardSuit.DIAMOND));
		cardList.add(new Card(11, CardSuit.DIAMOND));
		cardList.add(new Card(10, CardSuit.DIAMOND));
		

		Map<HandValues, List<Card>> result = new HashMap<>();
		
		checkStraight(cardList, result);
		
		Set<HandValues> keySet = result.keySet();
		for (HandValues value : keySet) {
			System.out.println(value + ": " + result.get(value));
		}
	}
	
	private void testcheckHandValues() {
		List<Card> cardList = new ArrayList<>();
		cardList.add(new Card(5, CardSuit.DIAMOND));
		cardList.add(new Card(7, CardSuit.SPADE));
		cardList.add(new Card(9, CardSuit.DIAMOND));
		cardList.add(new Card(1, CardSuit.DIAMOND));
		cardList.add(new Card(8, CardSuit.CLUB));
		cardList.add(new Card(3, CardSuit.HEART));
		cardList.add(new Card(10, CardSuit.DIAMOND));
		
		
		HandValues handValues = checkHandValues(cardList);
		System.out.println(handValues);
	}
	
	private void testprobCalculation() {
		List<Card> cardList = new ArrayList<>();
//		cardList.add(new Card(5, CardSuit.DIAMOND));
		cardList.add(new Card(3, CardSuit.DIAMOND));
//		cardList.add(new Card(9, CardSuit.DIAMOND));
		cardList.add(new Card(5, CardSuit.CLUB));
//		cardList.add(new Card(8, CardSuit.CLUB));
//		cardList.add(new Card(3, CardSuit.HEART));
//		cardList.add(new Card(10, CardSuit.DIAMOND));
		
		for (Card card : cardList) {
			mainDeck.remove(card);
			discardCard(card);
		}
		
		probCalculation(cardList);
	}
	
	public static void main(String[] args) {
		PokerCardManager manager = new PokerCardManager();
		
//		manager.testCompare();
//		manager.testcheckPairs();
//		manager.testcheckFlush();
//		manager.testcheckStraight();
//		manager.testcheckHandValues();
		manager.testprobCalculation();
	}
	
	private void testCompare() {
		
		
		List<Card> cardList = new ArrayList<>();
		cardList.add(new Card(5, CardSuit.DIAMOND));
		cardList.add(new Card(5, CardSuit.SPADE));
		cardList.add(new Card(5, CardSuit.CLUB));
		cardList.add(new Card(1, CardSuit.DIAMOND));
		cardList.add(new Card(9, CardSuit.SPADE));
		
		List<Integer> list = cardList.stream()
							.map(card -> card.number)
							.sorted(new CardProperties.CardsNumberComparator())
							.sorted(Comparator.reverseOrder())
							.collect(Collectors.toList());
		
		System.out.println(list);
		
	}
}
