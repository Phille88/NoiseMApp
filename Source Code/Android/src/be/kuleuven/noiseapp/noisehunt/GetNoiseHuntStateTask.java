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

public class GetNoiseHuntStateTask extends AsyncTask<Void, Void, Integer> {
	
	private JSONParser jsonParser = new JSONParser();
	private SharedPreferences sp;
	private String url_get_noise_hunt_state = Constants.BASE_URL_MYSQL + "get_noisehuntstate.php";
	
	public GetNoiseHuntStateTask(Activity a){
		sp = PreferenceManager.getDefaultSharedPreferences(a);
	}

	@Override
	protected Integer doInBackground(Void... args) {
		while(sp.getLong(MemoryFileNames.USERID, 0L) == 0L){
			//wait
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(sp.getLong(MemoryFileNames.USERID, 0L))));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        JSONObject json = jsonParser.makeHttpRequest(url_get_noise_hunt_state, "POST", params);

        // check log cat fro response
        Log.d("GetNoiseHuntState Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
                return json.getInt(JSONTags.NOISEHUNT_ID);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(Integer noiseHuntID){
		Editor edit = sp.edit();
		edit.putInt(MemoryFileNames.LAST_FINISHED_NOISEHUNT, noiseHuntID);
		edit.commit();
	}
}
