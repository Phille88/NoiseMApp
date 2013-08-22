package be.kuleuven.noiseapp.profile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.friends.DeleteFriendshipTask;
import be.kuleuven.noiseapp.points.Badges;
import be.kuleuven.noiseapp.tools.BitmapScaler;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

import be.kuleuven.noiseapp.R;
public class ViewOtherProfileActivity extends Activity {
	
	SharedPreferences sp;
	UserDetails userDetails;
	Activity vopa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_other_profile);
		setupActionBar();
		this.vopa = this;
		initializeUserDetails();
	}

	@SuppressLint("NewApi")
	private void initializeUserDetails() {
		userDetails = (UserDetails) getIntent().getSerializableExtra(MemoryFileNames.OTHER_PROFILE_DETAILS);
		
		TextView txt_userName = (TextView) findViewById(R.id.txt_other_username);
		txt_userName.setText(userDetails.getObfuscatedFullName());
		
		Button btn_delete_friend = (Button) findViewById(R.id.btn_delete_other_friend);
		btn_delete_friend.setBackgroundColor(Color.RED);
		btn_delete_friend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new DeleteFriendshipTask(vopa).execute(userDetails.getUserID());
				v.setVisibility(View.GONE);
			}
		});
		
		TextView txt_totalPoints = (TextView) findViewById(R.id.txt_other_points_earned_username);
		txt_totalPoints.setText(Long.toString(userDetails.getTotalPoints()));
		
		TextView txt_soundBattlesWon = (TextView) findViewById(R.id.txt_other_battles_won_username);
		txt_soundBattlesWon.setText(Long.toString(userDetails.getSoundBattlesWon()));
		
		if(userDetails.getLastSoundCheckin() != null){
			TextView txt_lastSoundCheckin = (TextView) findViewById(R.id.txt_other_last_sound_checkin_username);
			txt_lastSoundCheckin.setText(userDetails.getLastSoundCheckin().getPlaceName() + " (" + new DecimalFormat("#").format(userDetails.getLastSoundCheckin().getDB()) + " dB)");
		}
		
		TableLayout tbl_layout = (TableLayout) findViewById(R.id.tbl_other_badges);
		ArrayList<Integer> badgeIDs = userDetails.getBadgeIDs();
		TableRow badgeRow = null;
		TableRow badgeNameRow = null;
		for(int i = 0; i < badgeIDs.size(); i++){
			if(i % 3 == 0){
				badgeRow = new TableRow(this);
				badgeRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				badgeNameRow = new TableRow(this);
				badgeNameRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			ImageView badge = new ImageView(this);
			badge.setImageResource(Badges.getBadgeImage(badgeIDs.get(i)));
			
			//next 3 lines are a hack to size the image...
			badge.setAdjustViewBounds(true);
			Point displaySize = new Point();
			getWindowManager().getDefaultDisplay().getSize(displaySize);
			badge.setMaxWidth(displaySize.x/3);
			
			badge.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
			badgeRow.addView(badge);
			
			TextView badgeName = new TextView(this);
			badgeName.setText(Badges.getBadgeName(badgeIDs.get(i)));
			badgeName.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
			badgeName.setTypeface(null,Typeface.BOLD);
			badgeName.setGravity(Gravity.CENTER_HORIZONTAL);
			badgeName.setMaxWidth(displaySize.x/3);
			badgeNameRow.addView(badgeName);
			
			if(i % 3 == 0){
				tbl_layout.addView(badgeRow);
				tbl_layout.addView(badgeNameRow);
			}
		}
		switch(badgeIDs.size()%3){
		case 1 :
			ImageView extraInvBadge1 = new ImageView(this);
			extraInvBadge1.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
			badgeRow.addView(extraInvBadge1);
			TextView extraInvBadgeName1 = new TextView(this);
			extraInvBadgeName1.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
			badgeNameRow.addView(extraInvBadgeName1);
		case 2 :
			ImageView extraInvBadge2 = new ImageView(this);
			extraInvBadge2.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
			badgeRow.addView(extraInvBadge2);
			TextView extraInvBadgeName2 = new TextView(this);
			extraInvBadgeName2.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
			badgeNameRow.addView(extraInvBadgeName2);
			break;
		}
		
		ImageView img_profilePicture = (ImageView) findViewById(R.id.img_other_profile_picture);
		Bitmap bm = null;
		try {
			FileInputStream fis = openFileInput(MemoryFileNames.PROFILE_PICTURE + "_" + userDetails.getUserID());
			bm = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (bm != null) {
			img_profilePicture.setImageBitmap(BitmapScaler.scaleCenterCrop(bm, Constants.PROFILEPICTUREHEIGHT, Constants.PROFILEPICTUREWIDTH));
			img_profilePicture.setMaxHeight(Constants.PROFILEPICTUREHEIGHT);
			img_profilePicture.setMaxWidth(Constants.PROFILEPICTUREWIDTH);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(R.string.txt_view_other_profile_name);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent homeIntent = new Intent(this, ViewProfileTabActivity.class);
			homeIntent.putExtra(MemoryFileNames.COMINGFROMOTHERPROFILE, true);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
