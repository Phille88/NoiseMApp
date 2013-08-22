package be.kuleuven.noiseapp.soundcheckin;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.randomrecord.RandomRecordPointsActivity;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.recording.RecordingTask;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

public class SoundCheckinRecordTask extends RecordingTask {
	String placeName;
	public SoundCheckinRecordTask(View v, SoundCheckinActivity rActivity, String placeName) {
		super(v, rActivity);
		this.placeName = placeName;
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);
		
		new SaveSoundCheckinRecordingTask(placeName).executeOnExecutor(THREAD_POOL_EXECUTOR, result);
		
		RecordingPoints rr = null;
		try {
			rr = new CalculateSoundCheckinPoints(placeName).executeOnExecutor(THREAD_POOL_EXECUTOR, result).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		result.setRecordingPoints(rr);
		
		Intent i = new Intent(rActivity.getApplicationContext(), RandomRecordPointsActivity.class);
		i.putExtra(MemoryFileNames.LAST_NOISERECORDING,result);
		i.putExtra(MemoryFileNames.COMINGFROMSOUNDCHECKIN, true);
		rActivity.startActivity(i);
		finish();
	}
}
