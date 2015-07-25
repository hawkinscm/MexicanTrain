package gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.ScoreKeeper;

/**
 * Displays the current scores for all players, for each round and in total.
 */
public class ScoreSheetDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private boolean noDisplay = false;
	
	/**
	 * Creates a new Dialog to display player scores.
	 * @param owner the frame that called this dialog
	 * @param scoreKeeper the ScoreKeeper with all the player scores
	 */
	public ScoreSheetDialog(Frame owner, ScoreKeeper scoreKeeper) {
		super(owner, "Player Scores");
		
		if (scoreKeeper == null) {
			noDisplay = true;
			return;
		}
				
		List<String> playerNames = scoreKeeper.getPlayerNames();
		
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		getContentPane().add(new JLabel(" "), c);
		
		for (int round = 1; round <= scoreKeeper.getNumberOfRoundsFinished(); round++) {
			c.insets = new Insets(0, 5, 0, 5);
			c.gridy++;
			getContentPane().add(new JLabel("Round " + round), c);
		}

		c.insets = new Insets(0, 5, 0, 5);
		c.gridy++;
		c.gridwidth = playerNames.size() + 1;
		JPanel linePanel = new JPanel();
		getContentPane().add(linePanel, c);
		c.gridwidth = 1;
		
		c.gridy++;
		JLabel totalLabel = new JLabel("TOTAL");
		totalLabel.setFont(totalLabel.getFont().deriveFont(13f));
		getContentPane().add(totalLabel, c);
		
		c.anchor = GridBagConstraints.CENTER;
		for (String playerName : playerNames) {
			c.gridy = 0;
			c.gridx++;
			getContentPane().add(new JLabel(playerName), c);
			
			for (int round = 1; round <= scoreKeeper.getNumberOfRoundsFinished(); round++) {
				c.insets = new Insets(0, 5, 0, 5);
				c.gridy++;
				getContentPane().add(new JLabel("" + scoreKeeper.getPlayerRoundScore(playerName, round)), c);
			}
			c.insets = new Insets(5, 5, 5, 5);
			
			int playerScore = scoreKeeper.getPlayerScore(playerName);
			
			c.gridy += 2;
			totalLabel = new JLabel("" + playerScore);
			totalLabel.setFont(totalLabel.getFont().deriveFont(13f));
			getContentPane().add(totalLabel, c);
			
			c.gridy++;
			int playerRank = 1;
			for (String currentPlayer : playerNames) {
				int currentPlayerScore = scoreKeeper.getPlayerScore(currentPlayer);
				if (playerScore > currentPlayerScore) {
					playerRank++;
				}
				else if (playerScore == currentPlayerScore) {
					int numberOfZeroRounds = scoreKeeper.getNumberOfZeroRounds(playerName);
					int currentPlayerNumberOfZeroRounds = scoreKeeper.getNumberOfZeroRounds(currentPlayer);
					if (numberOfZeroRounds < currentPlayerNumberOfZeroRounds) {
						playerRank++;
					}
					else if (numberOfZeroRounds == currentPlayerNumberOfZeroRounds) {
						if (scoreKeeper.getLowestRoundScoreAboveZero(playerName) > scoreKeeper.getLowestRoundScoreAboveZero(currentPlayer)) {
							playerRank++;
						}
					}
				}
			}
			JLabel playerRankLabel = new JLabel();
			String playerRankStr = "" + playerRank;
			if (playerRank == 1 && scoreKeeper.getPipRoundNumber() < 0) {
				playerRankStr = "WINNER";
				playerRankLabel.setForeground(Color.BLUE);
			}
			else if (playerRank == 1)
				playerRankStr += "st";
			else if (playerRank == 2)
				playerRankStr += "nd";
			else if (playerRank == 3)
				playerRankStr += "rd";
			else
				playerRankStr += "th";
			playerRankLabel.setText(playerRankStr);
			getContentPane().add(playerRankLabel, c);
		}
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (noDisplay)
			return;
		super.setVisible(b);
	}
}
