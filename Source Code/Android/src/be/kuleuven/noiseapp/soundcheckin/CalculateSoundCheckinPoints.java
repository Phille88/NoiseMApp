package be.kuleuven.noiseapp.soundcheckin;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import be.kuleuven.noiseapp.points.Badge;
import be.kuleuven.noiseapp.points.Point;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.recording.NoiseRecording;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class CalculateSoundCheckinPoints extends AsyncTask<NoiseRecording, Void, RecordingPoints>{
	
	private JSONParser jsonParser;
	private String placeName;
	private static String url_get_sound_checkin_points = Constants.BASE_URL_MYSQL + "get_soundcheckinpoints.php";

	public CalculateSoundCheckinPoints(String placeName){
		this.placeName = placeName;
	}

	@Override
	protected RecordingPoints doInBackground(NoiseRecording... nrs) {
    	jsonParser = new JSONParser();
		NoiseRecording nr = nrs[0];
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID, Long.toString(nr.getUserID())));
        params.add(new BasicNameValuePair(MySQLTags.LATITUDE, Double.toString(nr.getLatitude())));
        params.add(new BasicNameValuePair(MySQLTags.LONGITUDE, Double.toString(nr.getLongitude())));
        params.add(new BasicNameValuePair(MySQLTags.DB,Double.toString(nr.getDB())));
        params.add(new BasicNameValuePair(MySQLTags.ACCURACY,Double.toString(nr.getAccuracy())));
        params.add(new BasicNameValuePair(MySQLTags.QUALITY,Double.toString(nr.getQuality())));
        params.add(new BasicNameValuePair(MySQLTags.PLACENAME,placeName));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        JSONObject json = jsonParser.makeHttpRequest(url_get_sound_checkin_points, "POST", params);
        //TODO mayorships --> Not Implemented

        Log.d("CalculateSoundCheckinPoints Response", json.toString());

        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	JSONArray points = json.getJSONArray(JSONTags.POINTS);
            	JSONArray badges = json.getJSONArray(JSONTags.BADGES);
            	ArrayList<Point> pointsList = new ArrayList<Point>();
            	ArrayList<Badge> badgesList = new ArrayList<Badge>();
            	for (int i = 0; i < points.length(); i++){
            		JSONObject point = points.getJSONObject(i);
            		pointsList.add(new Point(point.getString(JSONTags.POINT_DESC), point.getInt(JSONTags.POINT_AMOUNT)));
            	}
            	for (int i = 0; i < badges.length(); i++){
            		JSONObject badge = badges.getJSONObject(i);
            		badgesList.add(new Badge(badge.getInt(JSONTags.BADGEID), badge.getString(JSONTags.DESCRIPTION), badge.getInt(JSONTags.AMOUNT)));
            	}
            	return new RecordingPoints(pointsList, badgesList);
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
}
