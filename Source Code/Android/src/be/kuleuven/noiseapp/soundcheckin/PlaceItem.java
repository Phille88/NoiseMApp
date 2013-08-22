package be.kuleuven.noiseapp.soundcheckin;

import com.google.android.gms.maps.model.LatLng;

public class PlaceItem {
	
	private String name;
	private String address;
	private LatLng latLng;
	
	public PlaceItem(String name, String address, double lat, double lon){
		this.setName(name);
		this.setAddress(address);
		this.setLatLng(new LatLng(lat,lon));
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public LatLng getLatLng() {
		return latLng;
	}

}
