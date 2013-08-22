package be.kuleuven.noiseapp.showmap;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.recording.RecordActivity;

import be.kuleuven.noiseapp.R;
public class ShowMapActivity extends RecordActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setZoomLevel(13);
		super.onCreate(savedInstanceState);
		showNoiseRecordings();
		
		Button btn_record = (Button) findViewById(R.id.btn_record);
		btn_record.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void addListenerToRecordButton(){
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return null;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_show_map_name);
	}

	@Override
	protected boolean popupNeeded() {
		return false;
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_show_map_name;
	}

	@Override
	protected int getPopupExplanation() {
		return 0;
	}
	
	private void showNoiseRecordings(){
		super.mMap.clear();
		ArrayList<NoiseRecording> recordings;
		try {
			recordings = new GetAllNoiseRecordingsTask().execute().get();
			for (NoiseRecording nr : recordings){
				super.mMap.addMarker(nr.getMarker());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			
		}
	}

	@Override
	protected String getPopupDontShowAgainName() {
		return null;
	}

}
