package be.kuleuven.noiseapp.noisehunt;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.ObjectSerializer;

import be.kuleuven.noiseapp.R;
public class NoiseHuntActivity extends Activity {

	private static final long BLITZKRIEG_MINIMUM_POINTS = 500;
	protected static final long PARTYTIME_MINIMUM_POINTS = 1000;
	private static final long RIVERSIDE_MINIMUM_POINTS = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noise_hunt);
		setupActionBar();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int lastFinishedNoiseHunt = sp.getInt("lastFinishedNoiseHunt", 0);
		long totalPointsUser = ((UserDetails) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.USERDETAILS, null))).getTotalPoints();
		
		/**
		 * Walk in the Park
		 */
		Button btn_walkinthepark = (Button) findViewById(R.id.btn_walkinthepark);
		if(lastFinishedNoiseHunt < Constants.WALKINTHEPARK_ID){
			btn_walkinthepark.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_walkinthepark.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(), WalkInTheParkRecordActivity.class);
					startActivity(intent);
				}
			});
		}
		else
			btn_walkinthepark.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * BLITZKRIEG
		 */
		Button btn_blitzkrieg = (Button) findViewById(R.id.btn_blitzkrieg);
		if(lastFinishedNoiseHunt < Constants.WALKINTHEPARK_ID){
			btn_blitzkrieg.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
			btn_blitzkrieg.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "First, finish previous hunts!", Toast.LENGTH_LONG).show();
				}
			});
		}
		else if(totalPointsUser < BLITZKRIEG_MINIMUM_POINTS){
			{
				btn_blitzkrieg.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
				btn_blitzkrieg.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Toast.makeText(v.getContext(), "You need to have at least " + BLITZKRIEG_MINIMUM_POINTS + " points to play this hunt!", Toast.LENGTH_LONG).show();
					}
				});
			}
		}
		else if(lastFinishedNoiseHunt < Constants.BLITZKRIEG_ID){
			btn_blitzkrieg.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_blitzkrieg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), BlitzkriegRecordActivity.class);
				startActivity(intent);
			}
		});
		}
		else 
			btn_blitzkrieg.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Party Time
		 */
		Button btn_partytime = (Button) findViewById(R.id.btn_partytime);
		if(lastFinishedNoiseHunt < Constants.BLITZKRIEG_ID){
			btn_partytime.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
			btn_partytime.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "First, finish previous hunts!", Toast.LENGTH_LONG).show();
				}
			});
		}
		else if(totalPointsUser < PARTYTIME_MINIMUM_POINTS){
			{
				btn_partytime.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
				btn_partytime.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Toast.makeText(v.getContext(), "You need to have at least " + PARTYTIME_MINIMUM_POINTS + " points to play this hunt!", Toast.LENGTH_LONG).show();
					}
				});
			}
		}
		else if(lastFinishedNoiseHunt < Constants.PARTYTIME_ID){
			btn_partytime.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_partytime.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(), PartyTimeRecordActivity.class);
					startActivity(intent);
				}
			});
		}
		else
			btn_partytime.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Riverside
		 */
		Button btn_riverside = (Button) findViewById(R.id.btn_riverside);
		if(lastFinishedNoiseHunt < Constants.PARTYTIME_ID){
			btn_riverside.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
			btn_riverside.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "First, finish previous hunts!", Toast.LENGTH_LONG).show();
				}
			});
		}
		else if(totalPointsUser < RIVERSIDE_MINIMUM_POINTS){
			{
				btn_riverside.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
				btn_riverside.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Toast.makeText(v.getContext(), "You need to have at least " + RIVERSIDE_MINIMUM_POINTS + " points to play this hunt!", Toast.LENGTH_LONG).show();
					}
				});
			}
		}
		else if(lastFinishedNoiseHunt < Constants.RIVERSIDE_ID){
			btn_riverside.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_riverside.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(), RiversideRecordActivity.class);
					startActivity(intent);
				}
			});
		}
		else
			btn_riverside.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Trainspotting
		 */
		Button btn_trainspotting = (Button) findViewById(R.id.btn_trainspotting);
		if(lastFinishedNoiseHunt < Constants.RIVERSIDE_ID){
			btn_trainspotting.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
			btn_trainspotting.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "First, finish previous hunts!", Toast.LENGTH_LONG).show();
				}
			});
		}
		else if(lastFinishedNoiseHunt < Constants.TRAINSPOTTING_ID){
			btn_trainspotting.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_trainspotting.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Toast.makeText(getApplicationContext(), "This will become available soon!", Toast.LENGTH_LONG).show();
				}
			});
		}
		else
			btn_trainspotting.setBackgroundResource(R.drawable.img_btn_hunt_done);
		
		/**
		 * Morning Glory
		 */
		Button btn_morningglory = (Button) findViewById(R.id.btn_morningglory);
		if(lastFinishedNoiseHunt < Constants.TRAINSPOTTING_ID){
			btn_morningglory.setBackgroundResource(R.drawable.img_btn_hunt_inactive);
			btn_morningglory.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "First, finish previous hunts!", Toast.LENGTH_LONG).show();
				}
			});
		}
		else if(lastFinishedNoiseHunt < Constants.MORNINGGLORY_ID){
			btn_morningglory.setBackgroundResource(R.drawable.img_btn_hunt_active);
			btn_morningglory.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Toast.makeText(getApplicationContext(), "This will become available soon!", Toast.LENGTH_LONG).show();
				}
			});
		}
		else
			btn_morningglory.setBackgroundResource(R.drawable.img_btn_hunt_done);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.title_activity_noise_hunt);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
