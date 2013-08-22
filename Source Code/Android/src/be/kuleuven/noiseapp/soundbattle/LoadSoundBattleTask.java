package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.ImageDownloaderTask;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

public class LoadSoundBattleTask extends AsyncTask<Long, Void, JSONArray[]> {
	
	private static final String url_get_sound_battle_state = Constants.BASE_URL_MYSQL + "get_soundbattlestate.php";
	private long userID;
	private long soundBattleID;
	private JSONParser jsonParser = new JSONParser();
	private SoundBattleActivity sba;

	public LoadSoundBattleTask(SoundBattleActivity sba){
		this.sba = sba;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(sba.getApplicationContext());
		this.userID = sp.getLong(MemoryFileNames.USERID, 0L);
	}

	@Override
	protected JSONArray[] doInBackground(Long... soundBattleID) {
		this.soundBattleID = soundBattleID[0];
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(userID)));
        params.add(new BasicNameValuePair(MySQLTags.SOUNDBATTLEID, Long.toString(this.soundBattleID)));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));


        JSONObject json = jsonParser .makeHttpRequest(url_get_sound_battle_state , "POST", params);

        // check log cat fro response
        Log.d("LoadSoundBattle Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
                return new JSONArray[]{json.getJSONArray(JSONTags.SOUNDBATTLELOCATIONS),json.getJSONArray(JSONTags.OPPONENT_DETAILS)};
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(JSONArray[] jsa){
		Intent intent = new Intent(sba.getApplicationContext(),SoundBattleRecordActivity.class);
		
		//sound battle locations
		if(jsa[0].length() == 3){
			int[] SBLIDs = new int[jsa[0].length()];
			double[] SBLlongitudes = new double[jsa[0].length()];
			double[] SBLlatitudes = new double[jsa[0].length()];
			boolean[] SBLrecorded = new boolean[jsa[0].length()];
		
			for(int i = 0; i<jsa[0].length(); i++){
				JSONObject index;
				try {
					index = jsa[0].getJSONObject(i);
					SBLIDs[i] = index.getInt(JSONTags.SOUNDBATTLELOCATIONID);
					SBLlongitudes[i] = index.getDouble(JSONTags.LONGITUDE);
					SBLlatitudes[i] = index.getDouble(JSONTags.LATITUDE);
					SBLrecorded[i] = index.getBoolean(JSONTags.RECORDED);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			intent.putExtra("SBLIDs", SBLIDs);
			intent.putExtra("SBLlongitudes", SBLlongitudes);
			intent.putExtra("SBLlatitudes", SBLlatitudes);
			intent.putExtra("SBLrecorded", SBLrecorded);
		}
		
		//opponentdetails
		UserDetails opponentUserDetails = null;
		try {
			long opponentID = jsa[1].getJSONObject(0).getLong(JSONTags.USERID);
			String fName = jsa[1].getJSONObject(0).getString(JSONTags.FIRSTNAME);
			String lName = jsa[1].getJSONObject(0).getString(JSONTags.LASTNAME);
			String pictureURL = jsa[1].getJSONObject(0).getString(JSONTags.PICTUREURL);
			opponentUserDetails = new UserDetails(opponentID, null, fName, lName, null, 0L, 0L, new ArrayList<Integer>(), null, pictureURL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		intent.putExtra(MemoryFileNames.SOUNDBATTLEID, soundBattleID);
		intent.putExtra(MemoryFileNames.OPPONENTDETAILS, ObjectSerializer.serialize(opponentUserDetails));
		try {
			new ImageDownloaderTask(sba, MemoryFileNames.PROFILE_PICTURE + "_" + opponentUserDetails.getUserID()).execute(opponentUserDetails.getPictureURL()).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} // + "?size=" + Constants.SIZE_OPPONENT_PRROFILE_PICTURE);
		
		sba.startActivity(intent);
	}
}