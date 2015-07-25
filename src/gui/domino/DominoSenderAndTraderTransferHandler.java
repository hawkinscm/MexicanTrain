package gui.domino;


import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Drag and Drop handler from which a Domino can be taken and dragged, either moving to the domino display location or trading places with it.
 */
public class DominoSenderAndTraderTransferHandler extends DominoTraderTransferHandler {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new Handler for sending and trading places for domino drag and drops.
	 * @param playerDominoesDialog display for player dominoes that will handle domino place trades
	 */
	public DominoSenderAndTraderTransferHandler(PlayerDominoesDialog playerDominoesDialog) {
		super(playerDominoesDialog);
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}
		
	@Override
	public Transferable createTransferable(JComponent c) {
		if (c instanceof DominoDisplay) {
			return new DominoTransferable(((DominoDisplay) c).getDomino());
		}
		
		return null;
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action == MOVE && source instanceof DominoDisplay)
			playerDominoesDialog.removeDominoDisplayOrTradePlaces((DominoDisplay) source);
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDataFlavorSupported(DominoTransferable.getFlavor()))
			return false;
		if (!(support.getComponent() instanceof DominoDisplay))
			return false;
				
		return true;
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support) || support.getDropAction() != TransferHandler.MOVE)
			return false;
		
		playerDominoesDialog.setTradePlacesDominoDisplay((DominoDisplay) support.getComponent());
		return true;
	}
}
