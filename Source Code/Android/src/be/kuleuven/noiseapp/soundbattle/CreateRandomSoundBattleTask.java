package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class CreateRandomSoundBattleTask extends AsyncTask<Void, Void, Long> {
	private SoundBattleActivity sba;
	private long userID;
	private JSONParser jsonParser = new JSONParser();
	private static String url_get_random_player = Constants.BASE_URL_MYSQL + "get_random_player.php";
		
	public CreateRandomSoundBattleTask(SoundBattleActivity sba){
		this.sba = sba;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(sba.getApplicationContext());
		this.userID = sp.getLong(MemoryFileNames.USERID, 0L);
	}

    /**
     * Creating profile
     * */
    @Override
	protected Long doInBackground(Void... args) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", Long.toString(userID)));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_get_random_player, "POST", params);

        // check log cat for response
        Log.d("GetRandomSoundBattle Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
                return json.getLong(JSONTags.OPPONENT_ID);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
	protected void onPostExecute(Long opponentID){
    	new CreateSoundBattleTask(sba).execute(userID, opponentID);
    }
}
