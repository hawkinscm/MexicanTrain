package action;

import java.util.LinkedHashMap;

import gui.ParticipantGUI;

/**
 * Relays the scores for the last round.
 */
public class AddRoundScoresAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public AddRoundScoresAction() {}
	
	/**
	 * Generates and returns an Add Round Scores Action message using the given data.
	 * @param playerScores the round scores for each player which will be written as data in the message
	 * @param shouldDisplay whether or not the scores should be immediately displayed
	 * @return an Add Round Scores Action message generated from the given data
	 */
	public String createMessage(LinkedHashMap<String, Integer> playerScores, boolean shouldDisplay) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + shouldDisplay;
		for (String playerName : playerScores.keySet())
			message += MAIN_DELIM + playerName + INNER_DELIM + playerScores.get(playerName);
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] actionData = getMessageWithoutClassHeader().split(MAIN_DELIM);
		boolean shouldDisplay = Boolean.parseBoolean(actionData[0]);
		LinkedHashMap<String, Integer> playerScores = new LinkedHashMap<String, Integer>();
		for (int actionIndex = 1; actionIndex < actionData.length; actionIndex++) {
			String[] playerRoundScore = actionData[actionIndex].split(INNER_DELIM);
			playerScores.put(playerRoundScore[0], Integer.parseInt(playerRoundScore[1]));
		}
		gui.addRoundScores(playerScores, shouldDisplay);
		
		return null;
	}
}
