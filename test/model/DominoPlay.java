package model;

public class DominoPlay {
	public Domino domino;
	public String trainOwner;
	
	public DominoPlay(Domino domino, String trainOwner) {
		this.domino = domino;
		this.trainOwner = trainOwner;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof DominoPlay))
			return false;
		
		DominoPlay that = (DominoPlay) o;
		return (this.domino.equals(that.domino) && this.trainOwner.equals(that.trainOwner));
	}
	
	@Override
	public String toString() {
		return "DominoPlay:" + domino.toString() + "," + trainOwner; 
	}
}
