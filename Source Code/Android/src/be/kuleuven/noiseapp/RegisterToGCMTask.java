package be.kuleuven.noiseapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

public class RegisterToGCMTask extends AsyncTask<Void, Void, Void> {
	
	private MainActivity rActivity;
	private SharedPreferences sp;

	public RegisterToGCMTask(MainActivity rActivity){
		this.rActivity = rActivity;
		sp = PreferenceManager.getDefaultSharedPreferences(rActivity.getApplicationContext());
	}

	@Override
	protected Void doInBackground(Void... params) {
		while(sp.getLong(MemoryFileNames.USERID, 0L) == 0L){
			//wait
		}     
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result){
		rActivity.registerToGCMTask();
	}

	@Override
	protected void onCancelled(Void result){
	}
	
	
}
