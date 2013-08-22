package be.kuleuven.noiseapp.recording;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

public abstract class RecordingTask extends AsyncTask<Location, Integer, NoiseRecording> {
	
	private MediaRecorder mRecorder;
	private String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordsample.3gp";
	private ProgressDialog progressBar;
	private View v;
	protected RecordActivity rActivity;
	private int progressBarStatus;
	private ArrayList<Double> dBs =new ArrayList<Double>();
	private double avgDB = 0;
	private RecordingTask recordingTask;
	
	public RecordingTask(View v, RecordActivity rActivity){
		this.v = v;
		this.rActivity = rActivity;
		this.recordingTask = this;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setAudioChannels(2);
		mRecorder.setAudioEncodingBitRate(44100);
		mRecorder.setOutputFile(mFileName); 
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		try {
            mRecorder.prepare();
		} catch (IOException e) {
            Log.e("AudioRecordTest", "prepare() failed");
        }
		
		
		//prepare for a progress bar dialog
		progressBar = new ProgressDialog(v.getContext()){
			@Override
			public void onBackPressed(){
				this.dismiss();
				recordingTask.cancel(true);
				progressBarStatus = 0;
				return;
			}
			
			@Override
			public boolean onTouchEvent(MotionEvent e){
				this.dismiss();
				recordingTask.cancel(true);
				progressBarStatus = 0;
				return true;
			}
		};
		progressBar.setCancelable(true);
		progressBar.setCanceledOnTouchOutside(true);
		progressBar.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface arg0) {
				progressBar.dismiss();
				recordingTask.cancel(true);
				progressBarStatus = 0;
				return;
			}});
		progressBar.setMessage("Recording...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();

		//reset progress bar status
		progressBarStatus = 0;
		rActivity.addAccelerometerListener();
	}
	
	
	@Override
	protected NoiseRecording doInBackground(Location... locations) {
		Location currentLocation = locations[0];
		//ArrayList<Double> amps =new ArrayList<Double>();
		double amp = 0;
		
		mRecorder.start();
    	mRecorder.getMaxAmplitude();
        
    	progressBarStatus = 0;
    	
        while (progressBarStatus < 100 && !isCancelled()) {
			for(int i = 0; i<4;i++){
				try {
					Thread.sleep(250);
		
				} catch (InterruptedException e) {
					e.printStackTrace();
					progressBarStatus = 0;
				}
				
				amp = mRecorder.getMaxAmplitude();
				dBs.add(20 * Math.log10(amp / 0.5));
			}
			progressBarStatus += 10;
			publishProgress(progressBarStatus);
		}
        
        NoiseRecording recordedNoise = null;
		// ok, task is done
		if (progressBarStatus >= 100 && !isCancelled()) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			mRecorder=null;

			int size = 0;
			for(int i = 0; i < dBs.size(); i++){
				if(!dBs.get(i).isNaN() && !dBs.get(i).isInfinite() && dBs.get(i) != 0){
					avgDB += dBs.get(i);
					size++;
				}
			}
			avgDB = avgDB/size;

			// sleep 2 seconds, so that you can see the 100%
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(v.getContext());
			long userID = sp.getLong(MemoryFileNames.USERID, 0L);
			rActivity.removeAccelerometerListener();
			recordedNoise = new NoiseRecording(userID, currentLocation.getLatitude(), currentLocation.getLongitude(), avgDB, currentLocation.getAccuracy(), rActivity.calculateQuality());
			
		}
		return recordedNoise;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		 if(mRecorder != null){
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
		 }
		 rActivity.removeAccelerometerListener();
		 Toast.makeText(rActivity.getApplicationContext(), "Recording cancelled", Toast.LENGTH_SHORT).show();
		finish();
	}
	
	protected void finish() {
		mRecorder = null;
		mFileName = null;
		progressBar = null;
		v = null;
		rActivity = null;
		progressBarStatus = 0;
		dBs = null;
		avgDB = 0;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		progressBar.setProgress(values[0]);
	}
	
	@Override
	protected void onPostExecute(NoiseRecording result) {
		super.onPostExecute(result);
		progressBar.dismiss();
		rActivity.removeAccelerometerListener();
		saveNoiseRecording(result);
	}
	
	protected void saveNoiseRecording(NoiseRecording nr){
		try {
			new SaveNoiseRecordingRemoteTask().execute(nr).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		};
	}	
}