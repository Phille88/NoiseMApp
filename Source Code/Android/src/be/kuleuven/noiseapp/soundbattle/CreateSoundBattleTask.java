package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.ImageDownloaderTask;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

public class CreateSoundBattleTask extends AsyncTask<Long, Void, JSONObject> {

	private SoundBattleActivity sba;
	private JSONParser jsonParser = new JSONParser();
	private static final String url_create_soundbattle = Constants.BASE_URL_MYSQL + "create_soundbattle.php";
	
	public CreateSoundBattleTask(SoundBattleActivity sba){
		this.sba = sba;
	}

	@Override
	protected JSONObject doInBackground(Long... args) {
		long userID = args[0];
		long opponentID = args[1];
		
		// Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID1, Long.toString(userID)));
        params.add(new BasicNameValuePair(MySQLTags.USERID2, Long.toString(opponentID)));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_soundbattle,
                "POST", params);

        // check log cat fro response
        Log.d("CreateSoundBattle Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	return json;
            } else {
            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject jso){
		UserDetails opponentUserDetails = null;
		Long soundBattleID = 0L;
		try {
			long opponentID = jso.getJSONArray(JSONTags.OPPONENT_DETAILS).getJSONObject(0).getLong("opponentID");
			String fName = jso.getJSONArray(JSONTags.OPPONENT_DETAILS).getJSONObject(0).getString("firstName");
			String lName = jso.getJSONArray(JSONTags.OPPONENT_DETAILS).getJSONObject(0).getString("lastName");
			String pictureURL = jso.getJSONArray(JSONTags.OPPONENT_DETAILS).getJSONObject(0).getString("pictureURL");
			opponentUserDetails = new UserDetails(opponentID, null, fName, lName, null, 0L, 0L, new ArrayList<Integer>(), null, pictureURL);
			soundBattleID = jso.getLong(JSONTags.SOUNDBATTLE_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent i = new Intent(sba.getApplicationContext(),SoundBattleRecordActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("soundBattleID", soundBattleID);
		i.putExtra(MemoryFileNames.OPPONENTDETAILS, ObjectSerializer.serialize(opponentUserDetails));
		try {
			new ImageDownloaderTask(sba, MemoryFileNames.PROFILE_PICTURE + "_" + opponentUserDetails.getUserID()).execute(opponentUserDetails.getPictureURL()).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}// + "?size=" + Constants.SIZE_OPPONENT_PRROFILE_PICTURE);
		sba.startActivity(i);
	}
}
