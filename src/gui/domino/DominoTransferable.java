package gui.domino;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import model.Domino;

/**
 * Handler for defining a Domino object that can be dragged and dropped.
 */
public class DominoTransferable implements Transferable {
	
	private static DataFlavor dominoFlavor;
	private Domino domino;
	
	/**
	 * Creates a new DominoTransferable with a domino
	 * @param domino domino to transfer
	 */
	public DominoTransferable(Domino domino) {
		this.domino = domino;
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor) {
		if (isDataFlavorSupported(flavor))
			return domino;
		
		return null;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {getFlavor()};
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(getFlavor()));
	}
	
	/**
	 * Creates the Domino data flavor, if it does not exist, and returns it
	 * @return the Domino data flavor
	 */
	public static DataFlavor getFlavor() {
		if (dominoFlavor == null) {
			try {
				dominoFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=model.Domino");
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return dominoFlavor;
	}
}
