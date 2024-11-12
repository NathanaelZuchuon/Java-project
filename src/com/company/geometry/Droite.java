package com.company.geometry;

public class Droite {
	private Point a;
	private Point b;

	public Droite(Point a, Point b) {
		this.a = a;
		this.b = b;
	} // Droite D = new Droite(A,B);

	public double dist(Point a, Point b) {
		return Math.sqrt( Math.pow(a.x - b.x,2) + Math.pow(a.y - b.y,2) );
	}
}
