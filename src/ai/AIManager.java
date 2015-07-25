package ai;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Timer;

import model.Domino;
import model.Player;
import model.PlayerType;
import model.Randomizer;
import gui.HostGUI;
import gui.PlayManager;
import gui.TrainPanel;

/**
 * Manager for AI. 
 */
public class AIManager {
	
	// Keeping a reference to these until they have finished running may prevent the Java AWT-Event Thread daemon crash I see every now and then.
	private static List<Timer> playTimers = new LinkedList<Timer>();
	
	/**
	 * Handles the full turn of a computer player.
	 * @param playManager play manager used to evaluate and make plays
	 * @param player player whose turn it is
	 */
	public static void takeTurn(final PlayManager playManager, Player player) {
		if (!player.isComputer())
			return;
		
		List<AbstractAction> actions = new ArrayList<AbstractAction>();
		final String playerName = player.getName();
		
		if (player.getPlayerType() == PlayerType.COMPUTER_EASY) {
			List<Domino> randomOrderDominoes = new LinkedList<Domino>();
			for (Domino domino : player.getDominoes())
				randomOrderDominoes.add(Randomizer.getRandom(randomOrderDominoes.size() + 1), domino);
			
			if (playManager.getCurrentTurnType() == HostGUI.TurnType.FIRST) {
				int requiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
				Domino domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
				while (domino != null) {
					actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
					randomOrderDominoes.remove(domino);
					requiredPipEnd = (domino.getEndOneCount() == requiredPipEnd) ? domino.getEndTwoCount() : domino.getEndOneCount();
					domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
				}
				
				if (actions.isEmpty()) {
					domino = drawDomino(playManager, player);
					if (domino != null && (domino.getEndOneCount() == requiredPipEnd || domino.getEndTwoCount() == requiredPipEnd)) {
						while (domino != null) {
							actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
							randomOrderDominoes.remove(domino);
							requiredPipEnd = (domino.getEndOneCount() == requiredPipEnd) ? domino.getEndTwoCount() : domino.getEndOneCount();
							domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
						}
					}
				}
			}
			else if (playManager.getCurrentTurnType() == HostGUI.TurnType.NORMAL) {
				int requiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
				Domino domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
				if (domino != null) {
					actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
					randomOrderDominoes.remove(domino);
				}
				else {
					List<String> publicTrainOwners = getPublicTrainOwners(playManager.getPlayerTrainMap(), playerName);
					while (!publicTrainOwners.isEmpty()) {
						String trainOwner = publicTrainOwners.remove(Randomizer.getRandom(publicTrainOwners.size()));
						requiredPipEnd = playManager.getPlayerTrainMap().get(trainOwner).getRequiredEndPipCount();
						domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
						if (domino != null) {
							actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
							randomOrderDominoes.remove(domino);
							break;
						}
					}
				}
				
				if (actions.isEmpty() || (playManager.isOptionMexicanTrainExtraTurnSelected() && !((DominoPlayAction) actions.get(0)).getDomino().isDouble())) {
					requiredPipEnd = playManager.getPlayerTrainMap().get(HostGUI.MEXICAN_TRAIN_NAME).getRequiredEndPipCount();
					domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
					if (domino != null) {
						actions.add(new DominoPlayAction(playManager, playerName, domino, HostGUI.MEXICAN_TRAIN_NAME));
						randomOrderDominoes.remove(domino);
					}
				}
				
				if (actions.isEmpty()) {
					domino = drawDomino(playManager, player);
					if (domino != null) {
						requiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
						if (domino.getEndOneCount() == requiredPipEnd || domino.getEndTwoCount() == requiredPipEnd) {
							actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
							randomOrderDominoes.remove(domino);
						}
						else {
							List<String> publicTrainOwners = getPublicTrainOwners(playManager.getPlayerTrainMap(), playerName);
							publicTrainOwners.add(HostGUI.MEXICAN_TRAIN_NAME);
							while (!publicTrainOwners.isEmpty()) {
								String trainOwner = publicTrainOwners.remove(Randomizer.getRandom(publicTrainOwners.size()));
								requiredPipEnd = playManager.getPlayerTrainMap().get(trainOwner).getRequiredEndPipCount();
								if (domino.getEndOneCount() == requiredPipEnd || domino.getEndTwoCount() == requiredPipEnd) {
									actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
									randomOrderDominoes.remove(domino);
									break;
								}
							}
						}
					}
				}
				
				Domino lastDominoToPlay = (actions.isEmpty()) ? null : ((DominoPlayAction) actions.get(actions.size() - 1)).getDomino();
				if (lastDominoToPlay != null && lastDominoToPlay.isDouble()) {
					String doubleTrainOwner = ((DominoPlayAction) actions.get(actions.size() - 1)).getTrainOwner();
					requiredPipEnd = lastDominoToPlay.getEndOneCount();
					domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
					if (domino != null)
						actions.add(new DominoPlayAction(playManager, playerName, domino, doubleTrainOwner));
				}
			}
			else {
				String trainOwner = playManager.getSatisfyDoubleTrainOwner();
				if (playManager.getCurrentTurnType() == HostGUI.TurnType.MEXICAN_TRAIN_ONLY)
					trainOwner = HostGUI.MEXICAN_TRAIN_NAME;
				int requiredPipEnd = playManager.getPlayerTrainMap().get(trainOwner).getRequiredEndPipCount();
				Domino domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
				if (domino != null) {
					actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
					randomOrderDominoes.remove(domino);
				}
				else {
					domino = drawDomino(playManager, player);
					if (domino != null && (domino.getEndOneCount() == requiredPipEnd || domino.getEndTwoCount() == requiredPipEnd)) {
						actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
						randomOrderDominoes.remove(domino);
					}
				}
				
				Domino lastDominoToPlay = (actions.isEmpty()) ? null : ((DominoPlayAction) actions.get(0)).getDomino();
				if (lastDominoToPlay != null && lastDominoToPlay.isDouble()) {
					requiredPipEnd = lastDominoToPlay.getEndOneCount();
					domino = getFirstPlayableDomino(requiredPipEnd, randomOrderDominoes);
					if (domino != null)
						actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
				}
			}
		}
		// MEDIUM AND HARD AI
		else if (playManager.getCurrentTurnType() == HostGUI.TurnType.FIRST) {
			int requiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
			Integer aiLimit = null;
			if (player.getPlayerType() == PlayerType.COMPUTER_MEDIUM)
				aiLimit = (player.getDominoCount() + 1) / 2;
			List<Domino> bestDominoChain = buildHighestScoreDominoChain(player.getDominoes(), requiredPipEnd, new ArrayList<Domino>(0), new ArrayList<Domino>(), aiLimit);
			for (Domino domino : bestDominoChain)
				actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
							
			if (actions.isEmpty()) {
				Domino drawnDomino = drawDomino(playManager, player);
				if (drawnDomino != null && (drawnDomino.getEndOneCount() == requiredPipEnd || drawnDomino.getEndTwoCount() == requiredPipEnd)) {
					requiredPipEnd = (drawnDomino.getEndOneCount() == requiredPipEnd) ? drawnDomino.getEndTwoCount() : drawnDomino.getEndOneCount();
					List<Domino> availableDominoes = new ArrayList<Domino>(player.getDominoes());
					availableDominoes.remove(drawnDomino);
					bestDominoChain = buildHighestScoreDominoChain(availableDominoes, requiredPipEnd, Arrays.asList(drawnDomino), new ArrayList<Domino>(), aiLimit);
					for (Domino domino : bestDominoChain)
						actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
				}
			}
		}
		else if (playManager.getCurrentTurnType() == HostGUI.TurnType.NORMAL) {
			List<Domino> availableDominoes = new ArrayList<Domino>(player.getDominoes());
			int requiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
			
			if (player.getPlayerType() == PlayerType.COMPUTER_HARD) {
				List<Domino> bestChainDominoes = buildLongestDominoChain(availableDominoes, requiredPipEnd, new ArrayList<Domino>(0), new ArrayList<Domino>());
				if (!bestChainDominoes.isEmpty()) {
					Domino domino = bestChainDominoes.get(0);
					actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
					availableDominoes.remove(domino);
				}
			}
			
			if (actions.isEmpty()) {
				Domino domino = getHighestScorePlayableDomino(requiredPipEnd, availableDominoes);
				if (domino != null) {
					actions.add(new DominoPlayAction(playManager, playerName, domino, playerName));
					availableDominoes.remove(domino);
				}
			}
			
			if (actions.isEmpty()) {
				List<String> publicTrainOwners = getPublicTrainOwners(playManager.getPlayerTrainMap(), playerName);
				String bestPublicTrainOwner = null;
				Domino bestDomino = null;
				while (!publicTrainOwners.isEmpty()) {
					String trainOwner = publicTrainOwners.remove(Randomizer.getRandom(publicTrainOwners.size()));
					requiredPipEnd = playManager.getPlayerTrainMap().get(trainOwner).getRequiredEndPipCount();
					Domino domino = getHighestScorePlayableDomino(requiredPipEnd, availableDominoes);
					if (domino != null && (bestDomino == null || domino.getPipScore() > bestDomino.getPipScore())) {
						bestDomino = domino;
						bestPublicTrainOwner = trainOwner;
					}
				}
				
				if (bestDomino != null) {
					actions.add(new DominoPlayAction(playManager, playerName, bestDomino, bestPublicTrainOwner));
					availableDominoes.remove(bestDomino);
				}
			}
			
			if (actions.isEmpty() || (playManager.isOptionMexicanTrainExtraTurnSelected() && !((DominoPlayAction) actions.get(0)).getDomino().isDouble())) {
				requiredPipEnd = playManager.getPlayerTrainMap().get(HostGUI.MEXICAN_TRAIN_NAME).getRequiredEndPipCount();
				
				if (!actions.isEmpty() && player.getPlayerType() == PlayerType.COMPUTER_HARD) {
					List<Domino> nonChainDominoes = new LinkedList<Domino>(availableDominoes);
					int ownTrainRequiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
					List<Domino> longestDominoChain = buildLongestDominoChain(nonChainDominoes, ownTrainRequiredPipEnd, new ArrayList<Domino>(0), new ArrayList<Domino>());
					if (!longestDominoChain.isEmpty())
						longestDominoChain.remove(longestDominoChain.size() - 1);
					nonChainDominoes.removeAll(longestDominoChain);
					Domino domino = getHighestScorePlayableDomino(requiredPipEnd, nonChainDominoes);
					if (domino != null) {
						actions.add(new DominoPlayAction(playManager, playerName, domino, HostGUI.MEXICAN_TRAIN_NAME));
						availableDominoes.remove(domino);
					}
				}
				else {
					Domino domino = getHighestScorePlayableDomino(requiredPipEnd, availableDominoes);
					if (domino != null) {
						actions.add(new DominoPlayAction(playManager, playerName, domino, HostGUI.MEXICAN_TRAIN_NAME));
						availableDominoes.remove(domino);
					}
				}
			}
			
			if (actions.isEmpty()){
				Domino drawnDomino = drawDomino(playManager, player);
				if (drawnDomino != null) {
					requiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
					if (drawnDomino.getEndOneCount() == requiredPipEnd || drawnDomino.getEndTwoCount() == requiredPipEnd) {
						actions.add(new DominoPlayAction(playManager, playerName, drawnDomino, playerName));
						availableDominoes.remove(drawnDomino);
					}
					else {
						List<String> publicTrainOwners = getPublicTrainOwners(playManager.getPlayerTrainMap(), playerName);
						publicTrainOwners.add(HostGUI.MEXICAN_TRAIN_NAME);
						while (!publicTrainOwners.isEmpty()) {
							String trainOwner = publicTrainOwners.remove(Randomizer.getRandom(publicTrainOwners.size()));
							requiredPipEnd = playManager.getPlayerTrainMap().get(trainOwner).getRequiredEndPipCount();
							if (drawnDomino.getEndOneCount() == requiredPipEnd || drawnDomino.getEndTwoCount() == requiredPipEnd) {
								actions.add(new DominoPlayAction(playManager, playerName, drawnDomino, trainOwner));
								availableDominoes.remove(drawnDomino);
								break;
							}
						}
					}
				}
			}
			
			Domino lastDominoToPlay = (actions.isEmpty()) ? null : ((DominoPlayAction) actions.get(actions.size() - 1)).getDomino();
			if (lastDominoToPlay != null && lastDominoToPlay.isDouble()) {
				String doubleTrainOwner = ((DominoPlayAction) actions.get(actions.size() - 1)).getTrainOwner();
				requiredPipEnd = lastDominoToPlay.getEndOneCount();
				Domino domino = null;
				if (player.getPlayerType() == PlayerType.COMPUTER_HARD) {
					List<Domino> nonChainDominoes = new LinkedList<Domino>(availableDominoes);
					int ownTrainRequiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
					List<Domino> longestDominoChain = buildLongestDominoChain(nonChainDominoes, ownTrainRequiredPipEnd, new ArrayList<Domino>(0), new ArrayList<Domino>());
					if (!longestDominoChain.isEmpty())
						longestDominoChain.remove(longestDominoChain.size() - 1);
					nonChainDominoes.removeAll(longestDominoChain);
					domino = getHighestScorePlayableDomino(requiredPipEnd, nonChainDominoes);
				}					
				if (domino == null)
					domino = getHighestScorePlayableDomino(requiredPipEnd, availableDominoes);
				
				if (domino != null)
					actions.add(new DominoPlayAction(playManager, playerName, domino, doubleTrainOwner));
			}
		}
		else {
			List<Domino> availableDominoes = new ArrayList<Domino>(player.getDominoes());
			String trainOwner = playManager.getSatisfyDoubleTrainOwner();
			if (playManager.getCurrentTurnType() == HostGUI.TurnType.MEXICAN_TRAIN_ONLY)
				trainOwner = HostGUI.MEXICAN_TRAIN_NAME;
			int requiredPipEnd = playManager.getPlayerTrainMap().get(trainOwner).getRequiredEndPipCount();
			
			if (player.getPlayerType() == PlayerType.COMPUTER_HARD) {
				List<Domino> nonChainDominoes = new LinkedList<Domino>(availableDominoes);
				int ownTrainRequiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
				List<Domino> longestDominoChain = buildLongestDominoChain(nonChainDominoes, ownTrainRequiredPipEnd, new ArrayList<Domino>(0), new ArrayList<Domino>());
				if (!longestDominoChain.isEmpty())
					longestDominoChain.remove(longestDominoChain.size() - 1);
				nonChainDominoes.removeAll(longestDominoChain);
				Domino domino = getHighestScorePlayableDomino(requiredPipEnd, nonChainDominoes);
				if (domino != null) {
					actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
					availableDominoes.remove(domino);
				}
			}
			
			if (actions.isEmpty()) {
				Domino domino = getHighestScorePlayableDomino(requiredPipEnd, availableDominoes);
				if (domino != null) {
					actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
					availableDominoes.remove(domino);
				}
				else {
					domino = drawDomino(playManager, player);
					if (domino != null && (domino.getEndOneCount() == requiredPipEnd || domino.getEndTwoCount() == requiredPipEnd)) {
						actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
						availableDominoes.remove(domino);
					}
				}
			}
			
			Domino lastDominoToPlay = (actions.isEmpty()) ? null : ((DominoPlayAction) actions.get(0)).getDomino();
			if (lastDominoToPlay != null && lastDominoToPlay.isDouble()) {
				requiredPipEnd = lastDominoToPlay.getEndOneCount();
				Domino domino = null;
				if (player.getPlayerType() == PlayerType.COMPUTER_HARD) {
					List<Domino> nonChainDominoes = new LinkedList<Domino>(availableDominoes);
					int ownTrainRequiredPipEnd = playManager.getPlayerTrainMap().get(playerName).getRequiredEndPipCount();
					List<Domino> longestDominoChain = buildLongestDominoChain(nonChainDominoes, ownTrainRequiredPipEnd, new ArrayList<Domino>(0), new ArrayList<Domino>());
					if (!longestDominoChain.isEmpty())
						longestDominoChain.remove(longestDominoChain.size() - 1);
					nonChainDominoes.removeAll(longestDominoChain);
					domino = getHighestScorePlayableDomino(requiredPipEnd, nonChainDominoes);
				}					
				if (domino == null)
					domino = getHighestScorePlayableDomino(requiredPipEnd, availableDominoes);
				
				if (domino != null)
					actions.add(new DominoPlayAction(playManager, playerName, domino, trainOwner));
			}
		}
		
		boolean needEndTurnAction = false;
		if (playManager.getCurrentTurnType() == HostGUI.TurnType.FIRST)
			needEndTurnAction = true;
		else if (actions.isEmpty())
			needEndTurnAction = true;
		else if (((DominoPlayAction) actions.get(actions.size() - 1)).getDomino().isDouble())
			needEndTurnAction = true;
		else if (playManager.isOptionMexicanTrainExtraTurnSelected() && 
				 playManager.getCurrentTurnType() == HostGUI.TurnType.NORMAL &&
				 (actions.size() < 2 || !((DominoPlayAction) actions.get(actions.size() - 2)).getDomino().isDouble()) &&
				 !((DominoPlayAction) actions.get(actions.size() - 1)).getTrainOwner().equals(HostGUI.MEXICAN_TRAIN_NAME)) {
			needEndTurnAction = true;
		}
		if (actions.size() >= player.getDominoCount())
			needEndTurnAction = false;
			
		if (needEndTurnAction) {
			final boolean willPlayDomino = !actions.isEmpty();
			actions.add(new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					playManager.endTurn(playerName, willPlayDomino);
				}
			});
		}
		
		// All of the timer's from the previous AI call should have completed before this call was made, 
		// so they can be cleared from the list and garbage collected.
		playTimers.clear();
		
		int initial = 3000;
		int speed = (actions.size() > 3) ? 500 : 1000;
		for (int count = 1; count <= actions.size(); count++) {
			Timer timer = new Timer(initial + (count * speed), actions.get(count - 1));
			timer.setRepeats(false);
			playTimers.add(timer);
			timer.start();
		}
	}
	
	/**
	 * Returns the first domino in the list that can be played on the open pip end.
	 * @param openPipEnd pip end available to play on
	 * @param dominoes ordered list of dominoes to evaluate the play of
	 * @return the first domino in the list that can be played on the open pip end
	 */
	private static Domino getFirstPlayableDomino(int openPipEnd, List<Domino> dominoes) {
		for (Domino domino : dominoes)
			if (domino.getEndOneCount() == openPipEnd || domino.getEndTwoCount() == openPipEnd)
				return domino;
		
		return null;
	}
	
	/**
	 * Returns the highest pip score domino in the list that can be played on the open pip end.
	 * @param openPipEnd pip end available to play on
	 * @param dominoes list of dominoes to evaluate the play of
	 * @return the highest pip score domino in the list that can be played on the open pip end
	 */
	private static Domino getHighestScorePlayableDomino(int openPipEnd, List<Domino> dominoes) {
		Domino bestDomino = null;
		for (Domino domino : dominoes)
			if (domino.getEndOneCount() == openPipEnd || domino.getEndTwoCount() == openPipEnd)
				if (bestDomino == null || domino.getPipScore() > bestDomino.getPipScore())
					bestDomino = domino;
		
		return bestDomino;
	}
	
	/**
	 * Returns a drawn domino or null if the boneyard is empty.
	 * @param playManager manager used to draw a domino
	 * @param player the player drawing
	 * @return a drawn domino or null if the boneyard is empty
	 */
	private static Domino drawDomino(PlayManager playManager, Player player) {
		if (!playManager.draw(player.getName()))
			return null;
		return player.getDominoes().get(player.getDominoCount() - 1);
	}
	
	/**
	 * Returns a list of player names, other than the given player name, whose train is public.
	 * @param playerTrainMap mapping of all player names to their trains
	 * @param playerName name of the player to not add to list
	 * @return a list of player names, other than the given player name, whose train is public
	 */
	private static List<String> getPublicTrainOwners(LinkedHashMap<String, TrainPanel> playerTrainMap, String playerName) {
		List<String> playableTrainOwners = new LinkedList<String>();
		for (String otherPlayerName : playerTrainMap.keySet()) {
			if (!otherPlayerName.equals(playerName) && !otherPlayerName.equals(HostGUI.MEXICAN_TRAIN_NAME)) {
				TrainPanel train = playerTrainMap.get(otherPlayerName);
				if (train.isPublicTrain())
					playableTrainOwners.add(otherPlayerName);
			}
		}
		return playableTrainOwners;
	}
	
	/**
	 * Recursively builds a chain of dominoes that represent the highest pip score train play using the given dominoes.
	 * @param availableDominoes dominoes available to build a chain with
	 * @param requiredPipEnd current pip end required to connect a domino to the chain
	 * @param dominoChain current domino chain
	 * @param bestDominoChain the best domino chain found so far
	 * @param enough as soon as a chain is found that reaches or passes this value, stop searching and return; if null, examine all possible paths
	 * @return the highest pip score domino chain built using the given dominoes
	 */
	private static List<Domino> buildHighestScoreDominoChain(List<Domino> availableDominoes, int requiredPipEnd, List<Domino> dominoChain, List<Domino> bestDominoChain, Integer enough) {
		boolean matchFound = false;
		for (Domino domino : availableDominoes) {
			if (domino.getEndOneCount() == requiredPipEnd || domino.getEndTwoCount() == requiredPipEnd) {
				matchFound = true;
				List<Domino> newAvailableDominoes = new LinkedList<Domino>(availableDominoes);
				newAvailableDominoes.remove(domino);
				int newRequiredPipEnd = (domino.getEndOneCount() == requiredPipEnd) ? domino.getEndTwoCount() : domino.getEndOneCount();
				List<Domino> newDominoChain = new ArrayList<Domino>(dominoChain);
				newDominoChain.add(domino);
				bestDominoChain = buildHighestScoreDominoChain(newAvailableDominoes, newRequiredPipEnd, newDominoChain, bestDominoChain, enough);
				if (enough != null && bestDominoChain.size() >= enough)
					return bestDominoChain;
			}
		}
		
		if (!matchFound) {
			int totalPipScore = getTotalPipScore(dominoChain);
			int bestTotalPipScore = getTotalPipScore(bestDominoChain);
			if (totalPipScore > bestTotalPipScore || (totalPipScore == bestTotalPipScore && dominoChain.size() > bestDominoChain.size()))
				return dominoChain;
		}
		
		return bestDominoChain;
	}
	
	/**
	 * Recursively builds a chain of dominoes that represent the longest train play using the given dominoes.
	 * @param availableDominoes dominoes available to build a chain with
	 * @param requiredPipEnd current pip end required to connect a domino to the chain
	 * @param dominoChain current domino chain
	 * @param bestDominoChain the best domino chain found so far
	 * @return the longest domino chain built using the given dominoes
	 */
	private static List<Domino> buildLongestDominoChain(List<Domino> availableDominoes, int requiredPipEnd, List<Domino> dominoChain, List<Domino> bestDominoChain) {
		boolean matchFound = false;
		for (Domino domino : availableDominoes) {
			if (domino.getEndOneCount() == requiredPipEnd || domino.getEndTwoCount() == requiredPipEnd) {
				matchFound = true;
				List<Domino> newAvailableDominoes = new LinkedList<Domino>(availableDominoes);
				newAvailableDominoes.remove(domino);
				int newRequiredPipEnd = (domino.getEndOneCount() == requiredPipEnd) ? domino.getEndTwoCount() : domino.getEndOneCount();
				List<Domino> newDominoChain = new ArrayList<Domino>(dominoChain);
				newDominoChain.add(domino);
				bestDominoChain = buildLongestDominoChain(newAvailableDominoes, newRequiredPipEnd, newDominoChain, bestDominoChain);
			}
		}
		
		if (!matchFound) {
			if (dominoChain.size() > bestDominoChain.size() || (dominoChain.size() == bestDominoChain.size() && getTotalPipScore(dominoChain) > getTotalPipScore(bestDominoChain)))
				return dominoChain;
		}
		
		return bestDominoChain;
	}
	
	/**
	 * Counts the pips of all the dominoes in the list and returns the total.
	 * @param dominoes dominoes from which a score will be totaled
	 * @return total score in pips of the dominoes in the list 
	 */
	private static int getTotalPipScore(List<Domino> dominoes) {
		int totalPipScore = 0;
		for (Domino domino : dominoes)
			totalPipScore += domino.getPipScore();
		return totalPipScore;
	}
}
