package action;

import javax.swing.SwingUtilities;

import gui.MexicanTrainGUI;

/**
 * Action for informing players that a player has ended his turn.
 */
public class EndPlayerTurnAction extends Action<MexicanTrainGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public EndPlayerTurnAction() {}
	
	/**
	 * Generates and returns an End Player Turn Action message.
	 * @param playerName name of the player who ended his turn
	 * @param hasPlayedDomino whether or not the player played a domino on his turn
	 * @return an End Player Turn Action message
	 */
	public String createMessage(String playerName, boolean hasPlayedDomino) {
		setMessage(this.getClass().getName() + MAIN_DELIM + playerName + MAIN_DELIM + hasPlayedDomino);
		return getMessage();
	}
	
	@Override
	public Class<MexicanTrainGUI> getActionTypeClass() {
		return MexicanTrainGUI.class;
	}
	
	@Override
	public String[] performAction(final MexicanTrainGUI gui) {
		String[] data = getMessageWithoutClassHeader().split(MAIN_DELIM);
		final String playerName = data[0];
		final boolean hasPlayedDomino = Boolean.parseBoolean(data[1]);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.playerTurnEnded(playerName, hasPlayedDomino);
			}
		});
		return null;
	}
}
