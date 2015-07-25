package gui.domino;

import gui.ImageHelper;

import java.awt.Color;

import model.Domino;

/**
 * Displays a single domino.
 */
public class DominoDisplay extends AbstractDominoDisplay {
	private static final long serialVersionUID = 1L;

	private Domino domino;
	
	/**
	 * Creates a domino display.
	 * @param domino domino to display
	 * @param dominoEndSize size to make each domino end
	 */
	public DominoDisplay(Domino domino, int dominoEndSize) {
		super(dominoEndSize, (domino == null) ? Color.WHITE : Color.BLACK, (domino != null));
		this.domino = domino;
		if (domino == null)
			setBackground(Color.WHITE);
		else {
			endOneImageLabel.setIcon(ImageHelper.getDominoEndIcon(domino.getEndOneCount(), dominoEndSize));
			endTwoImageLabel.setIcon(ImageHelper.getDominoEndIcon(domino.getEndTwoCount(), dominoEndSize));
		}
	}
	
	/**
	 * Returns the domino displayed.
	 * @return the domino displayed; may return null
	 */
	public Domino getDomino() {
		return domino;
	}
	
	/**
	 * Flips the domino so end one becomes end two and vice versa.
	 */
	public void flipDomino() {
		if (domino != null) {
			domino.flip();
			endOneImageLabel.setIcon(ImageHelper.getDominoEndIcon(domino.getEndOneCount(), dominoEndSize));
			endTwoImageLabel.setIcon(ImageHelper.getDominoEndIcon(domino.getEndTwoCount(), dominoEndSize));
		}
	}
	
	@Override
	public void resizeDomino() {
		if (domino != null) {
			endOneImageLabel.setIcon(ImageHelper.getDominoEndIcon(domino.getEndOneCount(), dominoEndSize));
			endTwoImageLabel.setIcon(ImageHelper.getDominoEndIcon(domino.getEndTwoCount(), dominoEndSize));
		}
	}
}
