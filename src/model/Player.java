package model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Mexican Train player.  
 */
public class Player {

	private String name;
	private PlayerType playerType;
	
	private List<Domino> dominoes;
	
	/**
	 * Creates a new Mexican Train player with a name and player type.
	 * @param name name of the player
	 * @param type type of player
	 */
	public Player(String name, PlayerType playerType) {
		this.name = name;
		this.playerType = playerType;
		
		dominoes = new LinkedList<Domino>();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Returns the player's name.
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the player's type.
	 * @return returns the player's type
	 */
	public PlayerType getPlayerType() {
		return playerType;
	}
	
	/**
	 * Sets the player's type to the given type.
	 * @param type player type to set
	 */
	public void setPlayerType(PlayerType type) {
		if (playerType == type)
			return;
		
		playerType = type;
	}
	
	/**
	 * Returns whether or not the player is a computer.
	 * @return true if the player is a computer; false, otherwise
	 */
	public boolean isComputer() {
		if (playerType == PlayerType.COMPUTER_EASY)
			return true;
		else if (playerType == PlayerType.COMPUTER_MEDIUM)
			return true;
		else if (playerType == PlayerType.COMPUTER_HARD)
			return true;
		
		return false;
	}

	/**
	 * Sets the player's dominoes.
	 * @param dominoes dominoes to set
	 */
	public void setDominoes(List<Domino> dominoes) {
		this.dominoes = dominoes;
	}
	
	/**
	 * Returns the player's dominoes.
	 * @return the player's dominoes
	 */
	public List<Domino> getDominoes() {
		return dominoes;
	}
	
	/**
	 * Returns the number of dominoes the player currently has.
	 * @return the number of dominoes the player currently has
	 */
	public int getDominoCount() {
		return (dominoes == null) ? 0 : dominoes.size();
	}
	
	/**
	 * Adds the domino to the player's dominoes.
	 * @param domino domino to add
	 */
	public void addDomino(Domino domino) {
		dominoes.add(domino);
	}

	/**
	 * Removes the domino from the player's dominoes.
	 * @param domino domino to remove
	 * @return true if the domino was found in the list and removed; false otherwise
	 */
	public boolean removeDomino(Domino domino) {
		return dominoes.remove(domino);
	}
	
	/**
	 * Removes all dominoes from the player.
	 */
	public void removeDominoes() {
		dominoes.clear();
	}
	
	/**
	 * Returns the total point value of this player's dominoes.
	 * @return the total point value of this player's dominoes
	 */
	public int getDominoScore() {
		int score = 0;
		for (Domino domino : dominoes)
			score += domino.getPipScore();
		
		return score;
	}
}
