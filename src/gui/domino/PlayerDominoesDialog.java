package gui.domino;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import model.Domino;

/**
 * Dialog for displaying a player's dominoes.
 */
public class PlayerDominoesDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final int MAX_DOMINO_LINE_LIMIT = 4;
	
	private boolean limitByColumn;
	private int dominoEndSize;
	
	private JPanel dominoDisplayPanel;
	private DominoDisplayTable dominoDisplayTable;
	
	private DominoDisplay tradingPlacesDominoDisplay;
	
	/**
	 * Settings for loading and saving the dominoes display.
	 */
	public static class DominoesDisplaySettings {
		public Dimension size;
		public Point location;
		public boolean limitByColumn;
		public int dominoLineLimit;
		
		public DominoesDisplaySettings() {
			size = new Dimension(300, 600);
			location = null;
			limitByColumn = true;
			dominoLineLimit = 2;
		}
	}
	
	/**
	 * Creates a new Player Dominoes Dialog.
	 * @param owner parent component for this dialog
	 */
	public PlayerDominoesDialog(JFrame owner, DominoesDisplaySettings settings) {
		super(owner, "DOMINOES");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		dominoEndSize = DominoDisplay.DOMINO_END_MAX_SIZE;
		if (settings.dominoLineLimit < 1 || settings.dominoLineLimit > MAX_DOMINO_LINE_LIMIT)
			settings.dominoLineLimit = 2;
		dominoDisplayTable = new DominoDisplayTable(settings.dominoLineLimit);
		limitByColumn = settings.limitByColumn;
		
		setMinimumSize(new Dimension(150, 150));
		if (settings.size.height < 150 || settings.size.width < 150)
			settings.size = new Dimension(150, 150);
		setSize(settings.size);
		Point location = settings.location;
		if (location == null) {
			int maxX = Toolkit.getDefaultToolkit().getScreenSize().width - getWidth();
			int x = Math.min(maxX, owner.getLocation().x + owner.getWidth());
			int y = owner.getLocation().y;
			location = new Point(x, y);
		}

		if (location.x < 0 || location.x >= Toolkit.getDefaultToolkit().getScreenSize().width - 5)
			location = new Point(0, 0);
		else if (location.y < 0 || location.y >= Toolkit.getDefaultToolkit().getScreenSize().height - 25)
			location = new Point(0, 0);
		
		setLocation(location);
		
		dominoDisplayPanel = new JPanel();
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.NORTHWEST;
		getContentPane().add(new JLabel("Right-click a domino to flip it"), c);		
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridy++;
		getContentPane().add(dominoDisplayPanel, c);
		
		getContentPane().addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				resizeDisplay();
			}
			public void componentShown(ComponentEvent e) {}
		});
		
		createMenu();
	}
	
	/**
	 * Creates a menu for this display, used for sorting and displaying options.
	 */
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu displayMenu = new JMenu("Display");
		
		JMenu byColumnMenu = new JMenu("By Column");
		String type = " Column";
		for (int count = 1; count <= MAX_DOMINO_LINE_LIMIT; count++) {
			JMenuItem columnMenuItem = new JMenuItem(count + type);
			final int sort = count;
			columnMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					limitByColumn = true;
					dominoDisplayTable.setColumnLimit(sort);
					reloadDominoDisplay();
				}
			});
			byColumnMenu.add(columnMenuItem);
			type = " Columns";
		}
		
		JMenu byRowMenu = new JMenu("By Row");
		type = " Row";
		for (int count = 1; count <= MAX_DOMINO_LINE_LIMIT; count++) {
			JMenuItem rowMenuItem = new JMenuItem(count + type);
			final int sort = count;
			rowMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					limitByColumn = false;
					dominoDisplayTable.setColumnLimit(sort);
					reloadDominoDisplay();
				}
			});
			byRowMenu.add(rowMenuItem);
			type = " Rows";
		}
		
		displayMenu.add(byColumnMenu);
		displayMenu.add(byRowMenu);
		
		menuBar.add(displayMenu);
		setJMenuBar(menuBar);
	}
	
	/**
	 * Replaces the displayed dominoes with the given list.
	 * @param dominoes new list of dominoes to replace the current list
	 */
	public void setDominoes(List<Domino> dominoes) {
		dominoDisplayTable.reloadTable(dominoes);
		reloadDominoDisplay();
	}
	
	/**
	 * Adds a domino the the display.
	 * @param domino domino to add
	 */
	public void addDomino(Domino domino) {
		dominoDisplayTable.addDomino(domino);
		reloadDominoDisplay();
	}
	
	/**
	 * Reloads and displays a the domino displays from the domino display table.
	 */
	private void reloadDominoDisplay() {
		dominoDisplayPanel.removeAll();
		
		int rows = limitByColumn ? dominoDisplayTable.getRowCount() : dominoDisplayTable.getColumnCount();
		int columns = limitByColumn ? dominoDisplayTable.getColumnCount() : dominoDisplayTable.getRowCount();
		dominoDisplayPanel.setLayout(new GridLayout(rows, columns, 5, 5));
		
		List<DominoDisplay> dominoDisplays = limitByColumn ? dominoDisplayTable.getDominoDisplaysInRowOrder() : dominoDisplayTable.getDominoDisplaysInColumnOrder();
		for (final DominoDisplay dominoDisplay : dominoDisplays) {
			if (dominoDisplay.getTransferHandler() != null)
				{/* Do Nothing */}
			else if (dominoDisplay.getDomino() == null)
				dominoDisplay.setTransferHandler(new DominoTraderTransferHandler(this));
			else {
				dominoDisplay.setTransferHandler(new DominoSenderAndTraderTransferHandler(this));
				dominoDisplay.addMouseListener(new MouseListener() {
					private boolean isInside;
					
					public void mouseClicked(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {
						if (e.getButton() != MouseEvent.BUTTON1 || !(e.getSource() instanceof JComponent))
							return;
						
						JComponent comp = (JComponent) e.getSource();
						TransferHandler handler = comp.getTransferHandler();
						handler.exportAsDrag(comp, e, TransferHandler.MOVE);
					}
					public void mouseReleased(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON3 && isInside)
							dominoDisplay.flipDomino();
					}
					public void mouseEntered(MouseEvent e) { isInside = true; }
					public void mouseExited(MouseEvent e) { isInside = false; }
				});
			}
			dominoDisplayPanel.add(dominoDisplay);
		}
		
		validate();
		resizeDisplay();
	}

	/**
	 * Resizes the display and trains to fit in the GUI window.
	 */
	private void resizeDisplay() {
		if (dominoDisplayTable.getRowCount() <= 0)
			return;
		
		int numDominoesPerColumn = limitByColumn ? dominoDisplayTable.getRowCount() : dominoDisplayTable.getColumnCount();
		int usedHeight = 80 + (10 * numDominoesPerColumn);
		int availableHeight = getSize().height - usedHeight;
		int availableHeightPerDomino = Math.max(16, availableHeight / numDominoesPerColumn);
		
		int numDominoesPerRow = limitByColumn ? dominoDisplayTable.getColumnCount() : dominoDisplayTable.getRowCount();
		int usedWidth = 10 + (15 * numDominoesPerRow);
		int availableWidth = getSize().width - usedWidth;
		int availableWidthPerDominoEnd = Math.max(16, availableWidth / (numDominoesPerRow * 2));
		
		int newDominoEndSize = Math.min(DominoDisplay.DOMINO_END_MAX_SIZE, Math.min(availableHeightPerDomino, availableWidthPerDominoEnd));
		if (dominoEndSize != newDominoEndSize) {
			dominoEndSize = newDominoEndSize;
			dominoDisplayTable.setDominoEndSize(newDominoEndSize);
			for (Component component : dominoDisplayPanel.getComponents())
				if (component instanceof DominoDisplay)
					((DominoDisplay) component).resizeDisplay(newDominoEndSize);
		}
	}
	
	/**
	 * Clears the given domino display or, if tradingPlacesDominoDisplay is not null, trades its position with the set display.
	 * @param dominoDisplay domino display to clear or trade places with a previously set domino display
	 */
	public void removeDominoDisplayOrTradePlaces(DominoDisplay dominoDisplay) {
		Component[] dominoDisplays = dominoDisplayPanel.getComponents();
		int displayCount = dominoDisplays.length;
		int dominoDisplayIdx;
		for (dominoDisplayIdx = 0; dominoDisplayIdx < displayCount; dominoDisplayIdx++)
			if (dominoDisplays[dominoDisplayIdx] == dominoDisplay)
				break;
		
		if (displayCount == 0 || dominoDisplayIdx >= displayCount)
			return;

		dominoDisplayPanel.remove(dominoDisplay);
		DominoDisplay emptyDisplay = new DominoDisplay(null, dominoEndSize);
		emptyDisplay.setTransferHandler(new DominoTraderTransferHandler(this));
		dominoDisplayPanel.add(emptyDisplay, dominoDisplayIdx);
		dominoDisplayTable.replaceDominoDisplay(dominoDisplay, emptyDisplay);
		
		if (tradingPlacesDominoDisplay != null) {
			int tradingPlacesdominoDisplayIdx;
			for (tradingPlacesdominoDisplayIdx = 0; tradingPlacesdominoDisplayIdx < dominoDisplays.length; tradingPlacesdominoDisplayIdx++)
				if (dominoDisplays[tradingPlacesdominoDisplayIdx] == tradingPlacesDominoDisplay)
					break;
			
			dominoDisplayPanel.remove(tradingPlacesDominoDisplay);
			dominoDisplayPanel.add(dominoDisplay, tradingPlacesdominoDisplayIdx);
			dominoDisplayTable.replaceDominoDisplay(tradingPlacesDominoDisplay, dominoDisplay);
			
			dominoDisplayPanel.remove(emptyDisplay);
			dominoDisplayPanel.add(tradingPlacesDominoDisplay, dominoDisplayIdx);
			dominoDisplayTable.replaceDominoDisplay(emptyDisplay, tradingPlacesDominoDisplay);
			
			tradingPlacesDominoDisplay = null;
			validate();
		}
		else if (dominoDisplayTable.combineAdjacentEmptyRows())
			reloadDominoDisplay();
		else
			validate();
	}
	
	/**
	 * Sets a domino display to prepare to trade places with the display passed into removeDominoDisplayOrTradePlaces().
	 * @param dominoDisplay domino display to set as the trade places domino display
	 */
	public void setTradePlacesDominoDisplay(DominoDisplay dominoDisplay) {
		tradingPlacesDominoDisplay = dominoDisplay;
	}
	
	/**
	 * Builds and returns a Dominoes Display Settings container from the current settings.
	 * @return a Dominoes Display Settings container from the current settings
	 */
	public DominoesDisplaySettings buildDominoesDisplaySettings() {
		DominoesDisplaySettings dominoesDisplaySettings = new DominoesDisplaySettings();
		dominoesDisplaySettings.size = getSize();
		dominoesDisplaySettings.location = getLocation();
		dominoesDisplaySettings.limitByColumn = limitByColumn;
		dominoesDisplaySettings.dominoLineLimit = dominoDisplayTable.getColumnCount();
		return dominoesDisplaySettings;
	}
}
