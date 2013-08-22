package be.kuleuven.noiseapp.noisehunt;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import be.kuleuven.noiseapp.R;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.recording.RecordActivity;
import be.kuleuven.noiseapp.soundbattle.NoiseLocation;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.TimerTask;
import be.kuleuven.noiseapp.tools.iAfterTimer;

public class BlitzkriegRecordActivity extends RecordActivity implements iAfterTimer {

	private BlitzkriegRecordActivity bra;
	private ArrayList<NoiseLocation> recordedLocations = new ArrayList<NoiseLocation>();
	private static final double FIFTY_METERS = 50;
	private static final int TWO_MINUTES = 120000;
	private BlitzkriegRecordTask currentBrt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bra = this;
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
				else if(!isFurtherThan50m()){
					Toast.makeText(
							getApplicationContext(),
							"You have to get further away from the last record location!",
							Toast.LENGTH_LONG).show();
				}
				else {
					if(getRecordedLocations().isEmpty())
						new TimerTask(bra).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, TWO_MINUTES); //2 minutes
					setBrt(new BlitzkriegRecordTask(v,bra));
					getBrt().execute(getCurrentLocation());
				}
		}
		};
	}
	
	private boolean isFurtherThan50m(){
		for(NoiseLocation nl : getRecordedLocations())
			if(nl.getDistance(getCurrentLocation()) <= FIFTY_METERS)
				return false;
		return true;
	}

	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_noise_hunt_blitzkrieg);
	}

	@Override
	protected boolean popupNeeded() {
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_noise_hunt_blitzkrieg;
	}

	@Override
	protected int getPopupExplanation() {
		return R.string.txt_desc_noise_hunt_blitzkrieg;
	}

	@Override
	protected String getPopupDontShowAgainName() {
		return "B_DSA";
	}
	
	protected void setHuntDone(){
		new SaveFinishedNoiseHuntTask(this).execute(getNoiseHuntID());
	}

	protected ArrayList<NoiseLocation> getRecordedLocations() {
		return recordedLocations;
	}
	
	@Override
	protected Location getCurrentLocation() {
		return super.getCurrentLocation();
	}

	@Override
	public void afterTimer() {
		if(getBrt() != null){
			getBrt().cancel(true);
			while(!getBrt().isCancelled()){
				//wait
			}
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		RecordingPoints rr = null;
		try {
			rr = new CalculateNoiseHuntPoints().execute((long) getNoiseHuntID(), sp.getLong(MemoryFileNames.USERID, 0L)).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		setHuntDone();
		Intent i = new Intent(getApplicationContext(), NoiseHuntPointsActivity.class);
		i.putExtra(MemoryFileNames.NOISEHUNT_POINTS, rr);
		i.putExtra(MemoryFileNames.NOISEHUNT_TITLE, getActivityTitle());
		startActivity(i);
		finish();
	}

	protected void setBrt(BlitzkriegRecordTask brt) {
		this.currentBrt = brt;
	}

	protected BlitzkriegRecordTask getBrt() {
		return currentBrt;
	}
	
	protected int getNoiseHuntID(){
		return Constants.BLITZKRIEG_ID;
	}
}
