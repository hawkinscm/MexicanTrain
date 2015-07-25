package gui;

import gui.MexicanTrainGUI.TurnType;

import java.util.LinkedHashMap;

import model.Domino;

/**
 * Interface for class that will handle domino plays and current game information.
 */
public interface PlayManager {
	/**
	 * Returns whether or not the given domino can be played on the specified person's train
	 * @param domino domino to play
	 * @return trainOwner name of the owner of the train to play on
	 */
	public boolean canPlay(Domino domino, String trainOwner);
	
	/**
	 * Plays the given domino on the specified person's train.
	 * @param domino domino to play
	 * @return trainOwner name of the owner of the train to play on
	 */
	public void play(Domino domino, String trainOwner);
	
	/**
	 * Handles the playing of a domino by a player to a domino train.
	 * @param playerName name of player who is playing the domino
	 * @param domino domino to play
	 * @param trainOwner owner of the train where the domino is being played
	 */
	public void play(String playerName, Domino domino, String trainOwner);
	
	/**
	 * Draws a domino from the boneyard for the given player.
	 * @param playerName name of the player receiving a domino
	 * @return true if a domino was drawn; false if the boneyard is empty
	 */
	public boolean draw(String playerName);
	
	/**
	 * Ends the controlled player's turn.
	 * @param playerName name of the player whose turn is ending
	 * @param hasPlayedDomino whether or not the player played a domino this turn
	 */
	public void endTurn(String playerName, boolean hasPlayedDomino);
	
	/**
	 * Returns the current turn type.
	 * @return the current turn type
	 */
	public TurnType getCurrentTurnType();
	
	/**
	 * Returns the player name to train map.
	 * @return the player name to train map
	 */
	public LinkedHashMap<String, TrainPanel> getPlayerTrainMap();
	
	/**
	 * Returns whether or not the mexican train extra turn option is selected.
	 * @return true if selected; false if not
	 */
	public boolean isOptionMexicanTrainExtraTurnSelected();
	
	/**
	 * Returns the name the train's owner that has an open double which needs to be satisfied; 
	 * @return the name the train's owner that has an open double which needs to be satisfied or null if no double needs satisfying
	 */
	public String getSatisfyDoubleTrainOwner();
}
