package gui;

import gui.MexicanTrainGUI.TurnType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import model.Domino;
import model.DominoPlay;
import model.Player;

public class MockPlayManager implements PlayManager  {

	private Player player;
	private LinkedHashMap<String, TrainPanel> playerTrainMap;
	private TurnType currentTurnType;
	private List<Domino> boneyard;
	private boolean isExtraTurn;
	private String satisfyDoubleTrainOwner;
	
	private LinkedList<DominoPlay> plays;
	private Domino drawnDomino;
	private boolean isTurnEnded;
	private String endTurnPlayerName;
	private boolean endTurnPlayedDomino;
	
	public MockPlayManager(Player player, int pipRound) {
		this.player = player;
		
		playerTrainMap = new LinkedHashMap<String, TrainPanel>();
		playerTrainMap.put(player.getName(), new TrainPanel(this, player.getName()));
		playerTrainMap.put("1", new TrainPanel(this, "1"));
		playerTrainMap.put("2", new TrainPanel(this, "2"));
		playerTrainMap.put("3", new TrainPanel(this, "3"));
		playerTrainMap.put(MexicanTrainGUI.MEXICAN_TRAIN_NAME, new TrainPanel(this, MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		
		for (TrainPanel train : playerTrainMap.values())
			train.restartTrain(pipRound);
		
		boneyard = new ArrayList<Domino>();
		resetRound();
	}
	
	public void resetRound() {
		isTurnEnded = false;
		plays = new LinkedList<DominoPlay>();
		drawnDomino = null;
		endTurnPlayerName = null;
		endTurnPlayedDomino = false;
		boneyard.clear();
	}
	
	public void setBoneyard(List<Domino> boneyard) {
		this.boneyard.clear();
		this.boneyard.addAll(boneyard);
	}
	
	@Override
	public boolean canPlay(Domino domino, String trainOwner) {
		return true;
	}

	@Override
	public void play(Domino domino, String trainOwner) {
		play(player.getName(), domino, trainOwner);
	}

	@Override
	public void play(String playerName, Domino domino, String trainOwner) {
		plays.add(new DominoPlay(domino, trainOwner));
		player.removeDomino(domino);
		
		if (currentTurnType == TurnType.FIRST) {}
		else if (domino.isDouble()) {
			currentTurnType = TurnType.SATISFY_DOUBLE;
		}
		else if (currentTurnType == TurnType.SATISFY_DOUBLE) {
			currentTurnType = TurnType.NORMAL;
			endTurn(playerName, true);
		}
		else if (isExtraTurn && currentTurnType != TurnType.SATISFY_DOUBLE && !trainOwner.equals(MexicanTrainGUI.MEXICAN_TRAIN_NAME)) {
			currentTurnType = TurnType.MEXICAN_TRAIN_ONLY;
		}
		else
			endTurn(playerName, true);
	}

	@Override
	public boolean draw(String playerName) {
		if (boneyard == null || boneyard.isEmpty())
			return false;
		
		Domino drawnDomino = boneyard.remove(0);
		player.addDomino(drawnDomino);
		this.drawnDomino = drawnDomino;
		return true;
	}

	@Override
	public void endTurn(String playerName, boolean hasPlayedDomino) {
		isTurnEnded = true;
		endTurnPlayerName = playerName;
		endTurnPlayedDomino = hasPlayedDomino;
	}

	@Override
	public TurnType getCurrentTurnType() {
		return currentTurnType;
	}
	
	public void setCurrentTurnType(TurnType turnType) {
		currentTurnType = turnType;
	}

	@Override
	public LinkedHashMap<String, TrainPanel> getPlayerTrainMap() {
		return playerTrainMap;
	}

	@Override
	public boolean isOptionMexicanTrainExtraTurnSelected() {
		return isExtraTurn;
	}
	
	public void setIsExtraTurn(boolean isExtraTurn) {
		this.isExtraTurn = isExtraTurn;
	}

	@Override
	public String getSatisfyDoubleTrainOwner() {
		return satisfyDoubleTrainOwner;
	}

	public void setSatisfyDoubleTrainOwner(String trainOwner) {
		satisfyDoubleTrainOwner = trainOwner;
	}
	
	public boolean isTurnEnded() {
		return isTurnEnded;
	}
	
	public LinkedList<DominoPlay> getDominoPlays() {
		return plays;
	}
	
	public Domino getDrawnDomino() {
		return drawnDomino;
	}
	
	public String getEndTurnPlayerName() {
		return endTurnPlayerName;
	}
	
	public boolean getEndTurnPlayedDomino() {
		return endTurnPlayedDomino;
	}
}
