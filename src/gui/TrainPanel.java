package gui;

import gui.domino.DominoDisplay;
import gui.domino.DominoReceiverDisplay;
import gui.domino.DominoReceiverTransferHandler;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


import model.Domino;

/**
 * Panel for displaying a domino train.
 */
public class TrainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel dominoPanel;
	private GridBagConstraints c;
	private int dominoEndSize;
	
	private DominoReceiverDisplay placeHolderDominoDisplay;
	private int endPipCount;
	private boolean isPublicTrain;
	
	private LinkedList<Domino> trainDominoes;
	
	/**
	 * Creates a new TrainPanel.
	 * @param playManager handler for playing dominoes to the train
	 * @param playerName name of the player who owns this train 
	 */
	public TrainPanel(PlayManager playManager, String playerName) {
		dominoPanel = new JPanel();
		dominoPanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 0, 0);
		add(dominoPanel);
		
		trainDominoes = new LinkedList<Domino>();
		isPublicTrain = false;
		dominoPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		dominoEndSize = DominoDisplay.DOMINO_END_MAX_SIZE;
		
		placeHolderDominoDisplay = new DominoReceiverDisplay(0, dominoEndSize);
		placeHolderDominoDisplay.setTransferHandler(new DominoReceiverTransferHandler(playManager, playerName));
		add(placeHolderDominoDisplay);
	}
	
	/**
	 * Adds a domino to the display Train Panel.
	 * @param domino domino to add
	 */
	public void addDomino(Domino domino) {
		trainDominoes.add(domino);
		
		if (endPipCount != domino.getEndOneCount())
			domino.flip();
		dominoPanel.add(new DominoDisplay(domino, dominoEndSize), c);
		c.insets.left = 2;
		c.gridx++;
		setPlaceHolderDomino(domino.getEndTwoCount());
	}
	
	/**
	 * Clears all dominoes from the train and prepares it for a new round.
	 * @param startPipCount the pip count required to start the train
	 */
	public void restartTrain(int startPipCount) {
		dominoPanel.removeAll();
			
		trainDominoes.clear();
		setPublicTrain(false);
		
		c.gridx = 0;
		c.insets.left = 0;
		setPlaceHolderDomino(startPipCount);
	}
	
	/**
	 * Adds a domino place holder to the train, which will display the pip end number needed to play to the train.
	 * @param startPipCount pip end number needed to play to the train
	 */
	private void setPlaceHolderDomino(int startPipCount) {
		endPipCount = startPipCount;
		placeHolderDominoDisplay.setRequiredPipCount(startPipCount);
	}
	
	/**
	 * Sets whether or not this train is public.
	 * @param isPublic if true will set as public train; if false will set as private train
	 */
	public void setPublicTrain(boolean isPublic) {
		isPublicTrain = isPublic;
		if (isPublic)
			dominoPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
		else
			dominoPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
	}
	
	/**
	 * Returns whether or not this train is public
	 * @return true if this train is marked public; false if it is marked private
	 */
	public boolean isPublicTrain() {
		return isPublicTrain;
	}
	
	/**
	 * Returns the end pip count required to play on this train.
	 * @return the end pip count required to play on this train
	 */
	public int getRequiredEndPipCount() {
		return endPipCount;
	}
	
	/**
	 * Returns an ordered list of all dominoes in the train.
	 * @return an ordered list of all dominoes in the train
	 */
	public LinkedList<Domino> getTrainDominoes() {
		return trainDominoes;
	}
	
	/**
	 * Resizes the dominoes to the given end size.
	 * @param dominoEndSize square size to make a domino end 
	 */
	public void resizeDominoes(int dominoEndSize) {
		this.dominoEndSize = dominoEndSize;
		for (Component component : dominoPanel.getComponents())
			if (component instanceof DominoDisplay)
				((DominoDisplay) component).resizeDisplay(dominoEndSize);
		placeHolderDominoDisplay.resizeDisplay(dominoEndSize);
	}
}
