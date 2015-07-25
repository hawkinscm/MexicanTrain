package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps and tallies the players' scores for each round played. 
 */
public class ScoreKeeper {
	
	private LinkedHashMap<String, LinkedList<Integer>> roundScoreCard;
	private int currentPipRoundNumber;
	
	/**
	 * Creates a new Score Keeper for the given players.
	 * @param players list of players to keep scores for
	 */
	public ScoreKeeper(List<String> playerNames, int maxPipNumber) {
		currentPipRoundNumber = maxPipNumber;
		
		roundScoreCard = new LinkedHashMap<String, LinkedList<Integer>>();
		for (String playerName : playerNames)
			roundScoreCard.put(playerName, new LinkedList<Integer>());
	}
	
	/**
	 * Tallies the players' scores for the round.
	 * @param players players to gather scores from
	 * @return map of player names to scores for the tallied round
	 */
	public static LinkedHashMap<String, Integer> tallyRoundScores(List<Player> players) {
		LinkedHashMap<String, Integer> playerScores = new LinkedHashMap<String, Integer>();
		for (Player player : players)
			playerScores.put(player.getName(), player.getDominoScore());
		return playerScores;
	}
	
	/**
	 * Returns a list of all the names of the players in this game.
	 * @return a list of all the names of the players in this game
	 */
	public List<String> getPlayerNames() {
		return new ArrayList<String>(roundScoreCard.keySet());
	}
	
	/**
	 * Returns the score the given player received for the specified round.
	 * @param playerName the name of the player to get the round score for
	 * @param roundNum the round number to get the score for
	 * @return the score the given player received for the specified round; 0 when the round has not yet ended or roundNum is <= 0
	 */
	public int getPlayerRoundScore(String playerName, int roundNumber) {
		if (roundNumber <= 0 || roundNumber > getNumberOfRoundsFinished())
			return 0;
		
		return roundScoreCard.get(playerName).get(roundNumber - 1);
	}
	
	/**
	 * Returns a mapping between player names and the scores they got for the given round.
	 * @param roundNum number of the round to display (where 1 is the first round played [double-9 or double-12]) 
	 * @return a mapping between player names and the scores they got for the given round
	 */
	public LinkedHashMap<String, Integer> getRoundScores(int roundNum) {
		LinkedHashMap<String, Integer> playerScores = new LinkedHashMap<String, Integer>();
		for (String playerName : roundScoreCard.keySet())
			playerScores.put(playerName, getPlayerRoundScore(playerName, roundNum));
		return playerScores;
	}
	
	/**
	 * Returns the total score for all rounds for the given player.
	 * @param playerName the name of player whose total score will be given
	 * @return the total score for the given player.
	 */
	public int getPlayerScore(String playerName) {
		int totalScore = 0;
		for (Integer roundScore : roundScoreCard.get(playerName))
			totalScore += roundScore;
		
		return totalScore;
	}
	
	public int getNumberOfZeroRounds(String playerName) {
		int numberOfZeroRounds = 0;
		for (Integer roundScore : roundScoreCard.get(playerName))
			if (roundScore == 0)
				numberOfZeroRounds++;
		
		return numberOfZeroRounds;
	}
	
	public int getLowestRoundScoreAboveZero(String playerName) {
		int lowestScoreAboveZero = 0;
		for (Integer roundScore : roundScoreCard.get(playerName))
			if (roundScore > 0 && (lowestScoreAboveZero == 0 || roundScore < lowestScoreAboveZero))
				lowestScoreAboveZero++;
		
		return lowestScoreAboveZero;
	}
	
	/**
	 * Returns the number of rounds that have been played to completion.
	 * @return the number of round that have been finished
	 */
	public int getNumberOfRoundsFinished() {
		return roundScoreCard.values().iterator().next().size();
	}
	
	/**
	 * Returns the current pip round number.
	 * @return the current pip round number
	 */
	public int getPipRoundNumber() {
		return currentPipRoundNumber;
	}
	
	/**
	 * Adds a round of scores for the players.
	 * @param playerScores mapping of player names to their scores for the round.
	 */
	public void addPlayerRoundScores(LinkedHashMap<String, Integer> playerScores) {
		currentPipRoundNumber--;
		
		for (String playerName : roundScoreCard.keySet()) {
			int score = playerScores.get(playerName);
			roundScoreCard.get(playerName).add(score);
		}
	}
}
