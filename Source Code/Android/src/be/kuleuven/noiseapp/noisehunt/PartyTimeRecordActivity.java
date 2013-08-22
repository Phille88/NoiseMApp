package be.kuleuven.noiseapp.noisehunt;

import java.util.ArrayList;
import java.util.Calendar;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import be.kuleuven.noiseapp.recording.RecordActivity;
import be.kuleuven.noiseapp.soundbattle.NoiseLocation;
import be.kuleuven.noiseapp.tools.Constants;

import com.google.android.gms.maps.model.LatLng;

import be.kuleuven.noiseapp.R;
public class PartyTimeRecordActivity extends RecordActivity {

	private PartyTimeRecordActivity ptra;
	private ArrayList<NoiseLocation> recordedLocations = new ArrayList<NoiseLocation>();
	private ArrayList<ArrayList<LatLng>> boundaries = new ArrayList<ArrayList<LatLng>>();
	//private ArrayList<LatLng> groteMarktBoundary = new ArrayList<LatLng>();
	private ArrayList<LatLng> centreBoundary = new ArrayList<LatLng>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ptra = this;
		initializeBoundaries();
	}
	
	private void initializeBoundaries() {
//		initializeGroteMarkt();
		initializeCentre();
	}

	private void initializeCentre() {
		centreBoundary.add(new LatLng(50.87769412283608,4.698296785354614));
		centreBoundary.add(new LatLng(50.878181535682046,4.698554277420044));
		centreBoundary.add(new LatLng(50.878641865355405,4.6989405155181885));
		centreBoundary.add(new LatLng(50.87916988496933,4.699133634567261));
		centreBoundary.add(new LatLng(50.879576049831705,4.6986401081085205));
		centreBoundary.add(new LatLng(50.88018529048719,4.698811769485474));
		centreBoundary.add(new LatLng(50.880767446337664,4.699004888534546));
		centreBoundary.add(new LatLng(50.8801717518925,4.700485467910767));
		centreBoundary.add(new LatLng(50.8795354335048,4.701944589614868));
		centreBoundary.add(new LatLng(50.87892618435252,4.7033607959747314));
		centreBoundary.add(new LatLng(50.87946773954791,4.704519510269165));
		centreBoundary.add(new LatLng(50.87918342385511,4.7052061557769775));
		centreBoundary.add(new LatLng(50.87788367288198,4.704326391220093));
		centreBoundary.add(new LatLng(50.877639965538506,4.704626798629761));
		centreBoundary.add(new LatLng(50.877247323248724,4.705291986465454));
		centreBoundary.add(new LatLng(50.87792429064865,4.706064462661743));
		centreBoundary.add(new LatLng(50.87853355290019,4.7052061557769775));
		centreBoundary.add(new LatLng(50.87967082112353,4.706836938858032));
		centreBoundary.add(new LatLng(50.87845231839358,4.709519147872925));
		centreBoundary.add(new LatLng(50.877220244348166,4.707459211349487));
		centreBoundary.add(new LatLng(50.87652972706758,4.706665277481079));
		centreBoundary.add(new LatLng(50.876421409703035,4.705699682235718));
		centreBoundary.add(new LatLng(50.87707131011386,4.704455137252808));
		centreBoundary.add(new LatLng(50.87743687511202,4.703446626663208));
		centreBoundary.add(new LatLng(50.876827598521906,4.702008962631226));
		centreBoundary.add(new LatLng(50.87616415495323,4.700571298599243));
		centreBoundary.add(new LatLng(50.87716608649985,4.697974920272827));
		centreBoundary.add(new LatLng(50.87769412283608,4.698296785354614));
		boundaries.add(centreBoundary);
	}

//	private void initializeGroteMarkt() {
//		groteMarktBoundary.add(new LatLng(0,0));
//		boundaries.add(groteMarktBoundary);
//	}

	@Override
	protected OnClickListener recordButtonListener() {
		return new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!isProviderFixed())
					Toast.makeText(getApplicationContext(),
							"Wait for the GPS to have a fixed location",
							Toast.LENGTH_LONG).show();
				else if(!isPartyTime()){
					Toast.makeText(getApplicationContext(), "Wait until 10 p.m. to go out!", Toast.LENGTH_LONG).show();
				}
				else if (!isInPartyNeighborhood())
					Toast.makeText(
							getApplicationContext(),
							"You have to get into the city centre where the party is at!",
							Toast.LENGTH_LONG).show();
				else {
					new PartyTimeRecordTask(v,ptra).execute(getCurrentLocation());
				}
		}
		};
	}

	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_noise_hunt_partytime);
	}

	@Override
	protected boolean popupNeeded() {
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_noise_hunt_partytime;
	}

	@Override
	protected int getPopupExplanation() {
		return R.string.txt_desc_noise_hunt_partytime;
	}
	
	protected void setHuntDone(){
		new SaveFinishedNoiseHuntTask(this).execute(getNoiseHuntID());
	}

	@Override
	protected String getPopupDontShowAgainName() {
		return "PT_DSA";
	}
	
	protected boolean isEverythingRecorded() {
		return this.getRecordedLocations().size() == 3;
	}
	
	protected ArrayList<NoiseLocation> getRecordedLocations() {
		return recordedLocations;
	}
	
	@Override
	protected Location getCurrentLocation() {
		return super.getCurrentLocation();
	}
	
	private boolean isInPartyNeighborhood(){
	      boolean result = false;
	      for(ArrayList<LatLng> boundary : boundaries){
		      for (int i = 0, j = boundary.size() - 1; i < boundary.size(); j = i++) {
		        if ((boundary.get(i).longitude > getCurrentLocation().getLongitude()) != (boundary.get(j).longitude > getCurrentLocation().getLongitude()) &&
		            (getCurrentLocation().getLatitude() < (boundary.get(j).latitude - boundary.get(i).latitude) * (getCurrentLocation().getLongitude() - boundary.get(i).longitude) / (boundary.get(j).longitude-boundary.get(i).longitude) + boundary.get(i).latitude)) {
		          result = !result;
		         }
		      }
		      if(result == true)
		    	  return result;
	      }
	      return result;
	}
	
	private boolean isPartyTime(){
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		return (hour >= 22 || hour < 6);
	}
	
	protected int getNoiseHuntID(){
		return Constants.PARTYTIME_ID;
	}

}
