package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import socket.ParticipantSocket;

/**
 * Dialog used to join a hosted game.
 */
public class JoinGameDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private ParticipantSocket socket = null;
	private String playerName = null;
	
	/**
	 * Creates a new Join Game Dialog.
	 * @param owner Participant GUI that created/owns this dialog
	 * @param defaultName name to display as default for the player 
	 * @param defaultIP host IP to display as default for the player
	 */
	public JoinGameDialog(final ParticipantGUI owner, String defaultName, String defaultIP) {
		super(owner, "Join Game");

		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 2;
		JPanel namePanel = new JPanel(new GridBagLayout());
		GridBagConstraints panelc = new GridBagConstraints();
		panelc.gridx = 0;
		panelc.gridy = 0;
		panelc.insets = new Insets(10, 0, 10, 10);
		panelc.anchor = GridBagConstraints.WEST;
		namePanel.add(new JLabel("NAME"), panelc);
		
		panelc.gridx++;
		final JTextField nameField = new JTextField(10);
		nameField.setDocument(new PlainDocument() {
			private static final long serialVersionUID = 1L;

			@Override
			public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
				if (offset < 10)
					super.insertString(offset, s, attributeSet);
			}
		});
		nameField.setText(defaultName);
		nameField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				nameField.selectAll();				
			}
			public void focusLost(FocusEvent arg0) {}
		});
		namePanel.add(nameField, panelc);
		getContentPane().add(namePanel, c);
		
		c.gridy++;
		c.insets.bottom = 0;
		getContentPane().add(new JLabel("Get the following from the host:"), c);
		c.gridwidth = 1;
		
		c.gridy++;
		getContentPane().add(new JLabel("HOST IP"), c);
		
		c.gridx++;
		getContentPane().add(new JLabel("Port"), c);
		c.insets.bottom = 10;
		
		c.gridx = 0;
		c.gridy++;
		c.insets.top = 2;
		final JTextField ipField = new JTextField((defaultIP == null) ? "174.52.107.129" : defaultIP, 10);
		getContentPane().add(ipField, c);
		
		c.gridx++;
		final JTextField portField = new JTextField(6);
		getContentPane().add(portField, c);
		c.insets.top = 10;
		
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 2;
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		panelc = new GridBagConstraints();
		panelc.gridx = 0;
		panelc.gridy = 0;
		panelc.insets = new Insets(10, 15, 0 ,15);
		// Button that signals the completion of player input and tried to connect to the host
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;
			
			public void buttonClicked() {
				try {
					String name = nameField.getText().replaceAll("[^\\w ]", "").trim();
					if (name.equals("")) {
						Messenger.error("Please enter a valid player name.", "Empty Name");
						return;
					}
					
					int port = Integer.parseInt(portField.getText());				
					socket = new ParticipantSocket();
					socket.connect(ipField.getText(), port, name);
					playerName = name;
										
					dispose();
					return;
				}
				catch (NumberFormatException ex) {
					Messenger.error("Please enter a valid port number.", "Invalid Port");
				}
				catch (UnknownHostException ex) {
					Messenger.error("Please enter a valid host ip.", "Unknown Host");
				}
				catch (IOException ex) {
					Messenger.error("Unable to connect to host.", "Connection Error");
				}
				if (socket != null)
					socket.close();
				socket = null;
			}
		};
		buttonPanel.add(okButton, panelc);
						
		panelc.gridx++;
		// Button that will cancel this dialog and not create any new players
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;
			
			public void buttonClicked() {				
				dispose();
			}
		};
		buttonPanel.add(cancelButton, panelc);
		getContentPane().add(buttonPanel, c);
		
		refresh();
		nameField.requestFocus();
	}
	
	/**
	 * Returns the host-connected Socket for the user joining the game.
	 * @return the host-connected Socket for the user joining the game
	 */
	public ParticipantSocket getParticipantSocket() {
		return socket;
	}
	
	/**
	 * Returns the name entered by the user.
	 * @return the name entered by the user
	 */
	public String getPlayerName() {
		return playerName;
	}
}
