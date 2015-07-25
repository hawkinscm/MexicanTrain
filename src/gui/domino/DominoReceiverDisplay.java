package gui.domino;

import gui.ImageHelper;

import java.awt.Color;

/**
 * Displays a place-holder for domino, showing what is required to play a domino to this spot.
 */
public class DominoReceiverDisplay extends AbstractDominoDisplay {
	private static final long serialVersionUID = 1L;
	
	private int requiredPipEnd;
	
	/**
	 * Creates a place-holder domino display.
	 * @param requiredPipEnd required number of pips required to play a domino to this place-holder
	 * @param dominoEndSize size to make each domino end
	 */
	public DominoReceiverDisplay(int requiredPipEnd, int dominoEndSize) {
		super(dominoEndSize, Color.GRAY, false);
		this.requiredPipEnd = requiredPipEnd;
		endOneImageLabel.setIcon(ImageHelper.toGrayscale(ImageHelper.getDominoEndIcon(requiredPipEnd, dominoEndSize)));
		setBackground(Color.LIGHT_GRAY);
	}
		
	/**
	 * Sets the number of pips required to play a domino to this place-holder
	 * @param requiredPipEnd required number of pips required to play a domino to this place-holder
	 */
	public void setRequiredPipCount(int requiredPipEnd) {
		this.requiredPipEnd = requiredPipEnd;
		endOneImageLabel.setIcon(ImageHelper.toGrayscale(ImageHelper.getDominoEndIcon(requiredPipEnd, dominoEndSize)));
	}
	
	@Override
	public void resizeDomino() {
		endOneImageLabel.setIcon(ImageHelper.toGrayscale(ImageHelper.getDominoEndIcon(requiredPipEnd, dominoEndSize)));
	}
}
