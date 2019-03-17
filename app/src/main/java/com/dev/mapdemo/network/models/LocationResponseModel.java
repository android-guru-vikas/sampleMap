package com.dev.mapdemo.network.models;

public class LocationResponseModel{
	private double latitude;
	private String status;
	private double longitude;

	public void setLatitude(double latitude){
		this.latitude = latitude;
	}

	public double getLatitude(){
		return latitude;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setLongitude(double longitude){
		this.longitude = longitude;
	}

	public double getLongitude(){
		return longitude;
	}

	@Override
 	public String toString(){
		return 
			"LocationResponseModel{" + 
			"latitude = '" + latitude + '\'' + 
			",status = '" + status + '\'' + 
			",longitude = '" + longitude + '\'' + 
			"}";
		}
}
