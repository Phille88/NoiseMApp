package be.kuleuven.noiseapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class WaitForLoadingTask extends AsyncTask<Void, Void, Void> {
	
	private MainActivity rActivity;
	private ProgressDialog progressDialog;
	
	public WaitForLoadingTask(MainActivity rActivity){
		this.rActivity = rActivity;
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		progressDialog = new ProgressDialog(rActivity.getWindow().getDecorView().findViewById(android.R.id.content).getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		while(!rActivity.isInitializationDone()){
			//wait
		}
		return null;
	}
	
//	@Override
//	protected void onProgressUpdate(Void... values) {
//		super.onProgressUpdate(values);
//	}
	
	@Override
	protected void onPostExecute(Void value){
		progressDialog.dismiss();
	}

}
