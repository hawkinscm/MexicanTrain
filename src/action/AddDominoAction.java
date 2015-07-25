package action;

import javax.swing.SwingUtilities;

import model.Domino;

import gui.ParticipantGUI;

/**
 * Action for giving a domino to a Network player.
 */
public class AddDominoAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public AddDominoAction() {}
	
	/**
	 * Generates and returns an Add Domino Action message using the given data.
	 * @param domino domino to add to the player's dominoes
	 * @return an Add Domino Action message generated from the given data
	 */
	public String createMessage(Domino domino) {
		setMessage(this.getClass().getName() + MAIN_DELIM + generateDominoMessage(domino));
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(final ParticipantGUI gui) {
		final Domino domino = parseDomino(getMessageWithoutClassHeader());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.addDomino(domino);
			}
		});
		return null;
	}
}
