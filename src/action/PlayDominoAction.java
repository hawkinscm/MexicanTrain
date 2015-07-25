package action;

import javax.swing.SwingUtilities;

import model.Domino;
import gui.MexicanTrainGUI;

/**
 * Action for notifying Network players that a player has played a domino to a train.
 */
public class PlayDominoAction extends Action<MexicanTrainGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public PlayDominoAction() {}
	
	/**
	 * Generates and returns a Play Domino Action message using the given data.
	 * @param playerName name of the player playing the domino.
	 * @param domino domino being played
	 * @param trainOwner name of the player who owns the train where the domino is being played; may be the mexican train
	 * @return a Play Domino Action message generated from the given data
	 */
	public String createMessage(String playerName, Domino domino, String trainOwner) {
		String message = this.getClass().getName() + MAIN_DELIM +
			playerName + MAIN_DELIM +
			generateDominoMessage(domino) + MAIN_DELIM +
			trainOwner;
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<MexicanTrainGUI> getActionTypeClass() {
		return MexicanTrainGUI.class;
	}
	
	@Override
	public String[] performAction(final MexicanTrainGUI gui) {
		String[] dataMessages = getMessageWithoutClassHeader().split(MAIN_DELIM);
		final String playerName = dataMessages[0];
		final Domino domino = parseDomino(dataMessages[1]);
		final String trainOwner = dataMessages[2];
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.dominoPlayed(playerName, domino, trainOwner);
			}
		});
		return null;
	}
}
