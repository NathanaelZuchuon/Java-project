package src.company;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

	private static ArrayList<ClientHandler> clients = new ArrayList<>();

	private Socket socket;
	private String pseudo;

	private PrintWriter out;
	private BufferedReader in;

	private InputStream inputStream;
	private OutputStream outputStream;

	public String getPseudo() { return this.pseudo; }

	public ClientHandler(Socket socket) {
		try {
			this.socket = socket;

			this.inputStream = this.socket.getInputStream();
			this.outputStream = this.socket.getOutputStream();

			this.out = new PrintWriter(outputStream, true);
			this.in = new BufferedReader(new InputStreamReader(this.inputStream));

			synchronized (clients) {
				clients.add(this);
			}

		} catch (Exception e) {
			closeAll(this.socket, this.inputStream, this.outputStream);
			System.err.println("Error while trying to construct ClientHandler.\n");
		}
	}

	@Override
	public void run() {
		try {
			// Read pseudo first
			this.pseudo = in.readLine();
			broadcastMessage("SERVER: %s connected.".formatted(this.pseudo), "all");

			// Lire les messages en continu
			String clientMessage;
			while ((clientMessage = in.readLine()) != null) {
				broadcastMessage("%s: %s".formatted(this.pseudo, clientMessage), "all");
			}

		} catch (IOException e) {
			System.err.println("Error while trying to run the ClientHandler.");
			closeAll(this.socket, this.inputStream, this.outputStream);
		}
	}

	public void sendMessage(String message) throws IOException {
		this.out.println(message);
	}

	public void broadcastMessage(String message, String receiver) {
		try {
			synchronized (clients) {
				if (receiver.equals("all")) {
					for (ClientHandler client : clients) {
						if (client != this) client.sendMessage(message);
					}

				} else {
					for (ClientHandler client : clients) {
						if (client.getPseudo().equals(receiver)) {
							client.sendMessage(message);
							break;
						}
					}
				}
			}

		} catch (Exception e) {
				closeAll(this.socket, this.inputStream, this.outputStream);
				System.err.println("Error while trying to broadcast message.\n");
		}
	}

	public void removeClient() {
		synchronized (clients) {
			clients.remove(this);
		}
		this.broadcastMessage("SERVER: %s left.".formatted(this.pseudo), "all");
	}

	public void closeAll(Socket socket, InputStream inputStream, OutputStream outputStream) {
		removeClient();
		try {
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();

			if (socket != null) socket.close();

		} catch (Exception e) {
			System.err.println("Error while trying to close all.\n");
		}
	}
}
