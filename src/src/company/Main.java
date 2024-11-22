package src.company;

// import javafx.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Main {
	private static void print(Object... objs) {
		/**
		 * System.out.println est trop long pour ZUCHUON
		 */
		for (Object obj : objs) {
			System.out.print(obj.toString() + " ");
			System.out.println();
		}
	}

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/test";
		String user = "root";
		String password = "";

		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			System.out.println("Connection Established ! \n");

			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM article");
			/// ResultSetMetaData metaData = result.getMetaData();

			while (result.next()) {
				// System.out.println(result.getString(1) + " " + result.getString(2));
				print(result.getString(1) + " " + result.getString(2));
			}
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage());
		}
	}
}
