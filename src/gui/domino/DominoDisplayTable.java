package gui.domino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import model.Domino;

/**
 * A table of domino displays.
 */
public class DominoDisplayTable {

	private int dominoEndSize;
	private int columnSize;
	private LinkedList<DominoDisplay[]> rows;
		
	/**
	 * Creates a new, empty Domino Display Table.
	 * @param columnSize number of columns in the table
	 */
	public DominoDisplayTable(int columnSize) {
		this.columnSize = columnSize;
		rows = new LinkedList<DominoDisplay[]>();
		dominoEndSize = DominoDisplay.DOMINO_END_MAX_SIZE;
	}
	
	/**
	 * Returns the number of rows in the table.
	 * @return the number of rows in the table
	 */
	public int getRowCount() {
		return rows.size();
	}
	
	/**
	 * Returns the number of columns in the table.
	 * @return the number of columns in the table
	 */
	public int getColumnCount() {
		return columnSize;
	}
	
	/**
	 * Rearranges the table by how many domino displays are in one row.
	 * @param columnLimit how many columns will be in the rearranged table
	 */
	public void setColumnLimit(int columnLimit) {
		if (columnSize == columnLimit)
			return;
		
		columnSize = columnLimit;
		LinkedList<DominoDisplay> dominoDisplays = new LinkedList<DominoDisplay>(getDominoDisplaysInRowOrder());
		rows.clear();
		while (!dominoDisplays.isEmpty()) {
			DominoDisplay[] row = new DominoDisplay[columnSize];
			for (int col = 0; col < columnSize; col++) {
				DominoDisplay dominoDisplay = (dominoDisplays.isEmpty()) ? new DominoDisplay(null, dominoEndSize) : dominoDisplays.remove();
				row[col] = dominoDisplay;
			}
			rows.add(row);
		}
		
		if (countEmptyDominoSpaces() < columnSize)
			addEmptyRow();
		
		combineAdjacentEmptyRows();
	}
		
	/**
	 * Sets the square size of a domino end for display sizing.
	 * @param dominoEndSize square size of a domino end to set
	 */
	public void setDominoEndSize(int dominoEndSize) {
		this.dominoEndSize = dominoEndSize;
	}
	
	/**
	 * Returns a list of all domino displays in row order.
	 * @return a list of all domino with the first row first in the list and the last row last in the list.
	 */
	public List<DominoDisplay> getDominoDisplaysInRowOrder() {
		List<DominoDisplay> dominoDisplays = new ArrayList<DominoDisplay>(rows.size() * columnSize);
		for (DominoDisplay[] row : rows)
			dominoDisplays.addAll(Arrays.asList(row));
		return dominoDisplays;
	}
	
	/**
	 * Returns a list of all domino displays in column order.
	 * @return a list of all domino with the first column first in the list and the last column last in the list.
	 */
	public List<DominoDisplay> getDominoDisplaysInColumnOrder() {
		List<DominoDisplay> dominoDisplays = new ArrayList<DominoDisplay>(rows.size() * columnSize);
		for (int col = 0; col < columnSize; col++)
			for (DominoDisplay[] row : rows)
				dominoDisplays.add(row[col]);
		return dominoDisplays;
	}
	
	/**
	 * Reloads the table with the given data.
	 * @param columnSize number of columns for the table to have
	 * @param dominoes list of dominoes to include in the table
	 */
	public void reloadTable(List<Domino> dominoes) {
		rows.clear();
		
		LinkedList<Domino> remainingDominoes = new LinkedList<Domino>(dominoes);
		while (!remainingDominoes.isEmpty()) {
			DominoDisplay[] row = new DominoDisplay[columnSize];
			for (int col = 0; col < columnSize; col++) {
				Domino domino = null;
				if (!remainingDominoes.isEmpty())
					domino = remainingDominoes.remove();
				row[col] = new DominoDisplay(domino, dominoEndSize);
			}
			rows.add(row);
		}
		
		addEmptyRow();
	}
	
	/**
	 * Adds a row of empty domino displays to the end of the table.
	 */
	private void addEmptyRow() {
		DominoDisplay[] row = new DominoDisplay[columnSize];
		for (int col = 0; col < columnSize; col++)
			row[col] = new DominoDisplay(null, dominoEndSize);
		rows.add(row);
	}
	
	/**
	 * Replaces a domino display with a new domino display.
	 * @param currentDominoDisplay domino display to replace
	 * @param newDominoDisplay new domino display to replace current
	 */
	public void replaceDominoDisplay(DominoDisplay currentDominoDisplay, DominoDisplay newDominoDisplay) {
		for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
			DominoDisplay[] row = rows.get(rowIdx);
			for (int colIdx = 0; colIdx < row.length; colIdx++) {
				if (row[colIdx] == currentDominoDisplay) {
					row[colIdx] = newDominoDisplay;
					return;
				}
			}
		}
	}
	
	/**
	 * Adds the given domino as a domino display at the end of the table.
	 * @param domino domino to add
	 */
	public void addDomino(Domino domino) {
		combineAdjacentEmptyRows();
		
		DominoDisplay[] lastRow = rows.getLast();
		boolean lastRowEmpty = true;
		for (DominoDisplay dominoDisplay : lastRow) {
			if (dominoDisplay.getDomino() != null) {
				lastRowEmpty = false;
				break;
			}
		}
		
		if (lastRowEmpty && addDominoToFirstEmtpySpace(domino, rows.get(rows.size() - 2)))
			return;
			
		if (countEmptyDominoSpaces() > columnSize && addDominoToFirstEmtpySpace(domino, lastRow))
			return;
		
		addEmptyRow();
		
		if (addDominoToFirstEmtpySpace(domino, rows.get(rows.size() - 2)))
			return;
		
		rows.getLast()[0] = new DominoDisplay(domino, dominoEndSize);
	}
	
	/**
	 * Adds a domino to the first empty domino display space in the given row.
	 * @param domino domino to add
	 * @param row row to attempt to insert to domino
	 * @return true if an empty space was found and the domino was inserted; false if the domino was not added
	 */
	private boolean addDominoToFirstEmtpySpace(Domino domino, DominoDisplay[] row) {
		for (int colIdx = 0; colIdx < row.length; colIdx++) {
			if (row[colIdx].getDomino() == null) {
				row[colIdx] = new DominoDisplay(domino, dominoEndSize);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the number of empty domino spaces in the table.
	 * @return the number of empty domino spaces in the table
	 */
	private int countEmptyDominoSpaces() {
		int count = 0;
		for (DominoDisplay[] row : rows)
			for (DominoDisplay dominoDisplay : row)
				if (dominoDisplay.getDomino() == null)
					count++;
		
		return count;
	}
	
	/**
	 * Combines all adjacent empty rows of empty domino displays into one empty row of domino displays.
	 * @return true if rows were combined; false if nothing changed
	 */
	public boolean combineAdjacentEmptyRows() {
		boolean changed = false;
		
		boolean previousRowEmpty = false;
		Iterator<DominoDisplay[]> rowIterator = rows.iterator();
		while (rowIterator.hasNext()) {
			DominoDisplay[] row = rowIterator.next();
			boolean rowEmpty = true;
			for (DominoDisplay dominoDisplay : row) {
				if (dominoDisplay.getDomino() != null) {
					rowEmpty = false;
					break;
				}
			}
			
			if (rowEmpty && previousRowEmpty) {
				rowIterator.remove();
				changed = true;
			}
			
			previousRowEmpty = rowEmpty;
		}
		
		return changed;
	}
}
