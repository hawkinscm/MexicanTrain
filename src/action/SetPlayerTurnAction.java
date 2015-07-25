package action;

import javax.swing.SwingUtilities;

import gui.MexicanTrainGUI.TurnType;
import gui.ParticipantGUI;

/**
 * Action for notifying Network players that it is now a new player's turn. 
 */
public class SetPlayerTurnAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public SetPlayerTurnAction() {}
	
	/**
	 * Generates and returns a Change Player Turn Action message using the given data.
	 * @param playerName name of the player whose turn it now is
	 * @param turnType type of turn determining what the player is allowed to do
	 * @return a Change Player Turn Action message generated from the given data
	 */
	public String createMessage(String playerName, TurnType turnType) {
		setMessage(this.getClass().getName() + MAIN_DELIM + playerName + MAIN_DELIM + turnType.toString());
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(final ParticipantGUI gui) {
		String[] dataMessages = getMessageWithoutClassHeader().split(MAIN_DELIM);
		final String playerName = dataMessages[0];
		final TurnType turnType = TurnType.valueOf(dataMessages[1]);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.setPlayerTurn(playerName, turnType);
			}
		});
		return null;
	}
}
