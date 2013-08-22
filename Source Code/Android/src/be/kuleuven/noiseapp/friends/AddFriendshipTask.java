package be.kuleuven.noiseapp.friends;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class AddFriendshipTask extends AsyncTask<Long, Void, Long> {
	
	private Activity rActivity;
	private SharedPreferences sp;
	private static final String url_create_friendship = Constants.BASE_URL_MYSQL + "create_friendship.php";
	private JSONParser jsonParser = new JSONParser();

	public AddFriendshipTask(Activity rActivity){
		this.rActivity = rActivity;
		this.sp = PreferenceManager.getDefaultSharedPreferences(rActivity.getApplicationContext());
	}

	@Override
	protected Long doInBackground(Long... friendID) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(MySQLTags.USERID1, Long.toString(sp.getLong(MemoryFileNames.USERID, 0L))));
	    params.add(new BasicNameValuePair(MySQLTags.USERID2, Long.toString(friendID[0])));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));
	    
        JSONObject json = jsonParser.makeHttpRequest(url_create_friendship, "POST", params);
        
        Log.d("AddFriendshipResponse", json.toString());

        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	return friendID[0];
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	@Override
	protected void onPostExecute(Long friendID){
		new UpdateFriendsListLocalTask(rActivity).execute();
	}
}
