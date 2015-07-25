package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the domino boneyard (a deck or set of dominoes).
 */
public class Boneyard {
	
	private List<Player> players;
	private int numberOfTiles;
	private int maxEndPips;
	private int dealAmount;
	
	
	private List<Domino> dominoes;
	
	/**
	 * Creates and populates a new boneyard of dominoes.
	 */
	public Boneyard(List<Player> players) {
		this.players = players;
		int playerSize = players.size();
		maxEndPips = getMaxEndPipOnDominoSet(playerSize);
		numberOfTiles = ((maxEndPips + 1) * (maxEndPips + 2)) / 2;
		dealAmount = (playerSize <= 4) ? 15 : ((playerSize <= 6) ? 12 : 11);
		
		rebuild();
	}
	
	/**
	 * Returns the maximum pip count for the domino set used by the given number of players.
	 * @param numPlayers number of players will determine the set of dominoes used
	 * @return the maximum pip count for the domino set used by the given number of players
	 */
	public static int getMaxEndPipOnDominoSet(int numPlayers) {
		return (numPlayers <= 3) ? 9 : 12;
	}
	
	/**
	 * Removes dominoes from all the players and rebuilds the boneyard. 
	 */
	public void rebuild() {
		for (Player player : players)
			player.removeDominoes();
		
		dominoes = new ArrayList<Domino>(numberOfTiles);
		for (int endOne = 0; endOne <= maxEndPips; endOne++)
			for (int endTwo = endOne; endTwo <= maxEndPips; endTwo++)
				dominoes.add(new Domino(endOne, endTwo));
	}
	
	/**
	 * Deals dominoes to each player and returns the player who drew that round's starting domino.
	 * @param pipRound the pip round number for the starting round
	 * @return the player who drew the starting domino and will start this round
	 */
	public Player deal(int pipRound) {
		Player startingPlayer = null;
		for (int dealCount = 0; dealCount < dealAmount; dealCount++) {
			for (Player player : players) {
				Domino drawnDomino = drawDomino();
				player.addDomino(drawnDomino);
				if (drawnDomino.isDouble() && drawnDomino.getEndOneCount() == pipRound) {
					startingPlayer = player;
					player.removeDomino(drawnDomino);
				}
			}
		}
		
		// if starting double is not drawn, start from a random player and have each player draw one until the starting double is drawn
		int playerIdx = Randomizer.getRandom(players.size());
		while (startingPlayer == null) {
			Domino drawnDomino = drawDomino();
			players.get(playerIdx).addDomino(drawnDomino);
			if (drawnDomino.isDouble() && drawnDomino.getEndOneCount() == pipRound) {
				startingPlayer = players.get(playerIdx);
				startingPlayer.removeDomino(drawnDomino);
			}				
			
			playerIdx++;
			if (playerIdx >= players.size())
				playerIdx = 0;
		}
		
		return startingPlayer;
	}
		
	/**
	 * Returns whether or not the boneyard has any dominoes
	 * @return true if the boneyard has dominoes; false if not
	 */
	public boolean isEmpty() {
		return dominoes.isEmpty();
	}
	
	/**
	 * Returns the number of dominoes currently in the boneyard.
	 * @return the number of dominoes currently in the boneyard
	 */
	public int countRemaining() {
		return dominoes.size();
	}
	
	/**
	 * Returns a randomly selected domino from the boneyard.
	 * @return a randomly selected domino
	 */
	public Domino drawDomino() {
		if (dominoes.isEmpty())
			return null;
		
		return dominoes.remove(Randomizer.getRandom(dominoes.size()));
	}
}
