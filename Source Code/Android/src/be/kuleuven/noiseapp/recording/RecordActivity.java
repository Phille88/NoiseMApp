package be.kuleuven.noiseapp.recording;

import java.util.Date;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import be.kuleuven.noiseapp.MainActivity;
import be.kuleuven.noiseapp.tools.Constants;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import be.kuleuven.noiseapp.R;
public abstract class RecordActivity extends android.support.v4.app.FragmentActivity implements LocationListener, SensorEventListener{

	// fields for Google Maps API
	protected GoogleMap mMap;
	private UiSettings mUiSettings;    
	private float currentZoomLevel = 18;
	private static Location LEUVEN_CENTER;

	// fields for Android accelerometer
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private float mAccel;
	private float mAccelCurrent;
	private float mAccelLast;
	private int badQualityMovements = 0;
	private int goodQualityMovements = 0;
	private int superQualityMovements = 0;
	private int allMovements = 0;

	// fields for Android location
	protected LocationManager locationManager;
	private static String provider;
	private boolean providerFixed;
	private Location currentLocation;
	
	private Date lastTouchTime = new Date(System.currentTimeMillis()-15000);
	
	protected Button btn_record;
	
	//fields for progress bar
	private ProgressDialog progressBar;
	protected int progressBarStatus = 0;
	protected Handler progressBarHandler = new Handler();
	
	private static final int TWO_SECONDS = 1000 * 2;
	
	//fields for database
	//protected NoiseRecordingsDataSource datasource;
	private boolean gpsFixedMessageShown = false;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView();
		LEUVEN_CENTER = new Location("GPS");
	    LEUVEN_CENTER.setLatitude(50.877571);
		LEUVEN_CENTER.setLongitude(4.704328);
		
		setupActionBar();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		if(popupNeeded() && !sp.getBoolean(getPopupDontShowAgainName(), false))
			showPopup();
		
		addListenerToRecordButton();	
		
		//map initialization
		setUpMapIfNeeded();
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS) {
            Toast.makeText(this, "Please, enable GPS to use this application.", Toast.LENGTH_LONG).show();
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = LocationManager.GPS_PROVIDER;
        setCurrentLocation(locationManager.getLastKnownLocation(provider));
        if(getCurrentLocation() == null)
        	setCurrentLocation(LEUVEN_CENTER);
        zoomTo(getCurrentLocation());
        
        //Accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	protected void setView() {
		setContentView(R.layout.activity_map_record);
	}

	protected void addListenerToRecordButton() {
		btn_record = (Button) findViewById(R.id.btn_record);
		btn_record.setOnClickListener(recordButtonListener());
	}
	
	/**
	 * The OnClickListener for the record button.
	 * To be implemented by subclass
	 * @return
	 */
	abstract protected OnClickListener recordButtonListener();
	
	/**
	 * 
	 * @return
	 */
	protected int doSomeTasks() {
		 switch (progressBarStatus) {
			 case 0: return 10;
			 case 10: return 20;
			 case 20: return 30;
			 case 30: return 40;
			 case 40: return 50;
			 case 50: return 60;
			 case 60: return 70;
			 case 70: return 80;
			 case 80: return 90;
			 case 90: return 100;
		 }
		 return 100;
	}
	
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			setActionBarTitle();
		}
	}
	
	abstract protected void setActionBarTitle();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.sound_battle_record, menu);
		return true;
	}
	
	/**
	 * This function sets the default behaviour of the home button
	 * to the main activity. Override when needed.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			Intent homeIntent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			locationManager.removeUpdates(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		locationManager.removeUpdates(this);
		removeAccelerometerListener();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		locationManager.removeUpdates(this);
		removeAccelerometerListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 3, this);
		setUpMapIfNeeded();
	}
	


	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this Activity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the Activity may not have been completely destroyed during this process
	 * (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}
	
	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mUiSettings = mMap.getUiSettings();
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
		    public void onCameraChange(CameraPosition pos) {
		        if (pos.zoom != currentZoomLevel){
		            currentZoomLevel = pos.zoom;
		            lastTouchTime = new Date(System.currentTimeMillis());
		        }
		    }
		});
	}
	
	/**
	 * Checks if the map is ready (which depends on whether the Google Play
	 * services APK is available. This should be called prior to calling any
	 * methods on GoogleMap.
	 */
	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, R.string.txt_map_not_ready, Toast.LENGTH_LONG)
					.show();
			return false;
		}
		return true;
	}

	public void setZoomButtonsEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the zoom controls (+/- buttons in the bottom right
		// of the map).
		mUiSettings.setZoomControlsEnabled(((CheckBox) v).isChecked());
	}

	public void setCompassEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the compass (icon in the top left that indicates the
		// orientation of the
		// map).
		mUiSettings.setCompassEnabled(((CheckBox) v).isChecked());
	}

	public void setMyLocationButtonEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the my location button (this DOES NOT enable/disable
		// the my location
		// dot/chevron on the map). The my location button will never appear if
		// the my location
		// layer is not enabled.
		mUiSettings.setMyLocationButtonEnabled(((CheckBox) v).isChecked());
	}

	public void setMyLocationLayerEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables the my location layer (i.e., the dot/chevron on the
		// map). If enabled, it
		// will also cause the my location button to show (if it is enabled); if
		// disabled, the my
		// location button will never show.
		mMap.setMyLocationEnabled(((CheckBox) v).isChecked());
	}

	public void setScrollGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables scroll gestures (i.e. panning the map).
		mUiSettings.setScrollGesturesEnabled(((CheckBox) v).isChecked());
	}

	public void setZoomGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables zoom gestures (i.e., double tap, pinch & stretch).
		mUiSettings.setZoomGesturesEnabled(((CheckBox) v).isChecked());
	}

	public void setTiltGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables tilt gestures.
		mUiSettings.setTiltGesturesEnabled(((CheckBox) v).isChecked());
	}

	public void setRotateGesturesEnabled(View v) {
		if (!checkReady()) {
			return;
		}
		// Enables/disables rotate gestures.
		mUiSettings.setRotateGesturesEnabled(((CheckBox) v).isChecked());
	}
	
	/**
	 * Overridden methods of LocationListener
	 */
	@Override
	public void onLocationChanged(Location location) {
		if(isBetterLocation(location,getCurrentLocation())){
			setProviderFixed(true);
			setCurrentLocation(location);
			
			if(timeout()){
				zoomTo(getCurrentLocation());
			}
		}
	}
	
	public void addAccelerometerListener(){ 
		if(!sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI))
			Toast.makeText(getApplicationContext(), "Something wrong with the accelerometer", Toast.LENGTH_LONG).show();
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
	}
	
	public void removeAccelerometerListener(){
		sensorManager.unregisterListener(this, accelerometer);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event){
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			float[] eventValues = event.values.clone();
			float x = eventValues[0]; //x
			float y = eventValues[1]; //y
			float z = eventValues[2]; //z
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta;
			
			if(mAccel > Constants.ACCELEROMETER_BAD_QUALITY)
				badQualityMovements++;
			else if(mAccel > Constants.ACCELEROMETER_GOOD_QUALITY)
				goodQualityMovements++;
			else
				superQualityMovements++;
			allMovements++;
		}
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_SECONDS;
	    boolean isSignificantlyOlder = timeDelta < -TWO_SECONDS;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}


	
	protected boolean timeout() { 
		Date curDateTime = new Date(System.currentTimeMillis());  
		return curDateTime.getTime() - lastTouchTime.getTime() >= 15000;
	}

	protected void zoomTo(Location location) {
		double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLng = new LatLng(lat, lng);
		zoomTo(latLng);
	}
	
	private void zoomTo(LatLng latlng){
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, currentZoomLevel));
	}
	
	protected void setZoomLevel(float newZoomLevel){
		this.currentZoomLevel = newZoomLevel;
	}

	@Override
	public void onProviderDisabled(String provider) {
		setProviderFixed(false);
	}

	@Override
	public void onProviderEnabled(String provider) {
	}
	
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(status == LocationProvider.AVAILABLE && !gpsFixedMessageShown){
			Toast.makeText(getApplicationContext(), "GPS fixed! You can start recording now!", Toast.LENGTH_LONG).show();
			gpsFixedMessageShown = true;
		}
	}

	public void setProviderFixed(boolean providerFixed) {
		this.providerFixed = providerFixed;
	}

	public boolean isProviderFixed() {
		return providerFixed;
	}
	
	abstract protected boolean popupNeeded();
	
	protected void showPopup() {
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(
				R.layout.popup_explanation, null);

		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		TextView txt_title = (TextView) popupView.findViewById(R.id.txt_explanation_title);
		txt_title.setText(getActivityTitle());
		TextView txt_desc = (TextView) popupView.findViewById(R.id.txt_explanation);
		txt_desc.setText(getPopupExplanation());

		ImageButton btnDismiss = (ImageButton) popupView.findViewById(R.id.btn_popup_explanation_ok);
		btnDismiss.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		
		final CheckBox checkbox = (CheckBox) popupView.findViewById(R.id.checkBox_dont_show_again);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RecordActivity.this);
				Editor edit = sp.edit();
				if(checkbox.isChecked())
					edit.putBoolean(getPopupDontShowAgainName(), true);
				else
					edit.putBoolean(getPopupDontShowAgainName(), false);
				edit.commit();
			}
		}
		);
		
		DisplayMetrics metrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		popupWindow.setHeight(metrics.heightPixels);
		popupWindow.setWidth(metrics.widthPixels);
		findViewById(R.id.layout_map_record).post(new Runnable() {
			@Override
			public void run() {
				popupWindow.showAtLocation(
						findViewById(R.id.layout_map_record),
						Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
	}

	protected abstract int getActivityTitle();
	protected abstract int getPopupExplanation();
	protected abstract String getPopupDontShowAgainName();

	/**
	 * @return the progressBar
	 */
	protected ProgressDialog getProgressBar() {
		return progressBar;
	}
	
	/**
	 * @return the progressBar
	 */
	protected void setProgressBar(ProgressDialog pd) {
		this.progressBar = pd;
	}

	protected void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	protected Location getCurrentLocation() {
		return currentLocation;
	}

	public double calculateQuality() {
		return (superQualityMovements - badQualityMovements)/((float) allMovements);
	}
}
