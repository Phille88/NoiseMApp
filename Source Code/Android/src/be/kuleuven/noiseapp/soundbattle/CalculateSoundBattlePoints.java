package be.kuleuven.noiseapp.soundbattle;

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
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.JSONParser;
import be.kuleuven.noiseapp.tools.JSONTags;
import be.kuleuven.noiseapp.tools.MySQLTags;

public class CalculateSoundBattlePoints extends AsyncTask<Long, Void, SoundBattleRecordingPoints>{
	
	private JSONParser jsonParser = new JSONParser();
	private static String url_get_sound_battle_points = Constants.BASE_URL_MYSQL + "get_soundbattlepoints.php";

	@Override
	protected SoundBattleRecordingPoints doInBackground(Long... ids) {
		long soundBattleID = ids[0];
		long userID = ids[1];
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MySQLTags.USERID,Long.toString(userID)));
        params.add(new BasicNameValuePair(MySQLTags.SOUNDBATTLEID, Long.toString(soundBattleID)));
        params.add(new BasicNameValuePair(MySQLTags.REQUESTKEY, Constants.REQUESTKEY));

        JSONObject json = jsonParser.makeHttpRequest(url_get_sound_battle_points, "POST", params);

        Log.d("CalculateSoundBattlePoints Response", json.toString());

        try {
            int success = json.getInt(JSONTags.SUCCESS);

            if (success == 1) {
            	JSONArray userPoints = json.getJSONArray(JSONTags.POINTS);
            	JSONArray userBadges = json.getJSONArray(JSONTags.BADGES);
            	ArrayList<Point> userPointsList = new ArrayList<Point>();
            	ArrayList<Badge> userBadgesList = new ArrayList<Badge>();
            	for (int i = 0; i < userPoints.length(); i++){
            		JSONObject point = userPoints.getJSONObject(i);
            		userPointsList.add(new Point(JSONTags.QUALITY, point.getInt(JSONTags.QUALITY)));
            		userPointsList.add(new Point(JSONTags.ACCURACY, point.getInt(JSONTags.ACCURACY)));
            		userPointsList.add(new Point(JSONTags.SPEED, point.getInt(JSONTags.SPEED)));
            	}
            	for(int i = 0; i < userBadges.length(); i++){
            		JSONObject badge = userBadges.getJSONObject(i);
            		userBadgesList.add(new Badge(badge.getInt(JSONTags.BADGEID), badge.getString(JSONTags.DESCRIPTION), badge.getInt(JSONTags.AMOUNT)));
            	}
            	RecordingPoints userRecordingPoints = new RecordingPoints(userPointsList, userBadgesList);
            	
            	JSONArray opponentPoints = json.getJSONArray(JSONTags.OPPONENT_POINTS);
            	ArrayList<Point> opponentPointsList = new ArrayList<Point>();
            	for (int i = 0; i < opponentPoints.length(); i++){
            		JSONObject opponentPoint = opponentPoints.getJSONObject(i);
            		opponentPointsList.add(new Point(JSONTags.QUALITY, opponentPoint.getInt(JSONTags.QUALITY)));
            		opponentPointsList.add(new Point(JSONTags.ACCURACY, opponentPoint.getInt(JSONTags.ACCURACY)));
            		opponentPointsList.add(new Point(JSONTags.SPEED, opponentPoint.getInt(JSONTags.SPEED)));
            	}
            	RecordingPoints opponentRecordingPoints = new RecordingPoints(opponentPointsList, new ArrayList<Badge>());
            	
            	return new SoundBattleRecordingPoints(userRecordingPoints, opponentRecordingPoints);
            } 
            else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
	}
}
