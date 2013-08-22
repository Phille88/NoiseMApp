package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class SaveSoundBattleLocations extends AsyncTask<ArrayList<SoundBattleLocation>, Void, Void> {

	private static final String url_create_soundbattlelocation = Constants.BASE_URL_MYSQL + "create_soundbattlelocation.php";
	private JSONParser jsonParser = new JSONParser();
	private long soundBattleID;
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SOUNDBATTLELOCATION_ID = "soundBattleLocationID";
	
	public SaveSoundBattleLocations(long soundBattleID){
		super();
		this.soundBattleID = soundBattleID;
	}

	@Override
	protected Void doInBackground(ArrayList<SoundBattleLocation>... args) {
		ArrayList<SoundBattleLocation> sbls = args[0];
		
		// Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("soundBattleID", Long.toString(soundBattleID)));
        for(int i = 0 ; i < sbls.size(); i++){
        	params.add(new BasicNameValuePair("longitude" + (i+1), Double.toString(sbls.get(i).getLatLng().longitude)));
        	params.add(new BasicNameValuePair("latitude" + (i+1), Double.toString(sbls.get(i).getLatLng().latitude)));
        }
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));
        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_soundbattlelocation,
                "POST", params);

        // check log cat fro response
        Log.d("CreateSoundBattleLocation Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
            	long lastIndex = json.getLong(TAG_SOUNDBATTLELOCATION_ID);
            	for(int i = 0; i<3;i++){
            		sbls.get(i).setSoundBattleLocationID(lastIndex + i);
            	}
            } else {
            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
}
