package action;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import model.Domino;

import gui.ParticipantGUI;

/**
 * Action for dealing dominoes to a Network player.
 */
public class DealDominoesAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DealDominoesAction() {}
	
	/**
	 * Generates and returns a Deal Dominoes Action message using the given data.
	 * @param dominoes set of dominoes that the player has been dealt
	 * @return a Deal Dominoes Action message generated from the given data
	 */
	public String createMessage(List<Domino> dominoes) {
		String message = this.getClass().getName();
		message += MAIN_DELIM;
		for (Domino domino : dominoes)
			message += generateDominoMessage(domino) + MAIN_DELIM;
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(final ParticipantGUI gui) {
		String[] dominoMessages = getMessageWithoutClassHeader().split(MAIN_DELIM);
		
		final List<Domino> dominoes = new ArrayList<Domino>(dominoMessages.length);
		for (String dominoMessage : dominoMessages)
			dominoes.add(this.parseDomino(dominoMessage));
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.setDominoes(dominoes);
			}
		});
		return null;
	}
}
