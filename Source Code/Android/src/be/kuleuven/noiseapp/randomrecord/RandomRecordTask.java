package be.kuleuven.noiseapp.randomrecord;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.recording.RecordingTask;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

public class RandomRecordTask extends RecordingTask {

	public RandomRecordTask(View v, RandomRecordActivity rActivity) {
		super(v, rActivity);
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);
		RecordingPoints rr = null;
		try {
			rr = new CalculateRandomRecordPoints().executeOnExecutor(THREAD_POOL_EXECUTOR, result).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		result.setRecordingPoints(rr);
		
		Intent i = new Intent(rActivity.getApplicationContext(), RandomRecordPointsActivity.class);
		i.putExtra(MemoryFileNames.LAST_NOISERECORDING,result);
		rActivity.startActivity(i);
		//rActivity.finish();
		finish();
	}

}
