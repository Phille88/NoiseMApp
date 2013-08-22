package be.kuleuven.noiseapp.noisehunt;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.recording.RecordingTask;
import be.kuleuven.noiseapp.soundbattle.NoiseLocation;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

public class PartyTimeRecordTask extends RecordingTask {
	PartyTimeRecordActivity rActivity;
	
	public PartyTimeRecordTask(View v, PartyTimeRecordActivity rActivity) {
		super(v, rActivity);
		this.rActivity = rActivity;
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);		
		
		new SaveNoiseHuntRecordingTask(rActivity.getNoiseHuntID()).execute(result); //save remote
		
		rActivity.getRecordedLocations().add(new NoiseLocation(rActivity.getCurrentLocation().getLongitude(),rActivity.getCurrentLocation().getLatitude()));
		if (rActivity.isEverythingRecorded()) {
			//store points
			RecordingPoints rr = null;
			try {
				rr = new CalculateNoiseHuntPoints().execute((long) rActivity.getNoiseHuntID(), result.getUserID()).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			rActivity.setHuntDone();
			Intent i = new Intent(rActivity.getApplicationContext(), NoiseHuntPointsActivity.class);
			i.putExtra(MemoryFileNames.NOISEHUNT_POINTS, rr);
			i.putExtra(MemoryFileNames.NOISEHUNT_TITLE, rActivity.getActivityTitle());
			rActivity.startActivity(i);
			rActivity.finish();
		}
	}
}
