package gui.domino;


import javax.swing.TransferHandler;

/**
 * Drag and Drop receiver which can trade places with the domino display that is dropped on it.
 */
public class DominoTraderTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	
	protected PlayerDominoesDialog playerDominoesDialog;
	
	/**
	 * Creates a new Handler for sending and trading places for domino drag and drops.
	 * @param playerDominoesDialog display for player dominoes that will handle domino place trades
	 */
	public DominoTraderTransferHandler(PlayerDominoesDialog playerDominoesDialog) {
		this.playerDominoesDialog = playerDominoesDialog;
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
