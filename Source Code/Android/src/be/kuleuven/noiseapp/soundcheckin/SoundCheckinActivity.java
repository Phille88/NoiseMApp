package be.kuleuven.noiseapp.soundcheckin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import be.kuleuven.noiseapp.recording.RecordActivity;

import be.kuleuven.noiseapp.R;
public class SoundCheckinActivity extends RecordActivity implements LocationListener{
	
	// fields for Android location
	protected LocationManager locationManager;
	private static String provider;
	protected Location currentLocation;
	private static Location LEUVEN_CENTER;
	ArrayList<PlaceItem> nearbyPlaces = new ArrayList<PlaceItem>();
	PlaceItemAdapter adapter;
	ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

		LEUVEN_CENTER = new Location("GPS");
	    LEUVEN_CENTER.setLatitude(50.877571);
		LEUVEN_CENTER.setLongitude(4.704328);
        
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabledGPS) {
            Toast.makeText(this, "Please, enable GPS to use this application.", Toast.LENGTH_LONG).show();
        }
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        provider = LocationManager.GPS_PROVIDER;
//        Location location = locationManager.getLastKnownLocation(provider);
//        zoomTo(location);
        currentLocation = locationManager.getLastKnownLocation(provider);
        if(currentLocation == null)
        	currentLocation = LEUVEN_CENTER;
        
		listPlaces();
	}
	
	@Override
	protected void setView(){
		setContentView(R.layout.activity_sound_checkin);
	}

	private void listPlaces() {
		listView = (ListView) findViewById(R.id.list_places);
		performSearch();
		
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		adapter = new PlaceItemAdapter(this,android.R.id.text1, nearbyPlaces);
		listView.setAdapter(adapter); 
		listView.setOnItemClickListener(recordItemButtonListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.sound_checkin, menu);
		return true;
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	protected void addListenerToRecordButton(){
	}

	protected OnItemClickListener recordItemButtonListener(){
		final SoundCheckinActivity r = this;
		return new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			PlaceItem placeItem = (PlaceItem) listView.getAdapter().getItem(position);
			Location placeItemLocation = new Location("place");
			placeItemLocation.setLatitude(placeItem.getLatLng().latitude);
			placeItemLocation.setLongitude(placeItem.getLatLng().longitude);
			SoundCheckinRecordTask rt = new SoundCheckinRecordTask(v, r, placeItem.getName());
			rt.execute(placeItemLocation);
		}
	};
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void setActionBarTitle() {
		getActionBar().setTitle(R.string.txt_sound_checkin_name);
	}

	@Override
	protected boolean popupNeeded() {
		return false;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.txt_sound_checkin_name;
	}

	@Override
	protected int getPopupExplanation() {
		return 0;
	}

	@Override
	protected String getPopupDontShowAgainName() {
		return null;
	}

	@Override
	protected OnClickListener recordButtonListener() {
		return null;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(isBetterLocation(location,currentLocation)){
			setProviderFixed(true);
			currentLocation = location;
			if(timeout()){
				performSearch();
				adapter.notifyDataSetChanged();
			}
		}
	}

	/** PLACES **/
	private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/nearbysearch/";
	private static final String REQUEST_OUTPUT = "json";
	//private static final String REQUEST_LOCATION = "37.787930,-122.4074990";
	private static final String REQUEST_RADIUS = "100";
	private static final String REQUEST_SENSOR = "false";
	//private static final String API_KEY = "AIzaSyAqAKvwtX7uTgdDqmlOmDN3jJk86ITzSCo";
	private static final String API_KEY = "AIzaSyDepfs8lsYdSvcLqBlUTBGrEcIOZ8A93Yw"; //BROWSER API KEY

	public void performSearch() {
		nearbyPlaces.clear();
		try {
			parseJSON(new PlaceSearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new URI(PLACES_SEARCH_URL + REQUEST_OUTPUT + "?location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&radius="+ REQUEST_RADIUS + "&sensor=" + REQUEST_SENSOR + "&key=" + API_KEY)).get());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void parseJSON(String toParse) throws JSONException{
		JSONObject jsonObject = new JSONObject(toParse);
		JSONArray resultsArray = jsonObject.getJSONArray("results");
		for(int i = 0; i < resultsArray.length();i++){
			nearbyPlaces.add(new PlaceItem(resultsArray.getJSONObject(i).getString("name").toString(),
			null,
			resultsArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
			resultsArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")));
		}
	}
}