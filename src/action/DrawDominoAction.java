package action;

import javax.swing.SwingUtilities;

import gui.MexicanTrainGUI;

/**
 * Action for notifying Network players that a player has drawn a domino.
 */
public class DrawDominoAction extends Action<MexicanTrainGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DrawDominoAction() {}
	
	/**
	 * Generates and returns a Draw Domino Action message using the given data.
	 * @param playerName name of the player drawing a domino.
	 * @return a Draw Domino Action message generated from the given data
	 */
	public String createMessage(String playerName) {
		String message = this.getClass().getName() + MAIN_DELIM + playerName;
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<MexicanTrainGUI> getActionTypeClass() {
		return MexicanTrainGUI.class;
	}
	
	@Override
	public String[] performAction(final MexicanTrainGUI gui) {
		final String playerName = getMessageWithoutClassHeader();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.dominoDrawn(playerName);
			}
		});
		return null;
	}
}
