package be.kuleuven.noiseapp.tools;


import android.os.AsyncTask;

public class TimerTask extends AsyncTask<Integer, Void, Void> {
	iAfterTimer iat;
	
	public TimerTask(iAfterTimer iat){
		this.iat = iat;
	}
	
	@Override
	protected Void doInBackground(Integer... params) {
		try {
			Thread.sleep(params[0]);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void v){
		iat.afterTimer();
	}
}
