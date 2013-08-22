package be.kuleuven.noiseapp.soundbattle;

import android.content.Intent;
import android.view.View;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.recording.RecordingTask;

public class SoundBattleRecordTask extends RecordingTask {
	
	SoundBattleRecordActivity rActivity;

	public SoundBattleRecordTask(View v, SoundBattleRecordActivity rActivity) {
		super(v, rActivity);
		this.rActivity = rActivity;
	}
	
	@Override
	public void onPostExecute(NoiseRecording result){
		super.onPostExecute(result);
		
		SoundBattleLocation sbl = rActivity.getClosestSoundBattleLocationToRecord();
		sbl.setRecorded(true);
		rActivity.updateMarkers();

		//store state
		new SaveSoundBattleRecordingTask(sbl).execute(result);

		if (rActivity.isEverythingRecorded()) {			
			Intent i = new Intent(rActivity.getApplicationContext(), SoundBattleWaitActivity.class);
			rActivity.startActivity(i);
			rActivity.finish();
		}
		finish();
	}
}
