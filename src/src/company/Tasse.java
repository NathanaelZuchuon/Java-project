package src.company;

import java.io.*;
import java.net.*;

public class Tasse {
	public static void main(String[] args) throws IOException {
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;

		try {
			socket = new Socket("localhost", 8080); // Se connecte au serveur sur le port 8080
			System.out.println("Connecté au serveur...");

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String message;
			while (true) {
				System.out.print("Vous : ");
				message = stdIn.readLine();
				out.println(message); // Envoie un message au serveur

				if (message.equals("quit")) break; // Condition de sortie

				String response = in.readLine(); // Reçoit une réponse du serveur
				System.out.println("Serveur : " + response);
			}

		} catch (UnknownHostException e) {
			System.err.println("Serveur introuvable : " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Erreur client : " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}
