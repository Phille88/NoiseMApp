package be.kuleuven.noiseapp.noisehunt;

import android.view.View;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.recording.RecordingTask;
import be.kuleuven.noiseapp.soundbattle.NoiseLocation;

public class BlitzkriegRecordTask extends RecordingTask {
	BlitzkriegRecordActivity rActivity;

	public BlitzkriegRecordTask(View v, BlitzkriegRecordActivity rActivity) {
		super(v, rActivity);
		this.rActivity = rActivity;
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);		
		
		new SaveNoiseHuntRecordingTask(rActivity.getNoiseHuntID()).execute(result); //save remote
		
		rActivity.getRecordedLocations().add(new NoiseLocation(rActivity.getCurrentLocation().getLongitude(),rActivity.getCurrentLocation().getLatitude()));
		rActivity.setBrt(null);
	}

}
