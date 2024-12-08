package src.company;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private String pseudo;
	private Socket socket;

	private PrintWriter out;
	private BufferedReader in;

	private InputStream inputStream;
	private OutputStream outputStream;

	public Client(Socket socket) throws IOException {
		try {
			this.socket = socket;

			this.inputStream = this.socket.getInputStream();
			this.outputStream = this.socket.getOutputStream();

			this.out = new PrintWriter(this.outputStream, true);
			this.in = new BufferedReader(new InputStreamReader(this.inputStream));

		} catch (IOException e) {
			closeAll(this.socket, this.inputStream, this.outputStream);
			System.err.println("Error while trying to run Client.");
		}
	}

	public void closeAll(Socket socket, InputStream inputStream, OutputStream outputStream) throws IOException {
		try {
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();

			if (socket != null) socket.close();

		} catch (IOException e) {
			System.err.println("Error while trying to all close the client socket.\n");
		}
	}

	public void listenToMessages() {
		// Receive messages Thread
		new Thread(() -> {
			try {
				String message;
				while ((message = in.readLine()) != null) {
					System.out.println("\n%s".formatted(message));
					System.out.print("Vous: ");
				}

			} catch (IOException e) {
				e.printStackTrace();

			}
		}).start();
	}

	public void sendMessages() {
		try {
			// Get pseudo
			System.out.print("Entrez votre pseudo: ");

			Scanner scanner = new Scanner(System.in);
			String pseudo = scanner.nextLine();

			this.out.println(pseudo);
			System.out.print("Vous: ");

			// Sending messages Thread
			new Thread(() -> {
				try {
					while (this.socket.isConnected()) {
						String message = scanner.nextLine();
						this.out.println(message);
						System.out.print("Vous: ");
					}

				} catch (Exception e) {
					System.err.println("Error while trying to send messages.");

				}
			}).start();

		} catch (Exception e) {
			System.err.println("Error while trying to get pseudo.");
		}
	}

	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("localhost", 8080);
		Client client = new Client(socket);

		System.out.println("Connected on port 8080\n");

		client.listenToMessages();
		client.sendMessages();
	}
}
