package com.example.navidration;

public class Fountain {
	
	public static int FOUNTAINID = 0;
	public static int LATITUDE = 1;
	public static int LONGITUDE = 2;
	public static int NYES = 3;
	public static int NNO = 4;
	
	protected int id;
	protected double latitude;
	protected double longitude;
	protected int nYes;
	protected int nNo;
	
	Fountain(int id, double latitude, double longitude, int nYes, int nNo) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.nYes = nYes;
		this.nNo = nNo;
	}
}
