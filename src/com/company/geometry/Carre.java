package com.company.geometry;

import java.util.Scanner;

public class Carre {
	private double c;

	public Carre(double c) {
		this.c = c;
	}

	public double calculSurface() {
		return c*c;
	}

	public void miseJDonnees() {
		Scanner c = new Scanner(System.in);
		System.out.println("Entrer la valeur du coté: ");
		this.c = c.nextDouble(); c.close();

	}
}
