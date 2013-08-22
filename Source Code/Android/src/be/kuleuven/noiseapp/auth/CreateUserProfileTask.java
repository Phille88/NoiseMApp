package be.kuleuven.noiseapp.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.kuleuven.noiseapp.MainActivity;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.MySQLTags;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

public class CreateUserProfileTask extends AsyncTask<UserDetails, Void, Long> {
	private JSONParser jsonParser = new JSONParser();
	private MainActivity sourceActivity;
	private static String url_create_userprofile = Constants.BASE_URL_MYSQL + "create_userprofile.php";
	
	public CreateUserProfileTask(MainActivity sourceActivity){
		this.sourceActivity = sourceActivity;
	}

    /**
     * Creating profile
     * */
	@Override
    protected Long doInBackground(UserDetails... uds) {
    	UserDetails userDetails = uds[0];
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.GOOGLEID, userDetails.getGoogleID().toString()));
        params.add(new BasicNameValuePair(MySQLTags.FIRSTNAME, userDetails.getFName()));
        params.add(new BasicNameValuePair(MySQLTags.LASTNAME, userDetails.getLName()));
        params.add(new BasicNameValuePair(MySQLTags.EMAIL, userDetails.getEmail()));
        params.add(new BasicNameValuePair(MySQLTags.PICTUREURL, userDetails.getPictureURL()));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        JSONObject json = jsonParser.makeHttpRequest(url_create_userprofile, "POST", params);

        Log.d("CreateUserProfileTask Response", json.toString());

        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	return json.getLong(JSONTags.USERID);
            } 
            else {
            	long userID = json.getLong(JSONTags.USERID);
            	if(userID != 0L)
            		return json.getLong(JSONTags.USERID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    @Override
    protected void onPostExecute(Long result) {
    	if(result != 0){
    		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(sourceActivity.getApplicationContext());
    		UserDetails userDetails = (UserDetails) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.USERDETAILS, null));
    		userDetails.setUserID(result);
    		Editor edit = sp.edit();
    		edit.putString(MemoryFileNames.USERDETAILS, ObjectSerializer.serialize(userDetails));
    		edit.putLong(MemoryFileNames.USERID, result);
    		edit.commit();
    	
			new UpdateLocalProfileDetailsTask(sourceActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
			sourceActivity.getNoiseHuntState();
			sourceActivity.getFriendsList();
			sourceActivity.registerToGCMTask();
    	}
    }
}