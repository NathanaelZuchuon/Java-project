package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import com.company.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection Established ! \n");

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM article");

            while (result.next()) {
                System.out.println(result.getString(1) + " " + result.getString(2));
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}

/* import java.util.Scanner;
import com.company.equation.equaQuadratique;

public class Main {

    public static void main(String[] args) {
        Scanner var = new Scanner(System.in);

        System.out.println("Entrer a : ");
        double a = var.nextDouble();

        System.out.println("Entrer b : ");
        double b = var.nextDouble();

        System.out.println("Entrer c : ");
        double c = var.nextDouble();

        new equaQuadratique(a,b,c);

        var.close();
    }
} */

/* class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}


class Segment {
    private Point p1;
    private Point p2;
    private String couleur;
    private static int nbSegments = 0;

    public Segment(Point p1, Point p2, String couleur) {
        this.p1 = p1;
        this.p2 = p2;
        this.couleur = couleur;
        nbSegments++;
    }

    public double getLongueur() {
        // Concept : Calcul de distance
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }

    public boolean appartient(Point p) {
        // Concept : Vérification d'appartenance
        return (p.getX() >= Math.min(p1.getX(), p2.getX()) && p.getX() <= Math.max(p1.getX(), p2.getX()))
                && (p.getY() >= Math.min(p1.getY(), p2.getY()) && p.getY() <= Math.max(p1.getY(), p2.getY()));
    }

    @Override
    public String toString() {
        return "Segment [" + p1.getX() + ", " + p1.getY() + "] [" + p2.getX() + ", " + p2.getY() + "] - " + couleur;
    }

    public static int getNbSegments() {
        return nbSegments;
    }
}


public class Main {
    public static void main(String[] args) {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(3, 4);
        Segment segment = new Segment(p1, p2, "rouge");

        System.out.println(segment.toString());
        System.out.println("Longueur : " + segment.getLongueur());
        System.out.println("Appartient : " + segment.appartient(new Point(2, 3)));
    }
} */
