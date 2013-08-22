package be.kuleuven.noiseapp.noisehunt;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class SaveFinishedNoiseHuntTask extends AsyncTask<Integer, Void, Void> {

	private JSONParser jsonParser = new JSONParser();
	private String url_update_noisehuntstate = Constants.BASE_URL_MYSQL + "update_noisehuntstate.php";
	private SharedPreferences sp;
	
	public SaveFinishedNoiseHuntTask(Activity a){
		sp = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
	}

	@Override
	protected Void doInBackground(Integer... noiseHuntID) {
		//save remotely
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(sp.getLong(MemoryFileNames.USERID, 0L))));
	    params.add(new BasicNameValuePair(MySQLTags.NOISEHUNT_ID, Integer.toString(noiseHuntID[0])));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));
	
        JSONObject json = jsonParser .makeHttpRequest(url_update_noisehuntstate, "POST", params);

        Log.d("SaveFinishedNoiseHunt Response", json.toString());
        
        //save locally
		Editor edit = sp.edit();
		edit.putInt("lastFinishedNoiseHunt", noiseHuntID[0]);
		edit.commit();

        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	return null;
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
}
