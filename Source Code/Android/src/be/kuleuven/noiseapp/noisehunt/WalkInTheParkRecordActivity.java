package be.kuleuven.noiseapp.noisehunt;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import be.kuleuven.noiseapp.recording.RecordActivity;
import be.kuleuven.noiseapp.soundbattle.NoiseLocation;
import be.kuleuven.noiseapp.tools.Constants;

import com.google.android.gms.maps.model.LatLng;

import be.kuleuven.noiseapp.R;
public class WalkInTheParkRecordActivity extends RecordActivity {

	private static final double FIFTY_METERS = 50;
	private ArrayList<NoiseLocation> recordedLocations = new ArrayList<NoiseLocation>();
	private ArrayList<LatLng> parkBoundary = new ArrayList<LatLng>();
	private WalkInTheParkRecordActivity witpra;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		witpra = this;
		initializeParkBoundary();
	}

	private void initializeParkBoundary() {
		parkBoundary.add(new LatLng(50.87574442047066,4.701322317123413));
		parkBoundary.add(new LatLng(50.875866279258275,4.7018373012542725));
		parkBoundary.add(new LatLng(50.875974597913086,4.702620506286621));
		parkBoundary.add(new LatLng(50.87603552704579,4.703006744384766));
		parkBoundary.add(new LatLng(50.87556840165932,4.703307151794434));
		parkBoundary.add(new LatLng(50.876793749588906,4.705849885940552));
		parkBoundary.add(new LatLng(50.87671928184973,4.705989360809326));
		parkBoundary.add(new LatLng(50.87640110016916,4.7056567668914795));
		parkBoundary.add(new LatLng(50.87537207220087,4.703328609466553));
		parkBoundary.add(new LatLng(50.875155431838586,4.703382253646851));
		parkBoundary.add(new LatLng(50.874755998530524,4.70190167427063));
		parkBoundary.add(new LatLng(50.87462736673647,4.701933860778809));
		parkBoundary.add(new LatLng(50.87458674609618,4.70139741897583));
		parkBoundary.add(new LatLng(50.87477630878132,4.701268672943115));
		parkBoundary.add(new LatLng(50.87488462996956,4.701719284057617));
		parkBoundary.add(new LatLng(50.87547362202403,4.701429605484009));
		parkBoundary.add(new LatLng(50.87532468220768,4.701300859451294));
		parkBoundary.add(new LatLng(50.87520282200387,4.701365232467651));
		parkBoundary.add(new LatLng(50.87574442047066,4.701322317123413));
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!isProviderFixed())
					Toast.makeText(getApplicationContext(),
							"Wait for the GPS to have a fixed location",
							Toast.LENGTH_LONG).show();
				else if (!isInPark())
					Toast.makeText(
							getApplicationContext(),
							"You have to get into the city park!",
							Toast.LENGTH_LONG).show();
				
				else if(!isFurtherThan50m()){
					Toast.makeText(
							getApplicationContext(),
							"You are too close to previous recorded location(s)!",
							Toast.LENGTH_LONG).show();
				}
				else {
					new WalkInTheParkRecordTask(v,witpra).execute(getCurrentLocation());
				}
		}
		};
	}	

	protected boolean isEverythingRecorded() {
		return this.getRecordedLocations().size() == 3;
	}
	
	private boolean isInPark(){
      boolean result = false;
      for (int i = 0, j = parkBoundary.size() - 1; i < parkBoundary.size(); j = i++) {
        if ((parkBoundary.get(i).longitude > getCurrentLocation().getLongitude()) != (parkBoundary.get(j).longitude > getCurrentLocation().getLongitude()) &&
            (getCurrentLocation().getLatitude() < (parkBoundary.get(j).latitude - parkBoundary.get(i).latitude) * (getCurrentLocation().getLongitude() - parkBoundary.get(i).longitude) / (parkBoundary.get(j).longitude-parkBoundary.get(i).longitude) + parkBoundary.get(i).latitude)) {
          result = !result;
         }
      }
      return result;
	}
	
	private boolean isFurtherThan50m(){
		for(NoiseLocation nl : getRecordedLocations())
			if(nl.getDistance(getCurrentLocation()) <= FIFTY_METERS)
				return false;
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_noise_hunt_walkinthepark);
	}

	@Override
	protected boolean popupNeeded() {
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_noise_hunt_walkinthepark;
	}

	@Override
	protected int getPopupExplanation() {
		return R.string.txt_desc_noise_hunt_walkinthepark;
	}
	
	protected void setHuntDone(){
		new SaveFinishedNoiseHuntTask(this).execute(getNoiseHuntID());
	}

	@Override
	protected String getPopupDontShowAgainName() {
		return "WITP_DSA";
	}

	protected ArrayList<NoiseLocation> getRecordedLocations() {
		return recordedLocations;
	}
	
	@Override
	protected Location getCurrentLocation() {
		return super.getCurrentLocation();
	}
	
	protected int getNoiseHuntID(){
		return Constants.WALKINTHEPARK_ID;
	}
}
