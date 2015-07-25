package gui.domino;

import gui.PlayManager;

import javax.swing.TransferHandler;

import model.Domino;

/**
 * Drag and Drop handler that can receive a domino.
 */
public class DominoReceiverTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
		
	private PlayManager playManager;
	private String trainOwner;
	
	/**
	 * Creates a new Handler that can receive dragged and dropped dominoes.
	 * @param playManager playManager that determines if a domino can be played and handles the playing of it
	 * @param trainOwner name of the player who owns the train that contains this domino drop receiver
	 */
	public DominoReceiverTransferHandler(PlayManager playManager, String trainOwner) {
		this.playManager = playManager;
		this.trainOwner = trainOwner;
		getVisualRepresentation(null);
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDataFlavorSupported(DominoTransferable.getFlavor()))
			return false;
		if (!(support.getComponent() instanceof DominoReceiverDisplay))
			return false;
				
		return true;
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support) || support.getDropAction() != TransferHandler.MOVE)
			return false;
		
		try {
			Domino domino = (Domino) support.getTransferable().getTransferData(DominoTransferable.getFlavor());
			if (!playManager.canPlay(domino, trainOwner))
				return false;
			
			playManager.play(domino, trainOwner);
			return true;
		}
		catch (Exception ex) { ex.printStackTrace(); }
		
		return false;
	}
}