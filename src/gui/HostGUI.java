package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import action.*;
import ai.AIManager;

import socket.PlayerSocket;

import model.Boneyard;
import model.Domino;
import model.Player;
import model.PlayerType;
import model.ScoreKeeper;

/**
 * GUI used by the player hosting the game. Handles/directs all display for the host.
 */
public class HostGUI extends MexicanTrainGUI {
	private static final long serialVersionUID = 1L;

	public static final int MAX_NUM_PLAYERS = 8;
	
	private LinkedList<PlayerSocket> playerSockets;

	private LinkedList<Player> players;
	private Player currentPlayer;
	private Boneyard boneyard;
	private Player firstPlayer;
	
	/**
	 * Constructor for the Host GUI
	 */
	public HostGUI() {
		playerSockets = new LinkedList<PlayerSocket>();
		currentPlayer = null;
		
		newGameMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		
		replacePlayerMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replacePlayer(null);
			}
		});
		replacePlayerMenuItem.setVisible(true);
		
		endRoundMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!boneyard.isEmpty()) {
					Messenger.display("You can only use this after all the dominoes have been drawn from the boneyard.", "End Round", HostGUI.this);
					return;
				}
				
				String message = "<html> This will end the round and tally scores from players' remaining dominoes. <br>" +
				                 " As host, you should not do this unless all players agree that they can no longer play.</html>";
				int result = JOptionPane.showConfirmDialog(HostGUI.this, message, "End Round", JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION)
					endRound();
			}
		});
		endRoundMenuItem.setVisible(true);

		optionsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayOptions();
			}
		});
		optionsMenuItem.setVisible(true);
		
		newGame();
	}
		
	/**
	 * Starts a new game.
	 */
	private void newGame() {
		// If a game is already in process, prompt the user to see if they want
		// to end it and begin anew
		if (!isGameOver) {
			String message = "End current game?";
			int choice = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}

		// Prompt the user for how many people will play
		int numPlayers = 0;
		while (numPlayers < 2 || numPlayers > MAX_NUM_PLAYERS) {
			try {
				String message = "How Many Will Play (2-" + MAX_NUM_PLAYERS	+ ")?";
				String result = JOptionPane.showInputDialog(this, message, "New Game", JOptionPane.QUESTION_MESSAGE);
				if (result == null)
					return;
				numPlayers = Integer.parseInt(result);
			} 
			catch (NumberFormatException ex) {}
		}

		// Lets the user define the players for the game
		LinkedList<PlayerSocket> connectedPlayerSockets = new LinkedList<PlayerSocket>();
		for (PlayerSocket playerSocket : playerSockets)
			if (!playerSocket.isCleanlyClosed())
				connectedPlayerSockets.add(playerSocket);
		NewGameDialog playerInput = new NewGameDialog(this, numPlayers,	defaultPlayers, connectedPlayerSockets);
		playerInput.setVisible(true);
		if (playerInput.getPlayers() == null) {
			newGame();
			return;
		}

		players = playerInput.getPlayers();
		controlledPlayer = players.getFirst();
		LinkedList<PlayerSocket> oldPlayerSockets = playerSockets;
		playerSockets = playerInput.getPlayerSockets();

		isGameOver = false;
		
		String[] playerNames = new String[players.size()];
		for (int playerIdx = 0; playerIdx < players.size(); playerIdx++)
			playerNames[playerIdx] = players.get(playerIdx).getName();
		initializeGame(playerNames);
		
		for (final PlayerSocket playerSocket : playerSockets) {
			if (oldPlayerSockets.contains(playerSocket))
				oldPlayerSockets.remove(playerSocket);
			else
				startPlayerSocketListener(playerSocket);
			playerSocket.sendActionMessage(new NewGameAction().createMessage(playerNames));
		}
		for (PlayerSocket playerSocket : oldPlayerSockets)
			playerSocket.close();

		boneyard = new Boneyard(players);
		beginRound();
		
		players = playerInput.getPlayers();
		for (int playerIdx = 0; playerIdx < players.size(); playerIdx++) {
			Player currentPlayer = players.get(playerIdx);

			if (currentPlayer.getPlayerType() == PlayerType.HOST)
				controlledPlayer = currentPlayer;

			if (playerIdx < defaultPlayers.size())
				defaultPlayers.set(playerIdx, currentPlayer);
			else
				defaultPlayers.add(currentPlayer);
		}
	}
	
	/**
	 * Begins a new round.
	 */
	private void beginRound() {
		firstPlayer = boneyard.deal(scoreKeeper.getPipRoundNumber());
		currentPlayer = firstPlayer;
		currentTurnType = TurnType.FIRST;
		
		LinkedHashMap<String, Integer> playerDominoCountMap = new LinkedHashMap<String, Integer>();
		for (Player player : players)
			playerDominoCountMap.put(player.getName(), player.getDominoCount());
		playerDominoCountMap.put(BONEYARD_NAME, boneyard.countRemaining());
		setPlayerDominoCounts(playerDominoCountMap);
		displayNewRound();
		notifyPlayerSockets(new BeginRoundAction().createMessage(playerDominoCountMap));
		
		displaySetDominoes();
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.sendActionMessage(new DealDominoesAction().createMessage(playerSocket.getPlayer().getDominoes()));
		
		beginCurrentPlayerTurn();
	}
	
	/**
	 * Begins the turn for the current player.
	 */
	private void beginCurrentPlayerTurn() {
		setPlayerTurn(currentPlayer.getName(), currentTurnType);
		notifyPlayerSockets(new SetPlayerTurnAction().createMessage(currentPlayer.getName(), currentTurnType));
		
		if (currentPlayer.isComputer())
			AIManager.takeTurn(this, currentPlayer);
	}
	
	@Override
	public void play(String playerName, Domino domino, String trainOwner) {
		dominoPlayed(playerName, domino, trainOwner);
	}
	
	@Override
	public void dominoPlayed(String playerName, Domino domino, String trainOwner) {
		super.dominoPlayed(playerName, domino, trainOwner);
		notifyPlayerSockets(new PlayDominoAction().createMessage(playerName, domino, trainOwner));
		Player player = getPlayer(playerName);
		
		if (player != controlledPlayer)
			player.removeDomino(domino);
				
		if (currentTurnType != TurnType.FIRST && player.getDominoCount() == 0)
			endRound();
		else if (currentTurnType == TurnType.FIRST) {
			setPlayerTurn(playerName, TurnType.FIRST);
			notifyPlayerSocket(player, new SetPlayerTurnAction().createMessage(playerName, TurnType.FIRST));
		}
		else if (domino.isDouble()) {
			setPlayerTurn(playerName, TurnType.SATISFY_DOUBLE);
			notifyPlayerSocket(player, new SetPlayerTurnAction().createMessage(playerName, TurnType.SATISFY_DOUBLE));
		}
		else if (currentTurnType == TurnType.SATISFY_DOUBLE) {
			currentTurnType = TurnType.NORMAL;
			endCurrentPlayerTurn();
		}
		else if (isOptionMexicanTrainExtraTurnSelected && currentTurnType != TurnType.SATISFY_DOUBLE && !trainOwner.equals(MEXICAN_TRAIN_NAME)) {
			setPlayerTurn(playerName, TurnType.MEXICAN_TRAIN_ONLY);
			notifyPlayerSocket(player, new SetPlayerTurnAction().createMessage(playerName, TurnType.MEXICAN_TRAIN_ONLY));
		}
		else
			endCurrentPlayerTurn();
	}
	
	@Override
	public boolean draw(String playerName) {
		if (boneyard.isEmpty())
			return false;
		
		dominoDrawn(playerName);
		return true;
	}
	
	@Override
	public void dominoDrawn(String playerName) {
		super.dominoDrawn(playerName);
		notifyPlayerSockets(new DrawDominoAction().createMessage(playerName));
		
		Domino domino = boneyard.drawDomino();
		Player player = getPlayer(playerName);
		player.addDomino(domino);
		if (player == controlledPlayer)
			displayAddDomino(domino);
		else
			notifyPlayerSocket(player, new AddDominoAction().createMessage(domino));
	}
	
	@Override
	public void endTurn(String playerName, boolean hasPlayedDomino) {
		playerTurnEnded(playerName, hasPlayedDomino);
	}
	
	@Override
	public void playerTurnEnded(String playerName, boolean hasPlayedDomino) {
		super.playerTurnEnded(playerName, hasPlayedDomino);
		notifyPlayerSockets(new EndPlayerTurnAction().createMessage(playerName, hasPlayedDomino));
		
		endCurrentPlayerTurn();
	}
	
	/**
	 * Returns the player with the given player name.
	 * @param playerName name of the player to return
	 * @return the player with the given player name
	 */
	private Player getPlayer(String playerName) {
		for (Player player : players)
			if (player.getName().equals(playerName))
				return player;
		
		return null;
	}
	
	/**
	 * Ends the current player's turn and begins that next player's turn.
	 */
	private void endCurrentPlayerTurn() {
		int playerIndex = players.indexOf(currentPlayer) + 1;
		if (playerIndex >= players.size())
			playerIndex = 0;
		currentPlayer = players.get(playerIndex);
		
		if (firstPlayer != null) {
			if (currentPlayer == firstPlayer) {
				for (Player player : players)
					if (player.getDominoCount() == 0)
						endRound();
				
				currentTurnType = TurnType.NORMAL;
				firstPlayer = null;
			}
		}
		else if (currentTurnType != TurnType.SATISFY_DOUBLE)
			currentTurnType = TurnType.NORMAL;
				
		beginCurrentPlayerTurn();
	}
	
	/**
	 * Ends the current round.
	 */
	private void endRound() {
		LinkedHashMap<String, Integer> playerRoundScores = ScoreKeeper.tallyRoundScores(players);
		notifyPlayerSockets(new AddRoundScoresAction().createMessage(playerRoundScores, true));
		addRoundScores(playerRoundScores, true);
		
		boneyard.rebuild();
		if (isGameOver)
			currentPlayer = null;
		else
			beginRound();
	}

	/**
	 * Replaces the given player with a new player type. 
	 * @param player player to change; if null, asks to user to select a player to change
	 * @param whether or not the player should stay the same type;
	 * @return returns true if the player was replaced, false otherwise.
	 */
	private boolean replacePlayer(Player player) {
		ChangePlayerTypeDialog dialog;
		if (player != null)
			dialog = new ChangePlayerTypeDialog(HostGUI.this, player);
		else if (players == null || players.isEmpty()) {
			Messenger.display("There are no players yet to replace.", "Replace Player");
			return false;
		} 
		else
			dialog = new ChangePlayerTypeDialog(HostGUI.this, players);

		dialog.setVisible(true);
		player = dialog.getChangedPlayer();
		if (player == null)
			return false;

		if (player.isComputer()) {
			Iterator<PlayerSocket> playerSocketIter = playerSockets.iterator();
			while (playerSocketIter.hasNext()) {
				PlayerSocket playerSocket = playerSocketIter.next();
				if (player == playerSocket.getPlayer()) {
					playerSocket.close();
					playerSocketIter.remove();
					if (currentPlayer == player)
						AIManager.takeTurn(this, player);
					break;
				}
			}
		} 
		else {
			if (currentPlayer == player && dialog.getOldPlayerType() != PlayerType.NETWORK) {
				int interval = 500;
				for (int time = 0; time < 6000; time += interval) {
					try {
						Thread.sleep(interval);
					} 
					catch (InterruptedException ex) { ex.printStackTrace(); }
					
					if (currentPlayer != player)
						break;
				}
			}
			
			if (!connect(player)) {
				player.setPlayerType(dialog.getOldPlayerType());
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Connects to the given player and sends him all of the current game data.
	 * @param player player to connect to.
	 * @return true if a user was successfully connected to; false otherwise
	 */
	private boolean connect(Player player) {
		PlayerSocket playerSocket = null;
		Iterator<PlayerSocket> playerSocketIter = playerSockets.iterator();
		while (playerSocketIter.hasNext()) {
			PlayerSocket currentPlayerSocket = playerSocketIter.next();
			if (currentPlayerSocket.getPlayer() == player) {
				playerSocket = currentPlayerSocket;
				playerSocketIter.remove();
				break;
			}
		}
		if (playerSocket == null)
			playerSocket = new PlayerSocket(players.indexOf(player));
		
		try {
			playerSocket.close();
			playerSocket.reconnect();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			Messenger.error("Timed Out - Unable to connect to participant",	"Replace Player");
			playerSocket.close();
			return false;
		}
		
		playerSocket.setPlayer(player);
		playerSockets.add(playerSocket);

		startPlayerSocketListener(playerSocket);
		String[] playerNames = new String[players.size()];
		for (int playerIdx = 0; playerIdx < players.size(); playerIdx++)
			playerNames[playerIdx] = players.get(playerIdx).getName();
		playerSocket.sendActionMessage(new NewGameAction().createMessage(playerNames));
		
		for (int round = 1; round <= scoreKeeper.getNumberOfRoundsFinished(); round++)
			playerSocket.sendActionMessage(new AddRoundScoresAction().createMessage(scoreKeeper.getRoundScores(round), false));
		
		playerSocket.sendActionMessage(new BeginRoundAction().createMessage(playerDominoCountMap));
		playerSocket.sendActionMessage(new DealDominoesAction().createMessage(player.getDominoes()));
		playerSocket.sendActionMessage(new SetPlayerTurnAction().createMessage(" ", TurnType.FIRST));
		
		Domino doubleDomino = null;
		if (currentTurnType == TurnType.SATISFY_DOUBLE && satisfyDoubleTrainOwner != null)
			doubleDomino = playerTrainMap.get(satisfyDoubleTrainOwner).getTrainDominoes().getLast();
		
		for (String trainOwner : playerTrainMap.keySet()) {
			TrainPanel trainPanel = playerTrainMap.get(trainOwner);
			for (Domino domino : trainPanel.getTrainDominoes()) {
				if (!domino.equals(doubleDomino))
					playerSocket.sendActionMessage(new PlayDominoAction().createMessage(" ", domino, trainOwner));
			}
			if (trainPanel.isPublicTrain() && !trainOwner.equals(MEXICAN_TRAIN_NAME))
				playerSocket.sendActionMessage(new EndPlayerTurnAction().createMessage(trainOwner, false));
		}
		
		if (doubleDomino != null) {
			playerSocket.sendActionMessage(new SetPlayerTurnAction().createMessage(" ", TurnType.NORMAL));
			playerSocket.sendActionMessage(new PlayDominoAction().createMessage(" ", doubleDomino, satisfyDoubleTrainOwner));
		}			
		
		playerSocket.sendActionMessage(new SetPlayerTurnAction().createMessage(currentPlayer.getName(), currentTurnType));
		
		return true;
	}

	/**
	 * Starts listening for messages from the given player socket.
	 * @param playerSocket player socket to listen to
	 */
	private void startPlayerSocketListener(final PlayerSocket playerSocket) {
		final HostGUI self = this;
		Thread socketListenerThread = new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				while (true) {
					String actionMessage = playerSocket.getActionMessage();
					if (actionMessage == null) {
						if (playerSocket.isCleanlyClosed())
							break;

						while (true) {
							String message = "Lost connection to participant \"" + playerSocket.getPlayer().getName() + "\": would you like to reconnect?";
							int choice = JOptionPane.showConfirmDialog(HostGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						    if (choice == JOptionPane.YES_OPTION) { 
						    	if (connect(playerSocket.getPlayer()))
						    		return;
							}
						    else if (replacePlayer(playerSocket.getPlayer()))
						    	return;
						}
					}

					@SuppressWarnings("rawtypes")
					Action action = Action.parseAction(actionMessage);
					String[] replyMessages = action.performAction(self);

					if (replyMessages != null)
						for (String replyMessage : replyMessages)
							playerSocket.sendActionMessage(replyMessage);
				}
				if (!playerSocket.isCleanlyClosed()) {
					Messenger.error("Lost connection to network player '" + playerSocket.getPlayer().getName() + "'.", "Network Error");
					playerSocket.close();
				}
			}
		};
		socketListenerThread.start();
	}
		
	/**
	 * Displays and allows the player to change game options.
	 */
	private void displayOptions() {
		OptionsDialog dialog = new OptionsDialog(this, isOptionMexicanTrainExtraTurnSelected);
		dialog.setVisible(true);
		isOptionMexicanTrainExtraTurnSelected = dialog.isExtraTurnSelected();
	}
	
	/**
	 * Notifies the player of a specified action. If the player is not a network player, this does nothing.
	 * @param player player to notify
	 * @param actionMessage action message to send to the player socket
	 */
	private void notifyPlayerSocket(Player player, String actionMessage) {
		for (PlayerSocket playerSocket : playerSockets)
			if (playerSocket.getPlayer() == player)
				playerSocket.sendActionMessage(actionMessage);
	}

	/**
	 * Notifies all player sockets of a specified action.
	 * @param actionMessage action message to send to all player sockets
	 */
	private void notifyPlayerSockets(String actionMessage) {
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.sendActionMessage(actionMessage);
	}
	
	@Override
	protected void exitProgram() {
		if (!isGameOver) {
			int choice = JOptionPane.showConfirmDialog(null, "End current game?", "Quit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}

		updateConfigFile();
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.close();

		this.dispose();
		System.exit(0);
	}
}
