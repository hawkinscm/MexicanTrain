package ai;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import gui.MexicanTrainGUI;
import gui.MexicanTrainGUI.TurnType;
import gui.MockPlayManager;
import model.Domino;
import model.DominoPlay;
import model.Player;
import model.PlayerType;
import unit.UnitTest;

public class AIManagerTest extends UnitTest {
	
	public int testAll() {
		System.out.println("AIManagerTest");
		
		for (Method method : getClass().getMethods()) {
			if (method.getName().equals("testAll") || !method.getName().startsWith("test"))
				continue;
			
			try {
				System.out.println("  " + method.getName());
				method.invoke(this);
			} 
			catch (Exception e) { e.printStackTrace(); } 
		}
		
		return errorCount;
	}

	public void testEasyFirstTurn() {
		Player player = new Player("Easy", PlayerType.COMPUTER_EASY);
		MockPlayManager playManager = new MockPlayManager(player, 12);
		
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 12)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertTrue(player.removeDomino(new Domino(1, 1)));
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 12));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(11, 12), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 5);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(11, 9), player.getName()));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(9, 4), player.getName()));
		assertEquals(playManager.getDominoPlays().get(3), new DominoPlay(new Domino(4, 2), player.getName()));
		assertEquals(playManager.getDominoPlays().get(4), new DominoPlay(new Domino(2, 1), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		player.addDomino(new Domino(3, 5));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 5);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(11, 9), player.getName()));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(9, 4), player.getName()));
		assertEquals(playManager.getDominoPlays().get(3), new DominoPlay(new Domino(4, 2), player.getName()));
		assertEquals(playManager.getDominoPlays().get(4), new DominoPlay(new Domino(2, 1), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(3, 5));
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		player.addDomino(new Domino(5, 7));
		player.addDomino(new Domino(1, 1));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 6);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(11, 9), player.getName()));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(9, 4), player.getName()));
		assertEquals(playManager.getDominoPlays().get(3), new DominoPlay(new Domino(4, 2), player.getName()));
		assertEquals(playManager.getDominoPlays().get(4), new DominoPlay(new Domino(2, 1), player.getName()));
		assertEquals(playManager.getDominoPlays().get(5), new DominoPlay(new Domino(1, 1), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 2);
		assertEquals(player.getDominoes().get(0), new Domino(3, 5));
		assertEquals(player.getDominoes().get(1), new Domino(5, 7));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		player.addDomino(new Domino(1, 1));
		playManager.setBoneyard(Arrays.asList(new Domino(10, 8)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(10, 8));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 6);
		
		playManager.resetRound();
		playManager.setBoneyard(Arrays.asList(new Domino(11, 12)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 6);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(11, 9), player.getName()));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(9, 4), player.getName()));
		assertEquals(playManager.getDominoPlays().get(3), new DominoPlay(new Domino(4, 2), player.getName()));
		assertEquals(playManager.getDominoPlays().get(4), new DominoPlay(new Domino(2, 1), player.getName()));
		assertEquals(playManager.getDominoPlays().get(5), new DominoPlay(new Domino(1, 1), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(11, 12));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(10, 8));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(1, 3));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		player.addDomino(new Domino(1, 2));
		playManager.setBoneyard(Arrays.asList(new Domino(11, 12)));
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 6);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(11, 9), player.getName()));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(9, 4), player.getName()));
		assertEquals(playManager.getDominoPlays().get(3), new DominoPlay(new Domino(4, 2), player.getName()));
		assertEquals(playManager.getDominoPlays().get(4), new DominoPlay(new Domino(2, 1), player.getName()));
		assertEquals(playManager.getDominoPlays().get(5), new DominoPlay(new Domino(1, 3), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(11, 12));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 0);
	}
	
	public void testEasyNormalTurn() {
		Player player = new Player("Easy", PlayerType.COMPUTER_EASY);
		MockPlayManager playManager = new MockPlayManager(player, 12);
		playManager.setIsExtraTurn(true);

		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 12)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertTrue(player.removeDomino(new Domino(1, 1)));
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		playManager.setBoneyard(Arrays.asList(new Domino(12, 11), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 11));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(11, 12), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 4);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		player.addDomino(new Domino(3, 5));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 4), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 5);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(1, 2));
		playManager.setBoneyard(new ArrayList<Domino>());
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(1, 2));
		assertEquals(playManager.getCurrentTurnType(), TurnType.SATISFY_DOUBLE);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(1, 2));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 3), new Domino(12, 4)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 3));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 2);
		assertEquals(player.getDominoes().get(0), new Domino(1, 2));
		assertEquals(player.getDominoes().get(1), new Domino(1, 3));
		assertEquals(playManager.getCurrentTurnType(), TurnType.SATISFY_DOUBLE);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(1, 4));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 2);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(12, 4), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(1, 4));
		
		playManager.resetRound();
		player.removeDominoes();
		playManager.getPlayerTrainMap().get(MexicanTrainGUI.MEXICAN_TRAIN_NAME).addDomino(new Domino(12, 4));
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(3, 4));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 12)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 2);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(12, 5), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(5, 12));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(3, 4));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 2);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(4, 3), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(5, 6));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 11));
		player.addDomino(new Domino(4, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 3);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(4, 4), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(4, 5), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDrawnDomino(), new Domino(5, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(5, 6));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(4, 4));
		playManager.setBoneyard(new ArrayList<Domino>());
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(4, 4));
		
		playManager.resetRound();
		playManager.setIsExtraTurn(false);
		player.removeDominoes();
		player.addDomino(new Domino(12, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 2);
		assertEquals(player.getDominoes().get(0), new Domino(3, 4));
		assertEquals(player.getDominoes().get(1), new Domino(5, 6));
	}
	
	public void testEasyOtherTurn() {
		Player player = new Player("Easy", PlayerType.COMPUTER_EASY);
		MockPlayManager playManager = new MockPlayManager(player, 12);
		
		player.addDomino(new Domino(11, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.MEXICAN_TRAIN_ONLY);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(5, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 4);

		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(11, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		player.addDomino(new Domino(12, 11));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 12)));
		playManager.setCurrentTurnType(TurnType.MEXICAN_TRAIN_ONLY);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 3);
		
		playManager.resetRound();
		player.addDomino(new Domino(12, 12));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.MEXICAN_TRAIN_ONLY);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertEquals(player.getDominoes().size(), 4);
		
		playManager.resetRound();
		playManager.setSatisfyDoubleTrainOwner(MexicanTrainGUI.MEXICAN_TRAIN_NAME);
		player.addDomino(new Domino(12, 11));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.SATISFY_DOUBLE);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 4);
		
		playManager.resetRound();
		playManager.setSatisfyDoubleTrainOwner(MexicanTrainGUI.MEXICAN_TRAIN_NAME);
		player.addDomino(new Domino(10, 11));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.SATISFY_DOUBLE);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 6);
		
		playManager.resetRound();
		playManager.setSatisfyDoubleTrainOwner(MexicanTrainGUI.MEXICAN_TRAIN_NAME);
		playManager.setBoneyard(Arrays.asList(new Domino(12, 11), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.SATISFY_DOUBLE);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 11));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 6);
	}

	public void testAdvancedFirstTurn() {
		Player player = new Player("Hard", PlayerType.COMPUTER_HARD);
		MockPlayManager playManager = new MockPlayManager(player, 12);
		
		playManager.setCurrentTurnType(TurnType.FIRST);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		playManager.setCurrentTurnType(TurnType.FIRST);
		player.addDomino(new Domino(1, 5));
		player.addDomino(new Domino(1, 7));
		player.addDomino(new Domino(1, 6));
		player.addDomino(new Domino(12, 1));
		player.addDomino(new Domino(12, 11));
		player.addDomino(new Domino(8, 6));
		player.addDomino(new Domino(6, 6));
		player.addDomino(new Domino(7, 2));
		player.addDomino(new Domino(8, 10));
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 5);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 1), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(1, 6), player.getName()));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(6, 6), player.getName()));
		assertEquals(playManager.getDominoPlays().get(3), new DominoPlay(new Domino(6, 8), player.getName()));
		assertEquals(playManager.getDominoPlays().get(4), new DominoPlay(new Domino(8, 10), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 4);
		assertEquals(player.getDominoes().get(0), new Domino(1, 5));
		assertEquals(player.getDominoes().get(1), new Domino(1, 7));
		assertEquals(player.getDominoes().get(2), new Domino(12, 11));
		assertEquals(player.getDominoes().get(3), new Domino(7, 2));

		playManager.resetRound();
		playManager.setCurrentTurnType(TurnType.FIRST);
		player.removeDominoes();
		player.addDomino(new Domino(1, 5));
		player.addDomino(new Domino(1, 1));
		player.addDomino(new Domino(5, 11));
		player.addDomino(new Domino(7, 2));
		player.addDomino(new Domino(2, 11));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 1)));
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 6);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 1), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(1, 1), player.getName()));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(1, 5), player.getName()));
		assertEquals(playManager.getDominoPlays().get(3), new DominoPlay(new Domino(5, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(4), new DominoPlay(new Domino(11, 2), player.getName()));
		assertEquals(playManager.getDominoPlays().get(5), new DominoPlay(new Domino(2, 7), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 0);
	}
	
	public void testAdvancedNormalTurn() {
		Player player = new Player("Hard", PlayerType.COMPUTER_HARD);
		MockPlayManager playManager = new MockPlayManager(player, 12);
		playManager.setIsExtraTurn(true);

		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 12)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertTrue(player.removeDomino(new Domino(1, 1)));
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		playManager.setBoneyard(Arrays.asList(new Domino(12, 11), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 11));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(11, 12), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertTrue(player.getDominoes().isEmpty());
		
		playManager.resetRound();
		player.addDomino(new Domino(11, 12));
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 12), new Domino(1, 1)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 4);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(1, 2));
		player.addDomino(new Domino(2, 4));
		player.addDomino(new Domino(11, 9));
		player.addDomino(new Domino(9, 4));
		player.addDomino(new Domino(3, 5));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 4), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 5);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(1, 2));
		playManager.setBoneyard(new ArrayList<Domino>());
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(1, 2));
		assertEquals(playManager.getCurrentTurnType(), TurnType.SATISFY_DOUBLE);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(1, 2));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 3), new Domino(12, 4)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 3));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 2);
		assertEquals(player.getDominoes().get(0), new Domino(1, 2));
		assertEquals(player.getDominoes().get(1), new Domino(1, 3));
		assertEquals(playManager.getCurrentTurnType(), TurnType.SATISFY_DOUBLE);
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(1, 4));
		playManager.setBoneyard(Arrays.asList(new Domino(12, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 2);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(12, 4), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(1, 4));
		
		playManager.resetRound();
		player.removeDominoes();
		playManager.getPlayerTrainMap().get(MexicanTrainGUI.MEXICAN_TRAIN_NAME).addDomino(new Domino(12, 4));
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(3, 4));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 12)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 2);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(12, 5), player.getName()));
		assertEquals(playManager.getDrawnDomino(), new Domino(5, 12));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(3, 4));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 2);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(4, 3), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(5, 6));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 11));
		player.addDomino(new Domino(4, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 3);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertEquals(playManager.getDominoPlays().get(1), new DominoPlay(new Domino(4, 4), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDominoPlays().get(2), new DominoPlay(new Domino(4, 5), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDrawnDomino(), new Domino(5, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(5, 6));
		
		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(12, 12));
		player.addDomino(new Domino(4, 4));
		playManager.setBoneyard(new ArrayList<Domino>());
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 1);
		assertEquals(player.getDominoes().get(0), new Domino(4, 4));
		
		playManager.resetRound();
		playManager.setIsExtraTurn(false);
		player.removeDominoes();
		player.addDomino(new Domino(12, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.NORMAL);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), player.getName()));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 2);
		assertEquals(player.getDominoes().get(0), new Domino(3, 4));
		assertEquals(player.getDominoes().get(1), new Domino(5, 6));
	}
	
	public void testAdvancedOtherTurn() {
		Player player = new Player("Hard", PlayerType.COMPUTER_HARD);
		MockPlayManager playManager = new MockPlayManager(player, 12);
		
		player.addDomino(new Domino(11, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		playManager.setBoneyard(Arrays.asList(new Domino(5, 4), new Domino(1, 3)));
		playManager.setCurrentTurnType(TurnType.MEXICAN_TRAIN_ONLY);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(5, 4));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 4);

		playManager.resetRound();
		player.removeDominoes();
		player.addDomino(new Domino(11, 11));
		player.addDomino(new Domino(3, 4));
		player.addDomino(new Domino(5, 6));
		player.addDomino(new Domino(12, 11));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 12)));
		playManager.setCurrentTurnType(TurnType.MEXICAN_TRAIN_ONLY);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 3);
		
		playManager.resetRound();
		player.addDomino(new Domino(12, 12));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.MEXICAN_TRAIN_ONLY);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 12), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertEquals(player.getDominoes().size(), 4);
		
		playManager.resetRound();
		playManager.setSatisfyDoubleTrainOwner(MexicanTrainGUI.MEXICAN_TRAIN_NAME);
		player.addDomino(new Domino(12, 11));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.SATISFY_DOUBLE);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertNull(playManager.getDrawnDomino());
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 4);
		
		playManager.resetRound();
		playManager.setSatisfyDoubleTrainOwner(MexicanTrainGUI.MEXICAN_TRAIN_NAME);
		player.addDomino(new Domino(10, 11));
		playManager.setBoneyard(Arrays.asList(new Domino(1, 1), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.SATISFY_DOUBLE);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 0);
		assertEquals(playManager.getDrawnDomino(), new Domino(1, 1));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertFalse(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 6);
		
		playManager.resetRound();
		playManager.setSatisfyDoubleTrainOwner(MexicanTrainGUI.MEXICAN_TRAIN_NAME);
		playManager.setBoneyard(Arrays.asList(new Domino(12, 11), new Domino(12, 10)));
		playManager.setCurrentTurnType(TurnType.SATISFY_DOUBLE);
		AIManager.takeTurn(playManager, player);
		if (!waitForTurnEnd(playManager)) { return; }
		assertEquals(playManager.getDominoPlays().size(), 1);
		assertEquals(playManager.getDominoPlays().get(0), new DominoPlay(new Domino(12, 11), MexicanTrainGUI.MEXICAN_TRAIN_NAME));
		assertEquals(playManager.getDrawnDomino(), new Domino(12, 11));
		assertEquals(playManager.getEndTurnPlayerName(), player.getName());
		assertTrue(playManager.getEndTurnPlayedDomino());
		assertEquals(player.getDominoes().size(), 6);
	}
	
	private boolean waitForTurnEnd(MockPlayManager playManager) {
		int waitedTime = 0;
		int waitTime = 500;
		
		while (!playManager.isTurnEnded()) {
			if (waitedTime > 10000) {
				fail("Timed out waiting for end turn");
				return false;
			}
			
			try { 
				Thread.sleep(waitTime);
				waitedTime += waitTime;
			}
			catch (Exception ex) {
				fail(ex.getMessage());
				return false;
			}
		}
		
		return true;
	}
}
