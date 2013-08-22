package be.kuleuven.noiseapp.recording;

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
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class SaveNoiseRecordingRemoteTask  extends AsyncTask<NoiseRecording, Void, Void>{
	
	private JSONParser jsonParser = new JSONParser();
	private static String url_create_noiserecording = Constants.BASE_URL_MYSQL + "create_noiserecording.php";

    /**
     * Creating profile
     * */
    @Override
	protected Void doInBackground(NoiseRecording... args) {
    	NoiseRecording nr = args[0];
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(nr.getUserID())));
        params.add(new BasicNameValuePair(MySQLTags.LATITUDE, Double.toString(nr.getLatitude())));
        params.add(new BasicNameValuePair(MySQLTags.LONGITUDE, Double.toString(nr.getLongitude())));
        params.add(new BasicNameValuePair(MySQLTags.DB,Double.toString(nr.getDB())));
        params.add(new BasicNameValuePair(MySQLTags.ACCURACY,Double.toString(nr.getAccuracy())));
        params.add(new BasicNameValuePair(MySQLTags.QUALITY,Double.toString(nr.getQuality())));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        JSONObject json = jsonParser.makeHttpRequest(url_create_noiserecording, "POST", params);

        Log.d("SaveNoiseRecordingRemote Response", json.toString());

        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	int nrID = json.getInt(JSONTags.NOISERECORDING_ID);
            	nr.setId(nrID);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
