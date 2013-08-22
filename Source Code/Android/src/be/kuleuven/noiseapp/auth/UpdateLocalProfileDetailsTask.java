package be.kuleuven.noiseapp.auth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.soundcheckin.SoundCheckinDetails;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.ImageDownloaderTask;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

public class UpdateLocalProfileDetailsTask extends AsyncTask<Void, Void, JSONObject> {

	private static final String url_get_userprofile_details = Constants.BASE_URL_MYSQL + "get_userprofile_details.php";
	private JSONParser jsonParser = new JSONParser();
	private SharedPreferences sp;
	private long userID;
	private Activity rActivity;
	
	public UpdateLocalProfileDetailsTask(Activity rActivity){
		sp = PreferenceManager.getDefaultSharedPreferences(rActivity);
		userID = sp.getLong(MemoryFileNames.USERID, 0L);
		this.rActivity = rActivity;
	}
	
	@Override
	protected JSONObject doInBackground(Void... arg0) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(userID)));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));
	    
	    JSONObject json = jsonParser.makeHttpRequest(url_get_userprofile_details, "POST", params);
        
        Log.d("UpdateLocalProfileDetailsResponse", json.toString());
        
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
                return json.getJSONObject(JSONTags.USERPROFILE);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject userDetailsJSON){
		long userID = 0L;
		UserDetails userDetails = null;
		try {
			userID = userDetailsJSON.getLong(JSONTags.USERID);
			BigInteger googleID = new BigInteger(userDetailsJSON.getString(JSONTags.GOOGLEID));
			String fName = userDetailsJSON.getString(JSONTags.FIRSTNAME);
			String lName = userDetailsJSON.getString(JSONTags.LASTNAME);
			String email = userDetailsJSON.getString(JSONTags.EMAIL);
			long totalPoints = userDetailsJSON.getLong(JSONTags.TOTALPOINTS);
			long soundBattlesWon = userDetailsJSON.getLong(JSONTags.SOUNDBATTLESWON);
			JSONArray badges = userDetailsJSON.getJSONArray(JSONTags.BADGES);
			ArrayList<Integer> badgeIDs = new ArrayList<Integer>();
			for(int i = 0; i < badges.length(); i++){
				badgeIDs.add(badges.getInt(i));
			}
			JSONObject lastSoundCheckin = userDetailsJSON.getJSONObject(JSONTags.LASTSOUNDCHECKIN);
			String lastSoundCheckinName = lastSoundCheckin.getString(JSONTags.PLACENAME);
			double lastSoundCheckinDB = lastSoundCheckin.getDouble(JSONTags.DB);
			SoundCheckinDetails lastSoundCheckinDetails = new SoundCheckinDetails(lastSoundCheckinName, lastSoundCheckinDB);
			if(lastSoundCheckinName.equals("null") || lastSoundCheckinDB == 0)
				lastSoundCheckinDetails = null;
			String pictureURL = userDetailsJSON.getString(JSONTags.PICTUREURL);
			userDetails = new UserDetails(userID,googleID, fName, lName, email, totalPoints, soundBattlesWon, badgeIDs, lastSoundCheckinDetails, pictureURL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Editor edit = sp.edit();
		edit.putLong(MemoryFileNames.USERID, userID);
		edit.putString(MemoryFileNames.USERDETAILS, ObjectSerializer.serialize(userDetails));
		edit.commit();

		new ImageDownloaderTask(rActivity, MemoryFileNames.PROFILE_PICTURE).execute(userDetails.getPictureURL());
	}

}
