package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * A host-connected socket that is used to communicate back and forth between the host and the participant.
 */
public class ParticipantSocket extends Socket {
	
	private String host = null;
	private int port;
	
	/**
	 * Creates a new empty, unconnected Participant Socket.
	 */
	public ParticipantSocket() {}
			
	/**
	 * Connects to the given host and port and communicates the participant's player name.
	 * @param host host to connect to
	 * @param playerIndex index of the player used to determine the port number to connect to
	 * @param playerName name of the player
	 * @throws IOException on any socket, reading, or writing error
	 */
	public void connect(String host, int playerIndex, String playerName) throws IOException {
		if (playerIndex < 0 || playerIndex > 9)
			throw new NumberFormatException("playerIndex must be between 0 and 9");
		
		socket = new java.net.Socket(host, BASE_PORT + playerIndex);
		this.host = host;
		this.port = BASE_PORT + playerIndex;
		isCleanlyClosed = false;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		writer.println(playerName);
		writer.flush();
	}
	
	/**
	 * Closes and reconnects the socket.
	 * @param name of the player to reconnect as
	 * @throws IOException on any socket/IO error
	 */
	public void reconnect(String playerName) throws IOException {
		try { socket.close(); } catch (IOException ex) {}
		socket = new java.net.Socket(host, port);
		isCleanlyClosed = false;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		writer.println(playerName);
		writer.flush();
	}
	
	/**
	 * Returns the host connection text (usually an IP).
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
		
	/**
	 * Returns the connection socket port number used by this Player Socket.
	 * @return the connection socket port number used by this Player Socket
	 */
	public int getPortIndex() {
		if (socket == null)
			return -1;
		return socket.getPort() - BASE_PORT;
	}
}
