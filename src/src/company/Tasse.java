package src.company;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tasse {
	public static boolean handleMessage(String message, PrintWriter out, OutputStream outputStream) {
		if (message.equals("quit")) return false;

		String[] instructions = message.split(" ", 2);

		try {
			switch (instructions[0]) {
				case "/send":
					String filePath = System.getProperty("user.dir") + instructions[1];

					File file = new File(filePath);
					if (!file.exists() || !file.isFile()) {
						out.println("404");
						break;
					}

					long fileSize = file.length();
					if (fileSize > Integer.MAX_VALUE) {
						out.println("32");
						break;
					}

					out.println(message);

					DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
					FileInputStream fileInputStream = new FileInputStream(file);

					dataOutputStream.writeInt((int) fileSize); // Send file size

					byte[] buffer = new byte[8192]; // Buffer size
					int bytesRead;
					while ((bytesRead = fileInputStream.read(buffer)) != -1) {
						dataOutputStream.write(buffer, 0, bytesRead);
					}
					break;

				default:
					out.println(message);
			}

		} catch (IOException e) {
			System.err.println("hR ~ Erreur client : %s".formatted(e.getMessage()));

		}

		return true;
	}

	public static void main(String[] args) throws IOException {
		Socket socket = null;

		PrintWriter out = null;
		BufferedReader in = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			socket = new Socket("localhost", 8080); // Se connecte au serveur sur le port 8080
			System.out.println("Connecté au serveur ...\n");

			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

			in = new BufferedReader(new InputStreamReader(inputStream));
			out = new PrintWriter(outputStream, true);

			String message;
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			do {
				String response = in.readLine(); // Reçoit une réponse du serveur
				System.out.println("Serveur : " + response);

				System.out.print("Vous : ");
				message = stdIn.readLine();

			} while (handleMessage(message + " ", out, outputStream));

		} catch (UnknownHostException e) {
			System.err.println("Serveur introuvable : " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Main ~ Erreur client : " + e.getMessage());
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
