package action;

import java.util.LinkedHashMap;

import javax.swing.SwingUtilities;

import gui.ParticipantGUI;

/**
 * Action for beginning a round and relaying the dealt domino count of each player.
 */
public class BeginRoundAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public BeginRoundAction() {}
	
	/**
	 * Generates and returns a Begin Round Action message using the given data.
	 * @param playerDominoCountMap players names mapped to the number of dominoes they were dealt
	 * @param boneyardRemainingCount number of dominoes remaining in the boneyard
	 * @return a Begin Round Action message generated from the given data
	 */
	public String createMessage(LinkedHashMap<String, Integer> playerDominoCountMap) {
		String message = this.getClass().getName();
		for (String playerName : playerDominoCountMap.keySet())
			message += MAIN_DELIM + playerName + INNER_DELIM + playerDominoCountMap.get(playerName);
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(final ParticipantGUI gui) {
		String[] playerDominoCountMessages = getMessageWithoutClassHeader().split(MAIN_DELIM);
		
		final LinkedHashMap<String, Integer> playerDominoCountMap = new LinkedHashMap<String, Integer>();
		for (String playerDominoCountMessage : playerDominoCountMessages) {
			String[] playerDominoCountData = playerDominoCountMessage.split(INNER_DELIM);
			playerDominoCountMap.put(playerDominoCountData[0], Integer.parseInt(playerDominoCountData[1]));
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.setPlayerDominoCounts(playerDominoCountMap);
				gui.displayNewRound();
			}
		});
		return null;
	}
}
