package com.company.geometry;

import java.util.Scanner;

public class Triangle extends Calculatrice {
	public Triangle(double a, double b, double c) {
		super(a,b,c);
	}

	public double calculSurface(double a, double b, double c) {
		double s = (a+b+c)/2;
		s = s * (s-a) * (s-b) * (s-c);

		return Math.sqrt(s);
	}

	// public double calculHypotenuse(double a, double b, double c) { return 0; }

	public void miseJDonnees() {}
}
