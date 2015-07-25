package gui;

import gui.domino.DominoDisplay;
import gui.domino.PlayerDominoesDialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import log.ErrorWriter;
import model.Boneyard;
import model.Domino;
import model.Player;
import model.PlayerType;
import model.ScoreKeeper;

/**
 * Main GUI for Mexican Train games.
 */
public abstract class MexicanTrainGUI extends JFrame implements PlayManager {
	private static final long serialVersionUID = 1L;
	
	public static final String MEXICAN_TRAIN_NAME = "MEXICAN_TRAIN";
	public static final String BONEYARD_NAME = "BONEYARD";
	
	private final String configFile = "Mexican_Train.cfg";
	
	protected JMenuItem newGameMenuItem;
	protected JMenuItem replacePlayerMenuItem;
	protected JMenuItem endRoundMenuItem;
	protected JMenuItem optionsMenuItem;
	protected JMenuItem drawMenuItem;
	
	private PlayerDominoesDialog playerDominoPanel;
	private JLabel boneyardLabel;
	private CustomButton drawButton;
	private CustomButton endTurnButton;
	private int trainDominoEndSize = DominoDisplay.DOMINO_END_MAX_SIZE;
	
	protected String defaultIP;
	protected LinkedList<Player> defaultPlayers;
	private PlayerDominoesDialog.DominoesDisplaySettings dominoesDisplaySettings;
	
	protected Player controlledPlayer;
	protected ScoreKeeper scoreKeeper;
	protected LinkedHashMap<String, Integer> playerDominoCountMap;
	private LinkedHashMap<String, JLabel> playerLabelMap;
	protected LinkedHashMap<String, TrainPanel> playerTrainMap;
	private HashMap<String, Timer> playerTimers;
	
	protected boolean isControlledPlayerTurn;
	private boolean hasPlayedThisTurn;
	protected TurnType currentTurnType;
	protected String satisfyDoubleTrainOwner;
	protected boolean isGameOver;
	
	protected boolean isOptionMexicanTrainExtraTurnSelected;
	
	/**
	 * Enumeration of turn types which help determine legal plays.
	 */
	public enum TurnType {
		NORMAL,
		FIRST,
		MEXICAN_TRAIN_ONLY,
		SATISFY_DOUBLE;
	}
	
	/**
	 * Creates a new Mexican Train GUI.
	 */
	public MexicanTrainGUI() {
		super("MEXICAN TRAIN");
		isGameOver = true;
		
		// Changes the default icon displayed in the title bar
		setIconImage(ImageHelper.getTrainIcon().getImage());
		
		setLocation(0, 0);
		setSize(800, 685);
		setMinimumSize(new Dimension(575, 600));
		setResizable(true);		
		
		// Cause the application to call a special exit method on close
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitProgram();
			}			
		});
						
		defaultPlayers = new LinkedList<Player>();
		defaultIP = null;
		dominoesDisplaySettings = new PlayerDominoesDialog.DominoesDisplaySettings();
		isOptionMexicanTrainExtraTurnSelected = false;
		loadConfigFile();
				
		try {
			ImageHelper.loadImages();
		}
		catch (Exception ex) {
			Messenger.error(ex, "Unable to read internal JAR image files.", "Corrupted JAR");
			exitProgram();
		}
		catch (Error error) {
			Messenger.error("Unable to read internal JAR image files.", "Corrupted JAR");
			exitProgram();
		}
		
		createMenuBar();	
		setVisible(true);
	}
	
	/**
	 * Initializes and sets up the main menu bar.
	 */
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
			
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		
		// new game
		newGameMenuItem = new JMenuItem("New Game");
		newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F2, 0));
		
		// replace player
		replacePlayerMenuItem = new JMenuItem("Replace Player");
		replacePlayerMenuItem.setVisible(false);
		
		// end Round
		endRoundMenuItem = new JMenuItem("End Round");
		endRoundMenuItem.setVisible(false);
				
		// view rules
		JMenuItem rulesMenuItem = new JMenuItem("Rules");
		rulesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayRules();
			}
		});
		
		// set options
		optionsMenuItem = new JMenuItem("Options");
		optionsMenuItem.setVisible(false);
				
		// quit
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}
		});
		
		gameMenu.add(newGameMenuItem);
		gameMenu.add(replacePlayerMenuItem);
		gameMenu.add(endRoundMenuItem);
		gameMenu.addSeparator();
		gameMenu.add(rulesMenuItem);
		gameMenu.add(optionsMenuItem);
		gameMenu.add(quitMenuItem);
		
		// score sheet menu
		JMenuItem scoreSheetMenuItem = new JMenuItem("Score-Sheet");
		scoreSheetMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(new ScoreSheetDialog(MexicanTrainGUI.this, scoreKeeper)).setVisible(true);
			}
		});
		scoreSheetMenuItem.setMinimumSize(new Dimension(250, 21));
		scoreSheetMenuItem.setMaximumSize(new Dimension(250, 21));
		scoreSheetMenuItem.setOpaque(false);
						
		menuBar.add(gameMenu);
		menuBar.add(scoreSheetMenuItem);
		setJMenuBar(menuBar);
	}
	
	/**
	 * Initializes a new game.
	 */
	public void initializeGame(String[] playerNames) {
		
		getContentPane().removeAll();
		for (ComponentListener listener : getContentPane().getComponentListeners())
			getContentPane().removeComponentListener(listener);
		
		int startingPipNumber = Boneyard.getMaxEndPipOnDominoSet(playerNames.length);
		isGameOver = false;
		satisfyDoubleTrainOwner = null;
		scoreKeeper = new ScoreKeeper(Arrays.asList(playerNames), startingPipNumber);
		
		if (playerDominoPanel != null && playerDominoPanel.isVisible()) {
			dominoesDisplaySettings = playerDominoPanel.buildDominoesDisplaySettings();
			playerDominoPanel.dispose();
		}
		playerDominoPanel = new PlayerDominoesDialog(this, dominoesDisplaySettings);
		playerDominoPanel.setVisible(true);
				
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		boneyardLabel = new JLabel();
		getContentPane().add(boneyardLabel, c);
		
		c.gridx++;
		drawButton = new CustomButton("Draw Domino") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				drawButton.setEnabled(false);
				endTurnButton.setEnabled(true);
				draw(controlledPlayer.getName());
			}
		};
		drawButton.setEnabled(false);
		getContentPane().add(drawButton, c);
		
		c.gridx++;
		endTurnButton = new CustomButton("End Turn") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				endTurnButton.setEnabled(false);
				isControlledPlayerTurn = false;
				endTurn(controlledPlayer.getName(), hasPlayedThisTurn);
			}
		};
		endTurnButton.setEnabled(false);
		getContentPane().add(endTurnButton, c);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 3;
		c.weightx = 1.0;
		
		playerLabelMap = new LinkedHashMap<String, JLabel>();
		playerTrainMap = new LinkedHashMap<String, TrainPanel>();
		playerTimers = new HashMap<String, Timer>();
		for (String playerName : playerNames) {
			c.insets.top = 5;
			c.insets.bottom = 0;
			JLabel playerLabel = new JLabel(" ");
			getContentPane().add(playerLabel, c);
			playerLabelMap.put(playerName, playerLabel);
			c.gridy++;
			
			c.insets.top = 0;
			c.insets.bottom = 5;
			TrainPanel trainPanel = new TrainPanel(this, playerName);
			getContentPane().add(trainPanel, c);
			playerTrainMap.put(playerName, trainPanel);
			c.gridy++;
		}
		
		c.insets = new Insets(15, 5, 0, 5);
		getContentPane().add(new JLabel("MEXICAN TRAIN"), c);
		c.gridy++;
		c.insets.top = 0;
		c.insets.bottom = 5;
		TrainPanel mexicanTrainPanel = new TrainPanel(this, MEXICAN_TRAIN_NAME);
		getContentPane().add(mexicanTrainPanel, c);
		playerTrainMap.put(MEXICAN_TRAIN_NAME, mexicanTrainPanel);
		
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				resizeDisplay();
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			public void componentShown(ComponentEvent e) {}
		});
		
		validate();
		repaint();
	}
	
	/**
	 * Resizes the display and trains to fit in the GUI window.
	 */
	private void resizeDisplay() {
		int numPlayers = scoreKeeper.getPlayerNames().size();
		int usedHeight = 165 + 35 * numPlayers;
		int availableHeight = getSize().height - usedHeight;
		int availableHeightPerTrain = Math.max(16, availableHeight / (numPlayers + 1));
		
		int longestTrain = 0;
		for (TrainPanel trainPanel : playerTrainMap.values())
			if (trainPanel.getTrainDominoes().size() > longestTrain)
				longestTrain = trainPanel.getTrainDominoes().size();
		longestTrain++;
		int usedWidth = 30 + (10 * longestTrain);
		int availableWidth = getSize().width - usedWidth;
		int availableWidthPerDominoEnd = Math.max(16, availableWidth / (longestTrain * 2));
		int smallestBorderThreshold = DominoDisplay.DOMINO_END_MAX_SIZE / 3;
		int midBorderThreshold = DominoDisplay.DOMINO_END_MAX_SIZE / 2;
		if (availableWidthPerDominoEnd <= smallestBorderThreshold) {
			int maxAvailable = smallestBorderThreshold - availableWidthPerDominoEnd;
			availableWidthPerDominoEnd += Math.min(maxAvailable, (4 * longestTrain) / (longestTrain * 2));
		}
		else if (availableWidthPerDominoEnd <= midBorderThreshold) {
			int maxAvailable = midBorderThreshold - availableWidthPerDominoEnd;
			availableWidthPerDominoEnd += Math.min(maxAvailable, (2 * longestTrain) / (longestTrain * 2));
		}
		
		int newDominoEndSize = Math.min(DominoDisplay.DOMINO_END_MAX_SIZE, Math.min(availableHeightPerTrain, availableWidthPerDominoEnd));
		if (trainDominoEndSize != newDominoEndSize) {
			trainDominoEndSize = newDominoEndSize;
			for (TrainPanel trainPanel : playerTrainMap.values())
				trainPanel.resizeDominoes(trainDominoEndSize);
		}
	}
	
	/**
	 * Sets the mapping between player names and the number of dominoes they have.
	 * @param playerDominoCountMap the map to set
	 */
	public void setPlayerDominoCounts(LinkedHashMap<String, Integer> playerDominoCountMap) {
		this.playerDominoCountMap = playerDominoCountMap;
		for (String playerName : playerDominoCountMap.keySet()) {
			if (playerName.equals(BONEYARD_NAME))
				boneyardLabel.setText("Boneyard: " + playerDominoCountMap.get(playerName) + " remaining");
			else
				playerLabelMap.get(playerName).setText(playerName + ": " + playerDominoCountMap.get(playerName) + " Dominoes");
		}
	}
	
	/**
	 * Starts the next Player's turn.
	 */
	public void setPlayerTurn(String playerName, TurnType turnType) {
		if (turnType == TurnType.NORMAL) {
			playerTrainMap.get(MEXICAN_TRAIN_NAME).setPublicTrain(true);
		}
		
		isControlledPlayerTurn = (controlledPlayer.getName().equals(playerName));
		if (isControlledPlayerTurn) {
			if (hasPlayedThisTurn || playerDominoCountMap.get(BONEYARD_NAME) == 0)
				endTurnButton.setEnabled(true);
			else
				drawButton.setEnabled(true);
		}
		else {
			endTurnButton.setEnabled(false);
			hasPlayedThisTurn = false;
		}
		currentTurnType = turnType;
			
		JLabel controlledPlayerLabel = playerLabelMap.get(playerName);
		if (controlledPlayerLabel != null) {
			for (JLabel playerLabel : playerLabelMap.values())
				playerLabel.setIcon(null);
			controlledPlayerLabel.setIcon(ImageHelper.getTurnIcon());
		}
	}
	
	/**
	 * Clears the display and updates it for a new round
	 */
	public void displayNewRound() {
		int pipRoundNumber =  scoreKeeper.getPipRoundNumber();
		for (TrainPanel trainPanel : playerTrainMap.values())
			trainPanel.restartTrain(pipRoundNumber);
		drawButton.setEnabled(false);
		endTurnButton.setEnabled(false);
		hasPlayedThisTurn = false;
		resizeDisplay();
	}
	
	/**
	 * Adds the round scores for each player to the score keeper and displays the score sheet.
	 * @param playerScores map of each player to his score for the finished round.
	 */
	public void addRoundScores(LinkedHashMap<String, Integer> playerScores, boolean display) {
		scoreKeeper.addPlayerRoundScores(playerScores);
		if (scoreKeeper.getPipRoundNumber() < 0)
			isGameOver = true;
		
		if (display)
			(new ScoreSheetDialog(this, scoreKeeper)).setVisible(true);
	}

	/**
	 * Allows special exit handling to be performed on exit - prompt the user if they want to end the current game.
	 */
	protected abstract void exitProgram();
	
	@Override
	public TurnType getCurrentTurnType() {
		return currentTurnType;
	}
	
	@Override
	public LinkedHashMap<String, TrainPanel> getPlayerTrainMap() {
		return playerTrainMap;
	}
	
	@Override
	public boolean isOptionMexicanTrainExtraTurnSelected() {
		return isOptionMexicanTrainExtraTurnSelected;
	}
	
	@Override
	public String getSatisfyDoubleTrainOwner() {
		return satisfyDoubleTrainOwner;
	}
	
	@Override
	public boolean canPlay(Domino domino, String trainOwner) {
		if (!isControlledPlayerTurn) {
			Messenger.error("It is not your turn.", "Illegal Domino Play");
			return false;
		}
		
		TrainPanel train = playerTrainMap.get(trainOwner);
		if (currentTurnType == TurnType.SATISFY_DOUBLE) {
			if (!trainOwner.equals(satisfyDoubleTrainOwner)) {
				String trainName = (satisfyDoubleTrainOwner.equals(MEXICAN_TRAIN_NAME)) ? "the mexican train." : (satisfyDoubleTrainOwner + "'s train.");
				Messenger.error("You can only play to satisfy the open double on " + trainName, "Illegal Domino Play");
				return false;
			}
		}
		else if (currentTurnType == TurnType.MEXICAN_TRAIN_ONLY) {
			if (!trainOwner.equals(MEXICAN_TRAIN_NAME)) {
				Messenger.error("You can only play on the mexican train.", "Illegal Domino Play");
				return false;
			}
		}
		else if (currentTurnType == TurnType.FIRST) {
			if (!controlledPlayer.getName().equals(trainOwner)) {
				Messenger.error("You can only play on your own train.", "Illegal Domino Play");
				return false;
			}
		}
		else if (!controlledPlayer.getName().equals(trainOwner) && !trainOwner.equals(MEXICAN_TRAIN_NAME) && !train.isPublicTrain()) {
			Messenger.error("You cannot play on a another player's private train (marked in red).", "Illegal Domino Play");
			return false;
		}
		
		if (domino.getEndOneCount() != train.getRequiredEndPipCount() && domino.getEndTwoCount() != train.getRequiredEndPipCount()) {
			Messenger.error("You can only play a domino here that has " + train.getRequiredEndPipCount() + " pips on one end.", "Illegal Domino Play");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void play(Domino domino, String trainOwner) {
		if (!canPlay(domino, trainOwner))
			return;
		
		isControlledPlayerTurn = false;
		hasPlayedThisTurn = true;
		drawButton.setEnabled(false);
		controlledPlayer.removeDomino(domino);
		
		play(controlledPlayer.getName(), domino, trainOwner);
	}
	
	/**
	 * Handles display and logic for a played domino.
	 * @param playerName name of player who played domino
	 * @param domino domino that was played
	 * @param trainOwner owner of the train where the domino was played
	 */
	public void dominoPlayed(String playerName, Domino domino, String trainOwner) {
		updatePlayerDisplay(playerName, -1);
		TrainPanel train = playerTrainMap.get(trainOwner);
		train.addDomino(domino);
		if (playerName.equals(trainOwner))
			train.setPublicTrain(false);
		if (domino.isDouble() && currentTurnType != TurnType.FIRST)
			satisfyDoubleTrainOwner = trainOwner;
		else
			satisfyDoubleTrainOwner = null;
		
		resizeDisplay();
	}
	
	/**
	 * Handles display and logic for a drawn domino.
	 * @param playerName name of the player who drew a domino
	 */
	public void dominoDrawn(String playerName) {
		updatePlayerDisplay(playerName, 1);
	}
	
	/**
	 * Handles display and logic for a player's turn end.
	 * @param playerName name of the player whose turn ended
	 * @param hasPlayedDomino whether or not the player played a domino during his turn
	 */
	public void playerTurnEnded(String playerName, boolean hasPlayedDomino) {
		if (!hasPlayedDomino)
			playerTrainMap.get(playerName).setPublicTrain(true);
	}
	
	/**
	 * Reloads the display of the player's dominoes.
	 */
	public void displaySetDominoes() {
		playerDominoPanel.setDominoes(controlledPlayer.getDominoes());
	}
	
	/**
	 * Displays an addition domino with the player's domino display.
	 * @param domino new domino to add to the display
	 */
	public void displayAddDomino(Domino domino) {
		playerDominoPanel.addDomino(domino);
	}
	
	/**
	 * Updates the player label.
	 * @param playerName name of the player to update
	 * @param dominoChangeCount amount of dominoes that have been added/removed from the player; added if positive, removed if negative
	 */
	private void updatePlayerDisplay(final String playerName, int dominoChangeCount) {
		Integer playerDominoCount = playerDominoCountMap.get(playerName);
		if (playerDominoCount == null)
			return;
		
		playerDominoCount += dominoChangeCount;
		playerDominoCountMap.put(playerName, playerDominoCount);
		final JLabel playerLabel = playerLabelMap.get(playerName);
		if (dominoChangeCount == 1) {
			playerLabel.setText(playerName + ": " + playerDominoCount + " Dominoes (+1 DRAW)");
			playerLabel.setForeground(Color.BLUE);
			Timer timer = new Timer(1500, new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent arg0) {
					playerLabel.setForeground(Color.BLACK);
					playerLabel.setText(playerName + ": " + playerDominoCountMap.get(playerName) + " Dominoes");
				}
			});
			timer.setRepeats(false);
			playerTimers.put(playerName, timer);
			timer.start();
		}
		else
			playerLabel.setText(playerName + ": " + playerDominoCountMap.get(playerName) + " Dominoes");
		
		if (dominoChangeCount > 0) {
			playerDominoCountMap.put(BONEYARD_NAME, playerDominoCountMap.get(BONEYARD_NAME) - dominoChangeCount);
			boneyardLabel.setText("Boneyard: " + playerDominoCountMap.get(BONEYARD_NAME) + " remaining");
		}
	}
	
		
	/**
	 * Displays the rules of the game using the default text file reader.
	 */
	private void displayRules() {
		try {
			if (Desktop.isDesktopSupported())
				Desktop.getDesktop().open(new File("rules.txt"));
		}
		catch (IOException ex) {
			Messenger.error(ex.getMessage(), "File Error", this);
		}
	}
		
	/**
	 * Loads the configuration defaults and settings from the config file.
	 */
	private void loadConfigFile() {
		File file = new File(configFile);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(";");
					if (tokens.length == 4) {
						try {
							Dimension startSize = new Dimension();
							Point startLocation = getLocation();
							for (int tokenIdx = 0; tokenIdx < tokens.length; tokenIdx++) {
								String[] windowParams = tokens[tokenIdx].split(":");
								if (windowParams.length != 2)
									break;
								
								if (windowParams[0].equalsIgnoreCase("startX"))
									startLocation.x = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startY"))
									startLocation.y = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startWidth"))
									startSize.width = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startHeight"))
									startSize.height = Integer.parseInt(windowParams[1]);
							}							
							setLocation(startLocation);
							if (startSize.width > 20 && startSize.height > 20)
								setSize(startSize);
						}
						catch (NumberFormatException ex) {}
						
						continue;
					}
					
					if (line.startsWith(":HostIP:=")) {
						try {
							defaultIP = line.substring(line.indexOf('=') + 1);
						}
						catch (Exception ex) {}
						
						continue;
					}
										
					if (line.startsWith(":MexicanTrainIsExtraTurn:=")) {
						try {
							isOptionMexicanTrainExtraTurnSelected = Boolean.parseBoolean(line.substring(line.indexOf('=') + 1));
						}
						catch (Exception ex) {}
						
						continue;
					}
					
					if (line.startsWith(":DominoesDisplaySettings:=")) {
						try {
							String[] settings = line.substring(line.indexOf('=') + 1).split(";");
							if (settings.length != 6)
								continue;
							
							Point location = new Point(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]));
							Dimension size = new Dimension(Integer.parseInt(settings[2]), Integer.parseInt(settings[3]));
							boolean limitByColumn = Boolean.parseBoolean(settings[4]);
							int dominoLineLimit = Integer.parseInt(settings[5]);
							dominoesDisplaySettings.location = location;
							dominoesDisplaySettings.size = size;
							dominoesDisplaySettings.limitByColumn = limitByColumn;
							dominoesDisplaySettings.dominoLineLimit = dominoLineLimit;
						}
						catch (Exception ex) {}
						
						continue;
					}
					
					tokens = line.split(":");
					if (tokens.length < 2)
						continue;
					
					PlayerType type = PlayerType.parseType(tokens[1]);
					if (type == null)
						continue;
					
					defaultPlayers.add(new Player(tokens[0], type));
				}
				reader.close();
			} catch (IOException ex) {}
		}
	}
	
	/**
	 * Writes the latest configuration defaults and settings to the config file.
	 */
	protected void updateConfigFile() {
		if (playerDominoPanel != null && playerDominoPanel.isVisible())
			dominoesDisplaySettings = playerDominoPanel.buildDominoesDisplaySettings();
		
		File file = new File(configFile);
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("startX:" + getLocation().x + ";");
			writer.write("startY:" + getLocation().y + ";");
			writer.write("startWidth:" + getSize().width + ";");
			writer.write("startHeight:" + getSize().height);
			writer.newLine();
			
			if (defaultIP != null) {
				writer.write(":HostIP:=");
				writer.write(defaultIP);
				writer.newLine();
			}
			
			writer.write(":MexicanTrainIsExtraTurn:=" + isOptionMexicanTrainExtraTurnSelected);
			writer.newLine();
			
			writer.write(":DominoesDisplaySettings:=" + dominoesDisplaySettings.location.x);
			writer.write(";" + dominoesDisplaySettings.location.y);
			writer.write(";" + dominoesDisplaySettings.size.width);
			writer.write(";" + dominoesDisplaySettings.size.height);
			writer.write(";" + dominoesDisplaySettings.limitByColumn);
			writer.write(";" + dominoesDisplaySettings.dominoLineLimit);
			writer.newLine();
			
			writer.newLine();
						
			for (Player player : defaultPlayers) {
				writer.write(player.getName().replace(':', ' ') + ":" + player.getPlayerType());
				writer.newLine();
			}
			
			writer.close();
		} catch (IOException ex) {}
	}
		
	/**
	 * Main class that is first called and starts running the GUI and thereby the program.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			ErrorWriter.beginLogging("Mexican_Train.log");
		}
		catch (IOException ex) {
			Messenger.error(ex, "Failed to create/open error log file", "Log Error");
		}
		
		// Run the GUI in a thread safe environment
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					String prompt = "Would you like to host or join a Mexican Train game?";
					ImageIcon icon = ImageHelper.getTrainIcon();
					
					String[] options = {"Host", "Join", "Exit Program"};
					int result = JOptionPane.showOptionDialog(null, prompt, "Mexican Train v1.3", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, "Host");
					if (result == JOptionPane.YES_OPTION)
						new HostGUI();
					else if (result == JOptionPane.NO_OPTION)
						new ParticipantGUI();
				}
			}
		);
	}
}