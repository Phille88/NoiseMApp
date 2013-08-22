package be.kuleuven.noiseapp.randomrecord;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import be.kuleuven.noiseapp.recording.RecordActivity;

import be.kuleuven.noiseapp.R;
public class RandomRecordActivity extends RecordActivity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_random_record_name);
	}
	
	@Override
	protected OnClickListener recordButtonListener(){
		final RandomRecordActivity r = this;
		return new OnClickListener(){

		@Override
		public void onClick(View v) {

			if(isProviderFixed()){
				RandomRecordTask rt = new RandomRecordTask(v, r);
				rt.execute(getCurrentLocation());
			}
			else
				Toast.makeText(getApplicationContext(), "Wait for the GPS to have a fixed location", Toast.LENGTH_SHORT).show();
		}
	};
	}

	@Override
	protected boolean popupNeeded() {
		return true;
	}
	
	@Override
	protected int getActivityTitle(){
		return R.string.txt_random_record_name;
	}

	@Override
	protected int getPopupExplanation() {
		return R.string.txt_random_record_explanation;
	}
	
	@Override
	protected String getPopupDontShowAgainName(){
		return "RR_DSA";
	}
}