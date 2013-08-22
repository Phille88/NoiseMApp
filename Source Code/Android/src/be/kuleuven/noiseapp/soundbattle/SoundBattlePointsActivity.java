package be.kuleuven.noiseapp.soundbattle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

import be.kuleuven.noiseapp.R;
public class SoundBattlePointsActivity extends Activity {
	
	SoundBattleRecordingPoints sbrp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_battle_points);
		sbrp = (SoundBattleRecordingPoints) getIntent().getSerializableExtra(MemoryFileNames.SOUNDBATTLE_POINTS);
		setupActionBar();
		if(!sbrp.getUserPoints().getBadges().isEmpty())
			showBadgePopup();
		setPoints();
	}
	
	private void setPoints() {
		setQuality();
		setLocation();
		setSpeed();
		setTotal();
	}

	private void setTotal() {
		int userTotalPoints = sbrp.getUserTotalPoints();
		int opponentTotalPoints = sbrp.getOpponentTotalPoints();
		int totalPoints = userTotalPoints + opponentTotalPoints;
		
		float userWeight = (float) userTotalPoints / (float) totalPoints;
		float opponentWeight = (float) opponentTotalPoints / (float) totalPoints;
		
		TextView txt_user_totalPoints = (TextView) findViewById(R.id.txt_total_score_player);
		TextView txt_opponent_totalPoints = (TextView) findViewById(R.id.txt_total_score_opponent);
		
		txt_user_totalPoints.setText(Integer.toString(userTotalPoints));
		txt_user_totalPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, userWeight));
		txt_opponent_totalPoints.setText(Integer.toString(opponentTotalPoints));
		txt_opponent_totalPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, opponentWeight));
	
		TextView txt_won = (TextView) findViewById(R.id.txt_youve_won);
		if(userTotalPoints > opponentTotalPoints)
			txt_won.setText("You have won!");
		else if(userTotalPoints < opponentTotalPoints)
			txt_won.setText("You have lost :(...");
		else
			txt_won.setText("Cool, it is even!");
	
	}

	private void setSpeed() {
		int userSpeedPoints = sbrp.getUserSpeedPoints();
		int opponentSpeedPoints = sbrp.getOpponentSpeedPoints();
		int totalSpeedPoints = userSpeedPoints + opponentSpeedPoints;
		
		float userWeight = (float) userSpeedPoints / (float) totalSpeedPoints;
		float opponentWeight = (float) opponentSpeedPoints / (float) totalSpeedPoints;
		
		TextView txt_user_totalSpeedPoints = (TextView) findViewById(R.id.txt_speed_score_player);
		TextView txt_opponent_totalSpeedPoints = (TextView) findViewById(R.id.txt_speed_score_opponent);
		
		txt_user_totalSpeedPoints.setText(Integer.toString(userSpeedPoints));
		txt_user_totalSpeedPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, userWeight));
		txt_opponent_totalSpeedPoints.setText(Integer.toString(opponentSpeedPoints));
		txt_opponent_totalSpeedPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, opponentWeight));
	}

	private void setLocation() {
		int userLocationPoints = sbrp.getUserAccuracyPoints();
		int opponentLocationPoints = sbrp.getOpponentAccuracyPoints();
		int totalLocationPoints = userLocationPoints + opponentLocationPoints;
		
		float userWeight = (float) userLocationPoints / (float) totalLocationPoints;
		float opponentWeight = (float) opponentLocationPoints / (float) totalLocationPoints;
		
		TextView txt_user_totalLocationPoints = (TextView) findViewById(R.id.txt_location_accuracy_score_player);
		TextView txt_opponent_totalLocationPoints = (TextView) findViewById(R.id.txt_location_accuracy_score_opponent);
		
		txt_user_totalLocationPoints.setText(Integer.toString(userLocationPoints));
		txt_user_totalLocationPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, userWeight));
		txt_opponent_totalLocationPoints.setText(Integer.toString(opponentLocationPoints));
		txt_opponent_totalLocationPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, opponentWeight));
	}

	private void setQuality() {
		int userQualityPoints = sbrp.getUserQualityPoints();
		int opponentQualityPoints = sbrp.getOpponentQualityPoints();
		int totalQualityPoints = userQualityPoints + opponentQualityPoints;
		
		float userWeight = (float) userQualityPoints / (float) totalQualityPoints;
		float opponentWeight = (float) opponentQualityPoints / (float) totalQualityPoints;
		
		TextView txt_user_totalQualityPoints = (TextView) findViewById(R.id.txt_sound_quality_score_player);
		TextView txt_opponent_totalQualityPoints = (TextView) findViewById(R.id.txt_sound_quality_score_opponent);
		
		txt_user_totalQualityPoints.setText(Integer.toString(userQualityPoints));
		txt_user_totalQualityPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, userWeight));
		txt_opponent_totalQualityPoints.setText(Integer.toString(opponentQualityPoints));
		txt_opponent_totalQualityPoints.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, opponentWeight));
	}

	private void showBadgePopup(){
		LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
	    View popupView = layoutInflater.inflate(R.layout.popup_badge, null);
	    
	    final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
	    
	    TextView txt_badge_desc = (TextView) popupView.findViewById(R.id.txt_badge_description);
	    txt_badge_desc.setText(sbrp.getUserPoints().getBadges().get(0).getDescription());

	    TextView txt_badge = (TextView) popupView.findViewById(R.id.txt_badge);
	    txt_badge.setText(sbrp.getUserPoints().getBadges().get(0).getName());

	    ImageView img_badge = (ImageView) popupView.findViewById(R.id.img_badge);
	    img_badge.setBackgroundResource(sbrp.getUserPoints().getBadges().get(0).getImage());
	    
	    ImageButton btnDismiss = (ImageButton)popupView.findViewById(R.id.btn_popup_ok);
	    btnDismiss.setOnClickListener(new ImageButton.OnClickListener(){
	     @Override
	     public void onClick(View v) {
	    	 popupWindow.dismiss();
	     }
	     });
	    DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
	    popupWindow.setHeight(metrics.heightPixels);
	    popupWindow.setWidth(metrics.widthPixels);
	    findViewById(R.id.layout_sound_battle_points).post(new Runnable() {
	    	   @Override
			public void run() {
	    		    popupWindow.showAtLocation(findViewById(R.id.layout_sound_battle_points), Gravity.CENTER_HORIZONTAL, 0, 0);
	    	   }
	    	});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
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
		// getMenuInflater().inflate(R.menu.sound_battle_points, menu);
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
//			NavUtils.navigateUpFromSameTask(this);
			Intent homeIntent = new Intent(this, SoundBattleActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
