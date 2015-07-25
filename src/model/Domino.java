package model;

/**
 * Represents a domino.
 */
public class Domino {

	private int endOne;
	private int endTwo;
	
	/**
	 * Creates a new domino with the given pip counts on each end.
	 * @param endOne number of pips on one end of the domino
	 * @param endTwo number of pips on the other end of the domino 
	 */
	public Domino(int endOne, int endTwo) {
		this.endOne = endOne;
		this.endTwo = endTwo;
	}
	
	/**
	 * Returns the number of pips on one end of the domino.
	 * @return the number of pips on one end of the domino
	 */
	public int getEndOneCount() {
		return endOne;
	}
	
	/**
	 * Returns the number of pips on the other end of the domino.
	 * @return the number of pips on the other end of the domino
	 */
	public int getEndTwoCount() {
		return endTwo;
	}
	
	/**
	 * Returns whether or not this domino is a double.
	 * @return true if the count of pips on one end match the count of pips on the other end; false otherwise
	 */
	public boolean isDouble() {
		return (endOne == endTwo);
	}
	
	/**
	 * Returns the count of pips, or score value, of this domino.
	 * @return the count of pips, or score value, of this domino
	 */
	public int getPipScore() {
		if (endOne == 0 && endTwo == 0)
			return 50;
		
		return endOne + endTwo;
	}
	
	/**
	 * Flips the domino display - side one and side two change places.
	 */
	public void flip() {
		int temp = endOne;
		endOne = endTwo;
		endTwo = temp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Domino))
			return false;
		
		Domino that = (Domino) obj;
		return ((this.endOne == that.endOne && this.endTwo == that.endTwo) ||
				(this.endOne == that.endTwo && this.endTwo == that.endOne));
	}
	
	@Override
	public String toString() {
		return "Domino(" + endOne + "," + endTwo + ")";
	}
}
