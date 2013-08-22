package be.kuleuven.noiseapp.friends;

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
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.soundcheckin.SoundCheckinDetails;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.ImageDownloaderTask;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

public class UpdateFriendsListLocalTask extends AsyncTask<Void, Void, ArrayList<UserDetails>> {
	
	private SharedPreferences sp;
	private static final String url_get_friend_details = Constants.BASE_URL_MYSQL + "get_friends_details.php";
	private JSONParser jsonParser = new JSONParser();
	private Activity rActivity;

	public UpdateFriendsListLocalTask(Activity rActivity){
		this.sp = PreferenceManager.getDefaultSharedPreferences(rActivity.getApplicationContext());
		this.rActivity = rActivity;
	}

	@Override
	protected ArrayList<UserDetails> doInBackground(Void... arg0) {
		while(sp.getLong(MemoryFileNames.USERID, 0L) == 0L){
			//wait
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(sp.getLong(MemoryFileNames.USERID, 0L))));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));
	    
        JSONObject json = jsonParser.makeHttpRequest(url_get_friend_details, "POST", params);
        
        Log.d("GetFriendsListResponse", json.toString());
        
        ArrayList<UserDetails> friendsList = new ArrayList<UserDetails>();
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
                JSONArray friends = json.getJSONArray(JSONTags.FRIENDS);
                for(int i = 0; i < friends.length(); i++){
                	JSONObject friend = friends.getJSONObject(i);
        			JSONArray badges = friend.getJSONArray(JSONTags.BADGES);
        			ArrayList<Integer> badgeIDs = new ArrayList<Integer>();
        			for(int j = 0; j < badges.length(); j++){
        				badgeIDs.add(badges.getInt(j));
        			}
        			JSONObject lastSoundCheckin = friend.getJSONObject(JSONTags.LASTSOUNDCHECKIN);
        			String lastSoundCheckinName = lastSoundCheckin.getString(JSONTags.PLACENAME);
        			double lastSoundCheckinDB = lastSoundCheckin.getDouble(JSONTags.DB);
        			SoundCheckinDetails lastSoundCheckinDetails = new SoundCheckinDetails(lastSoundCheckinName, lastSoundCheckinDB);
        			if(lastSoundCheckinName.equals("null") || lastSoundCheckinDB == 0)
        				lastSoundCheckinDetails = null;
                	friendsList.add(new UserDetails(friend.getLong(JSONTags.USERID), new BigInteger(friend.getString(JSONTags.GOOGLEID)), friend.getString(JSONTags.FIRSTNAME), friend.getString(JSONTags.LASTNAME), null, friend.getLong(JSONTags.TOTALPOINTS), friend.getLong(JSONTags.SOUNDBATTLESWON), badgeIDs, lastSoundCheckinDetails, friend.getString(JSONTags.PICTUREURL)));
                }
                return friendsList;
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<UserDetails>();
	}
	
	@Override
	protected void onPostExecute(ArrayList<UserDetails> friends){
		Editor editor = sp.edit();
		if(friends != null && !friends.isEmpty()){
			editor.putString(MemoryFileNames.FRIENDLIST, ObjectSerializer.serialize(friends));
			for(UserDetails friend : friends)
				new ImageDownloaderTask(rActivity, MemoryFileNames.PROFILE_PICTURE + "_" + friend.getUserID()).execute(friend.getPictureURL());
		} else {
			editor.putString(MemoryFileNames.FRIENDLIST, ObjectSerializer.serialize(new ArrayList<UserDetails>()));
		}
		editor.commit();
	}

}
