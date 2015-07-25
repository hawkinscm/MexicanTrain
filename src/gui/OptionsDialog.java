package gui;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dialog for allowing player to see and set game options.
 */
public class OptionsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private JCheckBox isExtraTurnCheckBox;
	
	/**
	 * Creates a new Options Dialog.
	 * @param owner the frame that created/owns this dialog
	 * @param isMexicanTrainExtraTurn whether or not the mexican train is currently an extra turn
	 */
	public OptionsDialog(JFrame owner, final boolean isMexicanTrainExtraTurn) {
		super(owner, "Game Options");

		c.insets.bottom = 0;
		isExtraTurnCheckBox = new JCheckBox("Plays On The Mexican Train Are Extra Turns", isMexicanTrainExtraTurn);
		getContentPane().add(isExtraTurnCheckBox, c);
		
		c.insets.bottom = 10;
		c.insets.top = 0;
		c.gridy++;
		String explanation = "<html>" +
							 "When this option is selected, each player may play up to two dominoes per turn." + " <br> " +
		                     "A domino may first be played to the player's own train OR a public train; another" + " <br> " +
		                     "domino may then be played to the mexican train. A player does not have to play to" + " <br> " +
		                     "the mexican train and if he has already played on a train he does not have to draw" + " <br> " +
		                     "or mark his train public. A player may start his turn playing to the mexican train," + " <br> " +
		                     "however a further domino cannot be played and his turn will end." +
		                     "</html>";
		getContentPane().add(new JLabel(explanation), c);
		
		c.insets.top = 10;
		c.gridy++;
		JPanel buttonPanel = new JPanel();
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;

			public void buttonClicked() {
				dispose();
			}
		};
		buttonPanel.add(okButton);
		
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;

			public void buttonClicked() {
				isExtraTurnCheckBox.setSelected(isMexicanTrainExtraTurn);
				dispose();
			}			
		};
		buttonPanel.add(cancelButton);		
		getContentPane().add(buttonPanel, c);
		
		refresh();
	}
	
	/**
	 * Returns whether or not the user wants plays on the mexican train to be extra turns.
	 * @return true if the user wants plays on the mexican train to be extra turns; false if only one domino turn may be played
	 */
	public boolean isExtraTurnSelected() {
		return isExtraTurnCheckBox.isSelected();
	}
}
