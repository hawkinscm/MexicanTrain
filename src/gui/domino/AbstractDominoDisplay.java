package gui.domino;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Base class for a Domino Display.
 */
public abstract class AbstractDominoDisplay extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int DOMINO_END_MAX_SIZE = 60;
	
	protected JLabel endOneImageLabel;
	protected JLabel endTwoImageLabel;
	
	private Color borderColor;
	private int currentHalfMidBorderWidth;
	protected int dominoEndSize;
	
	private Timer timer;
	
	protected AbstractDominoDisplay(int dominoEndSize, Color borderColor, boolean highlight) {
		this.dominoEndSize = dominoEndSize;
		this.borderColor = borderColor;
		currentHalfMidBorderWidth = (dominoEndSize > DOMINO_END_MAX_SIZE / 2) ? 3 : ((dominoEndSize > DOMINO_END_MAX_SIZE / 3) ? 2 : 1);
		setLayout(new GridLayout(1, 2));
		
		if (highlight) {
			AbstractAction unhighlightAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					AbstractDominoDisplay.this.setBackground(Color.WHITE);
				}
			};
			timer = new Timer(3000, unhighlightAction);
			timer.setRepeats(false);
			timer.start();
			
			setBackground(Color.CYAN);
		}
		else
			setBackground(Color.WHITE);
		
		endOneImageLabel = new JLabel();
		endOneImageLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, currentHalfMidBorderWidth, this.borderColor));
		add(endOneImageLabel);
		
		endTwoImageLabel = new JLabel();
		endTwoImageLabel.setBorder(BorderFactory.createMatteBorder(1, currentHalfMidBorderWidth, 1, 1, this.borderColor));
		add(endTwoImageLabel);
	}
	
	/**
	 * Resizes the domino display to the given end size.
	 * @param dominoEndSize square size to make each domino end 
	 */
	public void resizeDisplay(int dominoEndSize) {
		this.dominoEndSize = dominoEndSize;
		int halfMidBorderWidth = (dominoEndSize > DOMINO_END_MAX_SIZE / 2) ? 3 : ((dominoEndSize > DOMINO_END_MAX_SIZE / 3) ? 2 : 1);
		if (currentHalfMidBorderWidth != halfMidBorderWidth) {
			currentHalfMidBorderWidth = halfMidBorderWidth;
			endOneImageLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, currentHalfMidBorderWidth, borderColor));
			endTwoImageLabel.setBorder(BorderFactory.createMatteBorder(1, currentHalfMidBorderWidth, 1, 1, borderColor));
		}
		resizeDomino();
	}
	
	/**
	 * Resizes the domino to the current square domino end size.
	 */
	protected abstract void resizeDomino();
	
	public void finalize() {
		timer.stop();
	}
}
