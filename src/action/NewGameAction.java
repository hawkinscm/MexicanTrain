package action;

import javax.swing.SwingUtilities;

import gui.ParticipantGUI;

/**
 * Relays the that a new game has begun with the given player names.
 */
public class NewGameAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public NewGameAction() {}
	
	/**
	 * Generates and returns a New Game Action message using the given data.
	 * @param playerNames the names of the new game players that will be written as data in the message
	 * @return a New Game Action message generated from the given data
	 */
	public String createMessage(String[] playerNames) {
		String message = this.getClass().getName();
		for (String playerName : playerNames)
			message += MAIN_DELIM + playerName;
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(final ParticipantGUI gui) {
		final String[] playerNames = getMessageWithoutClassHeader().split(MAIN_DELIM);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.initializeGame(playerNames);
			}
		});
		return null;
	}
}
