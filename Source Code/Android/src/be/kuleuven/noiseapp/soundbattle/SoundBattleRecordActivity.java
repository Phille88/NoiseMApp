package be.kuleuven.noiseapp.soundbattle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import be.kuleuven.noiseapp.MainActivity;
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.exception.NotInLeuvenException;
import be.kuleuven.noiseapp.friends.AddFriendshipTask;
import be.kuleuven.noiseapp.recording.RecordActivity;
import be.kuleuven.noiseapp.tools.BitmapScaler;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

import be.kuleuven.noiseapp.R;
public class SoundBattleRecordActivity extends RecordActivity {
	
	private ArrayList<SoundBattleLocation> SBLocations = new ArrayList<SoundBattleLocation>();
	private static final double MINIMUM_DISTANCE_SBL = 5; //metres
	private boolean battleLocationsInitialized;
	private long SoundBattleID;
	private SoundBattleRecordActivity sbra;
	private UserDetails opponentUserDetails;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		sbra = this;
		
		setSoundBattleID(getIntent().getLongExtra(MemoryFileNames.SOUNDBATTLEID, 0L));
		setOpponentDetails((UserDetails) ObjectSerializer.deserialize(getIntent().getStringExtra(MemoryFileNames.OPPONENTDETAILS)));
		
		showOpponentBox();
		if (getIntent().hasExtra("SBLlongitudes")){ //loaded SoundBattle
			loadSoundBattle();
		}
		

		final Button btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
		if(isFriend()){
			btn_add_friend.setText("Friend");
			btn_add_friend.setBackgroundColor(Color.GREEN);
		} else {
			btn_add_friend.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v) {
					new AddFriendshipTask(sbra).execute(opponentUserDetails.getUserID());
					btn_add_friend.setText("Friend");
					btn_add_friend.setBackgroundColor(Color.GREEN);
				}});
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isFriend() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		ArrayList<UserDetails> friends = (ArrayList<UserDetails>) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.FRIENDLIST,  ObjectSerializer.serialize(new ArrayList<UserDetails>())));
		boolean isFriend = false;
		for(UserDetails friend : friends) {
			if(friend.getUserID() == opponentUserDetails.getUserID())
				isFriend = true;
		}
		return isFriend;
	}

	/**
	 * @return the opponentName
	 */
	public String getOpponentName() {
		return opponentUserDetails.getObfuscatedFullName();
	}
	
	private void setOpponentDetails(UserDetails opponentDetails) {
		this.opponentUserDetails = opponentDetails;
	}

	private void showOpponentBox() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_map_record);
		View toAdd = getLayoutInflater().inflate(R.layout.box_add_friends,null);
		layout.addView(toAdd);
		TextView opponentName = (TextView) findViewById(R.id.txt_opponent_name);
		opponentName.setText(getOpponentName());
		
		ImageView img_opponentPicture = (ImageView) findViewById(R.id.img_opponent_profile_picture);
		Bitmap bm = null;
		try {
			FileInputStream fis = openFileInput(MemoryFileNames.PROFILE_PICTURE + "_" + opponentUserDetails.getUserID());
			bm = BitmapFactory.decodeStream(fis);
			fis.close();
		} 	
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if(bm != null){
			img_opponentPicture.setImageBitmap(BitmapScaler.scaleCenterCrop(bm, Constants.AVATARHEIGHT, Constants.AVATARWIDTH));
			img_opponentPicture.setMaxHeight(Constants.AVATARHEIGHT);
			img_opponentPicture.setMaxWidth(Constants.AVATARWIDTH);
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setActionBarTitle() {
			getActionBar().setTitle(R.string.txt_sound_battle_name);
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
			Intent homeIntent = new Intent(this, SoundBattleActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			locationManager.removeUpdates(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected OnClickListener recordButtonListener(){
		return new OnClickListener(){

			@Override
			public void onClick(View v) {
					if (!isProviderFixed())
						Toast.makeText(getApplicationContext(),
								"Wait for the GPS to have a fixed location",
								Toast.LENGTH_LONG).show();
					else if (!getClosestSoundBattleLocationToRecord().isClose(
							getCurrentLocation()))
						Toast.makeText(
								getApplicationContext(),
								"You have to get closer to a Sound Battle Location",
								Toast.LENGTH_LONG).show();
					else {
						new SoundBattleRecordTask(v,sbra).execute(getCurrentLocation());
					}
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void initializeBattleLocations() {
		//new sound battle creation
			SoundBattleLocationGenerator p = new SoundBattleLocationGenerator(
					getApplicationContext(), getCurrentLocation());
			try {
				p.generate();
			} catch (NotInLeuvenException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"You are not in Leuven. Play the battle when you are in Leuven.",
						Toast.LENGTH_LONG).show();
				try {
					Thread.sleep(8000);//TODO testen in Antwerpen!
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				Intent homeIntent = new Intent(this, MainActivity.class);
				NavUtils.navigateUpTo(this, homeIntent);
				super.locationManager.removeUpdates(this);
				finish();
				return;
			}
			for (int i = 0; i < 3; i++) {
				SoundBattleLocation toAdd;
				
				do{
					toAdd = p.getRandomSoundBattleLocation(getCurrentLocation());
				}
				while(contains(toAdd));
				SBLocations.add(toAdd);
			}
			new SaveSoundBattleLocations(getSoundBattleID()).execute(SBLocations);
		updateMarkers();
	}
	
	private void loadSoundBattle(){
		battleLocationsInitialized = true;
		int[] SBLIDs = getIntent().getIntArrayExtra("SBLIDs");
		double[] SBLlongitudes = getIntent().getDoubleArrayExtra("SBLlongitudes");
		double[] SBLlatitudes = getIntent().getDoubleArrayExtra("SBLlatitudes");
		boolean[] SBLrecorded = getIntent().getBooleanArrayExtra("SBLrecorded");
		setSoundBattleID(getIntent().getLongExtra("soundBattleID", 0L));
		for(int i = 0; i < SBLlongitudes.length; i++){
			SoundBattleLocation sbl = new SoundBattleLocation(SBLlongitudes[i], SBLlatitudes[i]);
			sbl.setRecorded(SBLrecorded[i]);
			sbl.setSoundBattleLocationID(SBLIDs[i]);
			SBLocations.add(sbl);
		}
		updateMarkers();
	}
	
	private boolean contains(NoiseLocation other){
		for(NoiseLocation nl : SBLocations)
			if(nl.getDistance(other) <= MINIMUM_DISTANCE_SBL)
					return true;
		return false;
	}

	public void updateMarkers() {
		super.mMap.clear();
		for (SoundBattleLocation sbl : SBLocations) {
			super.mMap.addMarker(sbl.getMarker(getCurrentLocation()));
		}
	}

	/**
	 * Overridden methods of LocationListener
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location, getCurrentLocation())) {
			setProviderFixed(true);
			setCurrentLocation(location);
			if (!battleLocationsInitialized) {
				initializeBattleLocations();
				battleLocationsInitialized = true;
			}
			updateMarkers();
			if (timeout()) {
				zoomTo(getCurrentLocation());
			}
		}
	}

	public SoundBattleLocation getClosestSoundBattleLocationToRecord() {
		double smallestDistance = Double.MAX_VALUE;
		SoundBattleLocation toReturn = null;
		for (SoundBattleLocation sbl : SBLocations)
			if (sbl.getDistance(getCurrentLocation()) < smallestDistance
					&& !sbl.isRecorded()) {
				smallestDistance = sbl.getDistance(getCurrentLocation());
				toReturn = sbl;
			}
		return toReturn;
	}

	public boolean isEverythingRecorded() {
		for(SoundBattleLocation sbl : SBLocations)
			if(!sbl.isRecorded())
				return false;
		return true;
	}


	@Override
	protected boolean popupNeeded() {
		return true;
	}


	@Override
	protected int getActivityTitle() {
		return R.string.txt_sound_battle_name;
	}


	@Override
	protected int getPopupExplanation() {
		return R.string.txt_sound_battle_explanation;
	}
	
	@Override
	protected String getPopupDontShowAgainName(){
		return "SBR_DSA";
	}

	private void setSoundBattleID(long soundBattleID) {
		SoundBattleID = soundBattleID;
	}

	public long getSoundBattleID() {
		return SoundBattleID;
	}

}
