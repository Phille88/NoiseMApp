package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import be.kuleuven.noiseapp.MainActivity;
import be.kuleuven.noiseapp.soundbattle.SoundBattleItemAdapter.RowType;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

import be.kuleuven.noiseapp.R;
public class SoundBattleActivity extends Activity {
	private ArrayList<iSoundBattleListItem> soundBattles = new ArrayList<iSoundBattleListItem>();
	private SoundBattleActivity sba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sba = this;
		setContentView(R.layout.activity_sound_battle);
		setupActionBar();
		
		performSearch();
		
		Button btn_new_game = (Button) findViewById(R.id.btn_new_game);
		btn_new_game.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new CreateRandomSoundBattleTask(sba).execute();
			}
		});
	}

	private void performSearch() {
		new GetAllSoundBattlesTask(this).execute();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.txt_sound_battle_name);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.random_sound_battle, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent homeIntent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setSoundBattles(ArrayList<iSoundBattleListItem> soundBattleItems){
		this.soundBattles = soundBattleItems;
	}

	public void listBattles() {
		ListView listView = (ListView) findViewById(R.id.list_sound_battles);
		
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		final SoundBattleItemAdapter adapter = new SoundBattleItemAdapter(this,android.R.id.text1, soundBattles);
		final SoundBattleActivity sba = this;
		listView.setAdapter(adapter); 
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				SoundBattleItem item = (SoundBattleItem) adapter.getItem(position);
				if(item.getViewType() == RowType.LIST_OPEN_ITEM.ordinal())
					new LoadSoundBattleTask(sba).execute(item.getSoundBattleID());
				if(item.getViewType() == RowType.LIST_FINISHED_ITEM.ordinal()){
					SoundBattleRecordingPoints sbrp = null;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					try {
						sbrp = new CalculateSoundBattlePoints().execute(item.getSoundBattleID(), sp.getLong(MemoryFileNames.USERID, 0L)).get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					Intent i = new Intent(getApplicationContext(), SoundBattlePointsActivity.class);
					i.putExtra(MemoryFileNames.SOUNDBATTLE_POINTS,sbrp);
					startActivity(i);
				}
			}
		});
	}
}