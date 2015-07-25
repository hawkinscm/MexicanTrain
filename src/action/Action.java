package action;

import model.Domino;
import model.Player;
import model.PlayerType;
import gui.Messenger;

/**
 * Abstract class representing an action that can be communicated via text across a network.
 * Every class that inherits Action must have an empty constructor in order for the parseAction method to work.
 */
public abstract class Action<T> {

	private String message;
	
	protected final static String MAIN_DELIM = ";";
	protected final static String INNER_DELIM = ",";
	
	/**
	 * Sets the message for this action.
	 * @param message action message to set
	 */
	protected void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Returns the action message without the action class specifier heading.
	 * @return the action message without the action class specifier heading
	 */
	protected String getMessageWithoutClassHeader() {
		int firstDividerIndex = message.indexOf(MAIN_DELIM);
		if (firstDividerIndex == -1)
			return MAIN_DELIM;
		return message.substring(firstDividerIndex + 1);
	}
	
	/**
	 * Parses and returns a player from the given player message.
	 * @param playerMessage player message to parse
	 * @return the parsed player
	 */
	protected Player parsePlayer(String playerMessage) {
		String[] playerData = playerMessage.split(INNER_DELIM);
		return new Player(playerData[0], PlayerType.parseType(playerData[1]));
	}
	
	/**
	 * Returns the player in action message format.
	 * @param player the player to convert
	 * @return the player in action message format
	 */
	protected String generateMessage(Player player) {
		return MAIN_DELIM + player.getName() + INNER_DELIM + player.getPlayerType().toString();
	}
		
	/**
	 * Parses and returns a domino from the given domino message.
	 * @param dominoMessage domino message to parse
	 * @return the parsed domino
	 */
	protected Domino parseDomino(String dominoMessage) {
		if (dominoMessage.startsWith("null"))
			return null;
		
		String[] endCounts = dominoMessage.split(INNER_DELIM);
		return new Domino(Integer.parseInt(endCounts[0]), Integer.parseInt(endCounts[1]));
	}
	
	/**
	 * Creates a data message from the given domino.
	 * @param domino domino to convert to a string
	 * @return a data message from the given domino
	 */
	protected String generateDominoMessage(Domino domino) {
		if (domino == null)
			return "null";
		
		return domino.getEndOneCount() + INNER_DELIM + domino.getEndTwoCount();
	}

	/**
	 * Returns this action as a string message.
	 * @return this action as a string message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Performs this action using the given object.
	 * @param obj object needed to performing the action.
	 * @return return an array of reply messages or null if not applicable
	 */
	public abstract String[] performAction(T obj);
	
	/**
	 * Returns the class that the class requires in order to handle a message.
	 * @return the class that the class requires in order to handle a message
	 */
	public abstract Class<T> getActionTypeClass();
		
	/**
	 * Parses the given action message then creates and returns an Action ready to be performed.
	 * @param actionMessage action message to parse
	 * @return the parsed Action
	 */
	public static Action<?> parseAction(String actionMessage) {
		try {
			String className = actionMessage;
			int classNameEndIndex = actionMessage.indexOf(MAIN_DELIM);
			if (classNameEndIndex > 0)
				className = actionMessage.substring(0, classNameEndIndex);
			try {
				Action<?> parsedAction = (Action<?>)Class.forName(className).newInstance();
				parsedAction.setMessage(actionMessage);
				return parsedAction;
			}
			catch (ClassNotFoundException ex) {
				return new Action<Object>() {					
					@Override
					public String[] performAction(Object obj) {
						return null;
					}

					@Override
					public Class<Object> getActionTypeClass() {
						return Object.class;
					}
					
				};
			}
		}
		catch (Exception ex) {
			Messenger.error(ex, ex.getMessage() + ": \"" + actionMessage + "\"", "Illegal Action Message");
		}
		
		return null;
	}
}
