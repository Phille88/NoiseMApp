package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.soundbattle.SoundBattleItemAdapter.RowType;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class GetAllSoundBattlesTask extends AsyncTask<Void, Void, JSONObject> {
	
	private static final String url_get_sound_battles = Constants.BASE_URL_MYSQL + "get_all_my_soundbattles.php";
	private long userID;
	private JSONParser jsonParser = new JSONParser();
	private SoundBattleActivity sba;

	public GetAllSoundBattlesTask(SoundBattleActivity sba){
		this.sba = sba;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(sba.getApplicationContext());
		this.userID = sp.getLong(MemoryFileNames.USERID, 0L);
	}

	@Override
	protected JSONObject doInBackground(Void... arg0) {
		  // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(userID)));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser .makeHttpRequest(url_get_sound_battles ,
                "POST", params);

        // check log cat fro response
        Log.d("GetRandomSoundBattle Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
                return json.getJSONObject(JSONTags.SOUNDBATTLES);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject jsa){
		ArrayList<iSoundBattleListItem> soundBattleItems = new ArrayList<iSoundBattleListItem>();
		try {
			JSONArray openSoundBattles = jsa.getJSONArray(JSONTags.OPEN_SOUNDBATTLES);
			for(int i = 0; i < openSoundBattles.length() ; i++){
				if(i == 0)
					soundBattleItems.add(new SoundBattleItemHeader("Finish these battles:"));
				long soundBattleID = openSoundBattles.getJSONObject(i).getLong(JSONTags.SOUNDBATTLE_ID);
				String oppName = openSoundBattles.getJSONObject(i).getString(JSONTags.OPPONENT_NAME);
				int amount = openSoundBattles.getJSONObject(i).getInt(JSONTags.AMOUNTNRS);
				soundBattleItems.add(new SoundBattleItem(soundBattleID, oppName, amount, RowType.LIST_OPEN_ITEM.ordinal()));
			}
			//sba.listOpenBattles();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			JSONArray pendingSoundBattles = jsa.getJSONArray(JSONTags.PENDING_SOUNDBATTLES);
			for(int i = 0; i < pendingSoundBattles.length() ; i++){
				if(i == 0)
					soundBattleItems.add(new SoundBattleItemHeader("Waiting for opponent:"));
				long soundBattleID = pendingSoundBattles.getJSONObject(i).getLong(JSONTags.SOUNDBATTLE_ID);
				String oppName = pendingSoundBattles.getJSONObject(i).getString(JSONTags.OPPONENT_NAME);
				int amount = pendingSoundBattles.getJSONObject(i).getInt(JSONTags.AMOUNTNRS);
				soundBattleItems.add(new SoundBattleItem(soundBattleID, oppName, amount, RowType.LIST_PENDING_ITEM.ordinal()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			JSONArray finishedSoundBattles = jsa.getJSONArray(JSONTags.FINISHED_SOUNDBATTLES);
			for(int i = 0; i < finishedSoundBattles.length() ; i++){
				if(i == 0)
					soundBattleItems.add(new SoundBattleItemHeader("Done battles:"));
				long soundBattleID = finishedSoundBattles.getJSONObject(i).getLong(JSONTags.SOUNDBATTLE_ID);
				String oppName = finishedSoundBattles.getJSONObject(i).getString(JSONTags.OPPONENT_NAME);
				int amount = finishedSoundBattles.getJSONObject(i).getInt(JSONTags.AMOUNTNRS);
				soundBattleItems.add(new SoundBattleItem(soundBattleID, oppName, amount, RowType.LIST_FINISHED_ITEM.ordinal()));
			}
			//sba.listFinishedBattles();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		sba.setSoundBattles(soundBattleItems);
		sba.listBattles();
	}

}
