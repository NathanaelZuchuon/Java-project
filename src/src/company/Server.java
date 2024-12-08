package src.company;

/*
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.UUID;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {

	private static final int POOL_SIZE = 10; // Maximal number of clients in the pool
	private static final ExecutorService executorClient = Executors.newFixedThreadPool(POOL_SIZE);

	private static final ExecutorService executorInput = Executors.newFixedThreadPool(POOL_SIZE);
	private static final ExecutorService executorOutput = Executors.newFixedThreadPool(POOL_SIZE);

	private static class objRef {
		// Pass a variable by reference

		public boolean value;

		public objRef(boolean value) {
			this.value = value;
		}
	}

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8080); // Listen on port 8080
			System.out.println("Serveur en attente de connexion sur le port %d ...\n".formatted(serverSocket.getLocalPort()));

			Socket clientSocket = null;
			while (true) { // Continuously accept new connections
				clientSocket = serverSocket.accept();
				System.out.println("Client connecté : %s".formatted(clientSocket.getInetAddress().toString()));

				// Submit client handling to the thread pool
				executorClient.submit(new ClientHandler(clientSocket));
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
			executorClient.shutdown(); // Shutdown the thread pool gracefully

			executorInput.shutdown(); // Shutdown the thread inputStream gracefully
			executorOutput.shutdown(); // Shutdown the thread outputStream gracefully
		}
	}

	// Inner class to handle client communication in a separate thread
	private static class ClientHandler implements Runnable {
		private Socket clientSocket;

		private static int CLIENTS = 0;
		private static Map<String, String> clientsIDs = new TreeMap<>(); // Client IDs
		private static Map<String, Socket> clientSockets = new HashMap<>(); // Client sockets List

		private String pseudo; // Client Pseudo
		private String clientId; // Client ID

		private PrintWriter out;
		private BufferedReader in;

		private InputStream inputStream;
		private OutputStream outputStream;

		private DataInputStream datainputStream;

		public ClientHandler(Socket socket) throws IOException {
			this.clientId = UUID.randomUUID().toString();

			this.clientSocket = socket;
			this.inputStream = this.clientSocket.getInputStream();
			this.outputStream = this.clientSocket.getOutputStream();

			this.datainputStream = new DataInputStream(this.inputStream);

			String tps = getPseudo();
			if (tps != "") {
				this.pseudo = tps;
				ClientHandler.CLIENTS++;
			}
		}

		private String pseudoVerification(String pseudo) {
			if (pseudo.equals("quit")) {
				return "q";
			} else {
				return "ok";
			}
		}

		private String getPseudo() throws IOException {
			try {
				this.out = new PrintWriter(outputStream, true);
				this.out.println("Entrer votre pseudo." + ".pseudo");

				int i = 0;
				String pseudo;

				this.in = new BufferedReader(new InputStreamReader(this.inputStream));
				do {
					if ( i == 1 ) this.out.println("Pseudo existant, retry !" + ".pseudo");

					pseudo = this.in.readLine();

					switch (this.pseudoVerification(pseudo)) {
						case "q":
							this.clientSocket.close();
							return "";

						case "ok":
							break;
					}

					System.out.println("Client [ %s ] : %s".formatted(this.clientId, pseudo));

					i = 1;
				} while (ClientHandler.clientsIDs.containsKey(pseudo));

				ClientHandler.clientsIDs.put("%s".formatted(pseudo), "%s".formatted(this.clientId));
				ClientHandler.clientSockets.put("%s".formatted(pseudo), this.clientSocket);

				return pseudo;

			} catch (IOException e) {
				System.err.println("getPseudo ~ Erreur client : %s".formatted(e.getMessage()));

			}
			return "";
		}

		@Override
		public void run() {
			executorInput.submit(new ClientInputHandler(this.in));
			executorOutput.submit(new ClientOutputHandler(this.out));

			try {
				String welcome = "Pseudo disponible, %s ".formatted(this.pseudo) +
						"est connecté. Utiliser " +
						"/send \\file\\path\\file.ext pour envoyer un fichier; " +
						"@pseudo msg pour envoyer un message à pseudo.";

				// this.out.println(welcome + ".msg");
				ClientOutputHandler.setMessage(welcome + ".msg");

				String message;
				while ((message = ClientInputHandler.readLine()) != null) {
					System.out.println("Client [ %s ] : %s".formatted(this.clientId, message));

					this.handleResponse(message);
				}

			} catch (IOException e) {
				System.err.println("run ~ Erreur client : %s".formatted(e.getMessage()));

			} finally {
				try {
					this.clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void handleResponse(String message) throws IOException {
			switch (message) {
				case "quit":
					this.clientSocket.close();
					break;

				case "404":
					ClientOutputHandler.setMessage("Fichier non existant." + ".msg");
					break;

				case "402":
					ClientOutputHandler.setMessage("Chemin d'accès inexistant." + ".msg");
					break;

				case "32":
					ClientOutputHandler.setMessage("Fichier trop lourd." + ".msg");
					break;

				default:
					String[] instructions = message.split(" ", 2);
					String command = instructions[0];

					try {
						switch (command.charAt(0)) {
							case '/':
								switch (command.substring(1)) {
									case "send":
										this.datainputStream = new DataInputStream(this.inputStream);
										int fileSize = this.datainputStream.readInt();

										byte[] fileData = new byte[fileSize];
										int bytesRead = 0;
										int totalBytesRead = 0;
										while (totalBytesRead < fileSize) {
											bytesRead = this.datainputStream.read(fileData, totalBytesRead, fileSize - totalBytesRead);
											if (bytesRead == -1) {
												throw new IOException("Client unexpectedly closed connection");
											}
											totalBytesRead += bytesRead;
										}

										LocalDateTime now = LocalDateTime.now();
										DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
										String filename = System.getProperty("user.dir") +
												"\\src\\src\\company\\uploads\\received_file_" + formatter.format(now) + ".dat";
										try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
											fileOutputStream.write(fileData);
											System.out.println("File received and saved as " + filename);
										}

										ClientOutputHandler.setMessage("Fichier ok !" + ".msg");
										break;

									default:
										ClientOutputHandler.setMessage("Entrer quelque chose." + ".msg");
								}

							case '@':
								String receiver = command.substring(1);
								Socket receiverSocket = ClientHandler.clientSockets.get(receiver);

								if (receiverSocket == this.clientSocket) {
									ClientOutputHandler.setMessage("@moi %s".formatted(instructions[1]) + ".msg");

								} else if (receiverSocket != null) {
									PrintWriter out = new PrintWriter(receiverSocket.getOutputStream(), true);
									String response = "@%s %s".formatted(this.pseudo, instructions[1]  + ".msg");

									out.println(response);

								} else {
									ClientOutputHandler.setMessage("Utilisateur non existant."  + ".msg");

								}
								break;

							default:
								ClientOutputHandler.setMessage("Entrer quelque chose." + ".msg");
						}

						System.out.println("Client [ %s ] : (juste un message envoyé)".formatted(this.clientId));

					} catch (IOException e) {
						System.err.println("hR ~ Erreur client : %s".formatted(e.getMessage()));

					}
			}
		}
	}

	private static class ClientInputHandler implements Runnable {
		private static BufferedReader in;

		public ClientInputHandler(BufferedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			while (true) {
				// if (bus == 1) {}
			}
		}

		public static String readLine() throws IOException {
			try {
				return ClientInputHandler.in.readLine();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class ClientOutputHandler implements Runnable {
		private PrintWriter out;
		private static String message = "";

		public static void setMessage(String message) {
			ClientOutputHandler.message = message;
		}

		public ClientOutputHandler(PrintWriter out) {
			this.out = out;
		}

		@Override
		public void run() {
			while (true) {
				if (!ClientOutputHandler.message.equals("")) {
					this.out.println(ClientOutputHandler.message);
					ClientOutputHandler.message = "";
				}
			}
		}
	}
}
*/

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
	private static final int PORT = 8080;

	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("Server started on port %d\n".formatted(PORT));

			// ExecutorService to handle Clients
			ExecutorService executorService = Executors.newCachedThreadPool();

			while (!serverSocket.isClosed()) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected...");

				// ClientHandler for each Client
				ClientHandler clientHandler = new ClientHandler(clientSocket);
				executorService.execute(clientHandler);
			}

		} catch (IOException e) {
			System.err.println("Error while trying to run the server.");
		}
	}
}
