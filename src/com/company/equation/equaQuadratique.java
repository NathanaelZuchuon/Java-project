package com.company.equation;

public class equaQuadratique {
	private double a;
	private double b;
	private double c;

	public equaQuadratique(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;

		double delta = Math.pow(b,2) - 4*a*c;

		if ( delta < 0 ) {
			String i = String.format("%.2f", Math.sqrt(-delta)/2*a);
			double r = -b/2*a;

			System.out.println("Les deux solutions sont : " + r + "+i(" + i + ") et " + r + "-i(" + i + ") :)");
		} else if ( delta == 0 ) {
			System.out.println("L'unique solution est : " + (-b/2*a));
		} else {
			double x1 = ( -b + Math.sqrt(delta) ) / (2*a);
			double x2 = ( -b - Math.sqrt(delta) ) / (2*a);

			System.out.println("Les deux solutions sont : " + x1 + " et " + x2);
		}
	}
}
