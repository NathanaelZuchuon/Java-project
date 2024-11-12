package com.company.geometry;

import java.util.Scanner;

public class Cercle {
	private double r;

	public Cercle(double r) {
		this.r = r;
	}

	public double calculSurface() {
		return Math.PI*r*r;
	}

	public void miseJDonnees() {
		Scanner r = new Scanner(System.in);
		System.out.println("Entrer la valeur du rayon: ");
		this.r = r.nextDouble(); r.close();
	}
}
