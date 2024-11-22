package src.company;

import java.io.*;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.*;

public class Server {

	private static final int POOL_SIZE = 10; // Number of clients in the pool
	private static final ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8080); // Listen on port 8080
			System.out.println("Serveur en attente de connexion sur le port %d ...".formatted(serverSocket.getLocalPort()));

			while (true) { // Continuously accept new connections
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connecté : %s".formatted(clientSocket.getInetAddress().toString()));

				// Submit client handling to the thread pool
				executor.submit(new ClientHandler(clientSocket));
			}

		} catch (IOException e) {
			System.err.println("Erreur serveur : %s".formatted(e.getMessage()));
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			executor.shutdown(); // Shutdown the thread pool gracefully
		}
	}

	// Inner class to handle client communication in a separate thread
	private static class ClientHandler implements Runnable {
		private static int CLIENTS = 0;
		private final Socket clientSocket;

		private String clientId; // Client ID

		public ClientHandler(Socket socket) {
			this.clientId = UUID.randomUUID().toString();
			this.clientSocket = socket;
			CLIENTS++;
		}

		@Override
		public void run() {
			try (
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
			) {
				String message;
				while ((message = in.readLine()) != null) {
					System.out.println("Client [ %s ] : %s".formatted(clientId, message));
					out.println("Bonjour " + message + " !");
				}
			} catch (IOException e) {
				System.err.println("Erreur client : %s".formatted(e.getMessage()));
			} finally {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
