package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class NoiseLocation{
	private LatLng latLng;
	private double dB;
	private boolean recorded;

	
	public NoiseLocation(double longitude, double latitude){
		this.setLatLng(latitude,longitude);
	}

	public void setLatLng(double latitude, double longitude) {
		this.latLng = new LatLng(latitude,longitude);
	}

	public LatLng getLatLng() {
		return latLng;
	}
	
	/**
	 * Returns the distance between two coordinates in metres.
	 * 
	 * @param l1
	 * @param l
	 * @return
	 */
	public double getDistance(Location l){
		double earthRadius = 3958.75;
	    double dLat = Math.toRadians(l.getLatitude()-getLatLng().latitude);
	    double dLng = Math.toRadians(l.getLongitude()-getLatLng().longitude);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(getLatLng().latitude)) * Math.cos(Math.toRadians(l.getLatitude())) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;
	    return dist*meterConversion;
	}
	
	public double getDistance(NoiseLocation other) {
		double earthRadius = 3958.75;
	    double dLat = Math.toRadians(other.getLatLng().latitude-getLatLng().latitude);
	    double dLng = Math.toRadians(other.getLatLng().longitude-getLatLng().longitude);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(getLatLng().latitude)) * Math.cos(Math.toRadians(other.getLatLng().latitude)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;
	    return dist*meterConversion;
	}
	
	@Override
	public String toString(){
		return "(" + this.getLatLng().latitude + ", " + this.getLatLng().longitude + ")";
	}
	
	public boolean equals(NoiseLocation other){
		return (other.getLatLng().latitude == this.getLatLng().latitude) && (other.getLatLng().longitude == this.getLatLng().longitude);
	}

	public void setdB(int dB) {
		this.dB = dB;
	}

	public double getdB() {
		return dB;
	}
	
	public boolean liesInArea(ArrayList<LatLng> boundary){
      boolean result = false;
      for (int i = 0, j = boundary.size() - 1; i < boundary.size(); j = i++) {
        if ((boundary.get(i).longitude > this.getLatLng().longitude) != (boundary.get(j).longitude > this.getLatLng().longitude) &&
            (this.getLatLng().latitude < (boundary.get(j).latitude - boundary.get(i).latitude) * (this.getLatLng().longitude - boundary.get(i).longitude) / (boundary.get(j).longitude-boundary.get(i).longitude) + boundary.get(i).latitude)) {
          result = !result;
         }
      }
      return result;
	}

	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}

	public boolean isRecorded() {
		return recorded;
	}

}
