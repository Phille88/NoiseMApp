package be.kuleuven.noiseapp.soundbattle;

import java.io.Serializable;

import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import be.kuleuven.noiseapp.R;
public class SoundBattleLocation extends NoiseLocation implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final double TEN_METRES = 10;
	private static final double FIFTY_METERS = 50;
	private long sblID;

	public SoundBattleLocation(double longitude, double latitude) {
		super(longitude, latitude);
		this.setRecorded(false);
	}
	
	public long getSoundBattleLocationID(){
		return sblID;
	}
	
	public void setSoundBattleLocationID(long sblID){
		this.sblID = sblID;
	}
	
	public MarkerOptions getMarker(Location currentLocation){
		if (isRecorded())
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("You have successfully recorded this place!")
					.icon(getRecordedIcon());
		else if (isClose(currentLocation))
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("You are ready to record at this location!")
					.icon(getCloseIcon());
		else if (isOnTheWay(currentLocation))
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("You are almost there!").icon(getOnTheWayIcon());
		else
			return new MarkerOptions().position(getLatLng())
					.title("Sound Battle Location")
					.snippet("Come closer to record here!").icon(getFarIcon());
	}
	
	public boolean isClose(Location l){
	    return getDistance(l) <= TEN_METRES && !isRecorded();
	}
	
	public boolean isOnTheWay(Location l){
	    return getDistance(l) > TEN_METRES && getDistance(l) <= FIFTY_METERS && !isRecorded();
	}
	
	public boolean isFar(Location l){
	    return getDistance(l) > FIFTY_METERS && !isRecorded();
	}
	
	private BitmapDescriptor getFarIcon(){
		return BitmapDescriptorFactory.fromResource(R.drawable.img_marker_far);
	}
	
	private BitmapDescriptor getCloseIcon(){
		return BitmapDescriptorFactory.fromResource(R.drawable.img_marker_close);
	}
	
	private BitmapDescriptor getOnTheWayIcon(){
		return BitmapDescriptorFactory.fromResource(R.drawable.img_marker_ontheway);
	}
	
	private BitmapDescriptor getRecordedIcon(){
		return BitmapDescriptorFactory.fromResource(R.drawable.img_marker_recorded);
	}

}
