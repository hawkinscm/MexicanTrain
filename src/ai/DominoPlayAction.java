package ai;

import gui.PlayManager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import model.Domino;

/**
 * Action for making a domino play.
 */
public class DominoPlayAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private PlayManager playManager;
	private String playerName;
	private Domino domino;
	private String trainOwner;
	
	/**
	 * Creates a new domino play action.
	 * @param hostGUI GUI required to make the play
	 * @param playerName name of the player making the play
	 * @param domino domino to play
	 * @param trainOwner name of the owner of the train to play on
	 */
	public DominoPlayAction(PlayManager playManager, String playerName, Domino domino, String trainOwner) {
		this.playManager = playManager;
		this.playerName = playerName;
		this.domino = domino;
		this.trainOwner = trainOwner;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		playManager.play(playerName, domino, trainOwner);
	}
	
	/**
	 * Returns the domino to play.
	 * @return the domino to play
	 */
	public Domino getDomino() {
		return domino;
	}
	
	/**
	 * Returns name of the owner of the train to play on.
	 * @return name of the owner of the train to play on
	 */
	public String getTrainOwner() {
		return trainOwner;
	}
}
