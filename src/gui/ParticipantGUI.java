package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import socket.ParticipantSocket;

import action.Action;
import action.DrawDominoAction;
import action.EndPlayerTurnAction;
import action.PlayDominoAction;

import model.Domino;
import model.Player;
import model.PlayerType;

/**
 * GUI used by a player joining a hosted game.  Handles/directs all display for the participating player.
 */
public class ParticipantGUI extends MexicanTrainGUI {
	private static final long serialVersionUID = 1L;
	
	private ParticipantSocket socket;

	/**
	 * Creates a new Participant GUI.
	 */
	public ParticipantGUI() {
		socket = null;
		
		newGameMenuItem.setText("Join Game");
		newGameMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				joinGame();
			}
		});
		replacePlayerMenuItem.setVisible(false);
		optionsMenuItem.setVisible(false);
		
		joinGame();
	}
	
	/**
	 * Joins a new game.
	 */
	private void joinGame() {
		// If a game is already in process, prompt the user to see if they want to end it and begin anew
		if (socket != null && !socket.isCleanlyClosed()) {
			String message = "Would you like to drop the host and join a different game?";
			int choice = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}
		
		if (socket != null)
			socket.close();
		String defaultPlayerName = (defaultPlayers.isEmpty()) ? null : defaultPlayers.getFirst().getName();
		JoinGameDialog dialog = new JoinGameDialog(this, defaultPlayerName, defaultIP);
		dialog.setVisible(true);
		
		socket = dialog.getParticipantSocket();
		if (socket == null)
			return;
		defaultIP = socket.getHost();
		
		isGameOver = false;
		controlledPlayer = new Player(dialog.getPlayerName(), PlayerType.NETWORK);
		if (defaultPlayers.isEmpty())
			defaultPlayers.add(new Player(controlledPlayer.getName(), PlayerType.NETWORK));
		else
			defaultPlayers.set(0, new Player(controlledPlayer.getName(), PlayerType.NETWORK));
		
		Thread socketListenerThread = new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				while (true) {
					String actionMessage = socket.getActionMessage();
					if (actionMessage == null) {
						if (socket.isCleanlyClosed())
							break;
						
						String message = "Lost connection to host: would you like to reconnect?";
						int choice = JOptionPane.showConfirmDialog(ParticipantGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						if (choice != JOptionPane.YES_OPTION) {
							socket.close();
							break;
						}
						while (true) {
							try {
								socket.reconnect(controlledPlayer.getName());
								break;
							}
							catch (IOException ex) {
								message = "Unable to connect to host: would you like to try again?";
								choice = JOptionPane.showConfirmDialog(ParticipantGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
								if (choice != JOptionPane.YES_OPTION) {
									socket.close();
									isGameOver = true;
									return;
								}
							}
						}
						continue;
					}
					
					@SuppressWarnings("rawtypes")
					Action action = Action.parseAction(actionMessage);
					String[] replyMessages = action.performAction(ParticipantGUI.this);
					
					if (replyMessages != null)
						for (String replyMessage : replyMessages)
							socket.sendActionMessage(replyMessage);
				}
				
				isGameOver = true;
			}
		};
		socketListenerThread.start();
		
		Messenger.display("Display will appear as soon as the host starts the game.", "FYI", this);
	}
	
	@Override
	protected void exitProgram() {
		if (socket != null && !isGameOver) {
			int choice = JOptionPane.showConfirmDialog(this, "Disconnect from host and exit program?", "Quit", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}
		
		if (socket != null)
			socket.close();
		updateConfigFile();
				
		this.dispose();
		System.exit(0);
	}
	
	@Override
	public void play(String playerName, Domino domino, String trainOwner) {
		socket.sendActionMessage(new PlayDominoAction().createMessage(playerName, domino, trainOwner));
	}
	
	@Override
	public boolean draw(String playerName) {
		if (playerDominoCountMap.get(BONEYARD_NAME) <= 0)
			return false;
		
		socket.sendActionMessage(new DrawDominoAction().createMessage(playerName));
		return true;
	}

	@Override
	public void endTurn(String playerName, boolean hasPlayedDomino) {
		socket.sendActionMessage(new EndPlayerTurnAction().createMessage(playerName, hasPlayedDomino));
	}
				
	/**
	 * Adds a domino to the player's dominoes and adds it to the display.
	 * @param domino domino to add
	 */
	public void addDomino(Domino domino) {
		controlledPlayer.addDomino(domino);
		displayAddDomino(domino);
	}
	
	/**
	 * Sets the player's dominoes and displays them.
	 * @param dominoes dominoes to set
	 */
	public void setDominoes(List<Domino> dominoes) {
		controlledPlayer.setDominoes(dominoes);
		displaySetDominoes();
	}
}
