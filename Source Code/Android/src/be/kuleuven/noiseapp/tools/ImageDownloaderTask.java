package be.kuleuven.noiseapp.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

	Activity mActivity;
	private String fileName;
	private boolean isInMemory = false;
	
	public ImageDownloaderTask(Activity activity, String fileName) {
		this.mActivity = activity;
		this.fileName = fileName;
	  }
	
	@Override
	protected void onPreExecute(){
		File file = mActivity.getApplicationContext().getFileStreamPath(fileName);
		if(file.exists())
			isInMemory = true;
	}
	
    @Override
	protected Bitmap doInBackground(String... urls) {
    	if(!isInMemory){
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
    	}
    	return null;
    }

    @Override
	protected void onPostExecute(Bitmap result) {
    	if(!isInMemory){
	    	FileOutputStream fos;
			try {
				fos = mActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
				result.compress(CompressFormat.PNG, 90, fos); //quality ignored, because of png format
	        	fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		mActivity = null;
    }
}