package be.kuleuven.noiseapp.noisehunt;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class SaveNoiseHuntRecordingTask extends AsyncTask<NoiseRecording, Void, Void> {
	private static final String url_create_noisehuntrecording = Constants.BASE_URL_MYSQL + "create_noisehuntrecording.php";
	private JSONParser jsonParser = new JSONParser();
	private int noiseHuntID;
	
	public SaveNoiseHuntRecordingTask(int noiseHuntID){
		this.noiseHuntID = noiseHuntID;
	}

	@Override
	protected Void doInBackground(NoiseRecording... args) {
		NoiseRecording nr = args[0];
		 
		// Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", Long.toString(nr.getUserID())));
        params.add(new BasicNameValuePair("noiseHuntID", Long.toString(noiseHuntID)));
        params.add(new BasicNameValuePair("noiseRecordingID", Long.toString(nr.getID())));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_create_noisehuntrecording,
                "POST", params);
        // check log cat fro response
        Log.d("SaveNoiseHuntRecording Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
	}
}
