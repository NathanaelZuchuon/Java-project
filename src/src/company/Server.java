package src.company;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.UUID;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {

	private static final int POOL_SIZE = 10; // Maximal number of clients in the pool
	private static final ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

	private static class objRef {
		/**
		 * Pass a variable by reference
		 */
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
		private static Map<String, String> clientsIDs = new TreeMap<>(); // Client IDs

		private String pseudo; // Client Pseudo
		private String clientId; // Client ID

		private PrintWriter out;
		private BufferedReader in;

		private InputStream inputStream;
		private OutputStream outputStream;
		private DataInputStream datainputStream;

		public ClientHandler(Socket socket) throws IOException {
			this.clientId = UUID.randomUUID().toString();
			// System.out.println(ClientHandler.clientsIDs.keySet());

			this.clientSocket = socket;
			this.inputStream = this.clientSocket.getInputStream();
			this.outputStream = this.clientSocket.getOutputStream();
			this.datainputStream = new DataInputStream(this.inputStream);

			this.pseudo = getPseudo();
			ClientHandler.CLIENTS++;
		}

		private String getPseudo() throws IOException {
			try {
				this.out = new PrintWriter(outputStream, true);
				this.out.println("Entrer votre pseudo");

				int i = 0;
				String pseudo;

				this.in = new BufferedReader(new InputStreamReader(this.inputStream));
				do {
					if ( i == 1 ) this.out.println("Pseudo existant, retry !");

					pseudo = this.in.readLine();

					System.out.println("Client [ %s ] : %s".formatted(this.clientId, pseudo));

					i = 1;
				} while (ClientHandler.clientsIDs.containsKey(pseudo));

				ClientHandler.clientsIDs.put("%s".formatted(pseudo), "%s".formatted(this.clientId));
				return pseudo;

			} catch (IOException e) {
				System.err.println("Pseudo ~ Erreur client : %s".formatted(e.getMessage()));

			}
			return "";
		}

		@Override
		public void run() {
			try {
				String welcome = "Pseudo disponible, %s ".formatted(this.pseudo) +
						"est connecté. Utiliser " +
						"/send \\file\\path\\file.ext pour envoyer un fichier.";

				this.out.println(welcome);

				String message;
				while ((message = this.in.readLine()) != null) {
					System.out.println("Client [ %s ] : %s".formatted(this.clientId, message));

					this.handleResponse(message);
				}

			} catch (IOException e) {
				System.err.println("Run ~ Erreur client : %s".formatted(e.getMessage()));

			} finally {
				try {
					this.clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void handleResponse(String message) throws IOException {
			String[] instructions = message.split(" ", 2);

			try {
				switch (instructions[0]) {
					case "404":
						this.out.println("Fichier non existant.");
						break;

					case "32":
						this.out.println("Fichier trop lourd");
						break;

					case "/send":
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

						this.out.println("Fichier ok !");
						break;

					default:
						this.out.println("Entrer quelque chose");
				}
				System.out.println("Client [ %s ] : (juste un message envoyé)".formatted(this.clientId));

			} catch (IOException e) {
				System.err.println("hR ~ Erreur client : %s".formatted(e.getMessage()));

			}
		}
	}
}
