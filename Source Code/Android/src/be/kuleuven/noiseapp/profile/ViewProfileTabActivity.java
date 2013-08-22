package be.kuleuven.noiseapp.profile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import be.kuleuven.noiseapp.auth.UpdateLocalProfileDetailsTask;
import be.kuleuven.noiseapp.auth.UserDetails;
import be.kuleuven.noiseapp.friends.UpdateFriendsListLocalTask;
import be.kuleuven.noiseapp.points.Badges;
import be.kuleuven.noiseapp.tools.BitmapScaler;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.MemoryFileNames;
import be.kuleuven.noiseapp.tools.ObjectSerializer;
import be.kuleuven.noiseapp.tools.QuickSort;

import be.kuleuven.noiseapp.R;
public class ViewProfileTabActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	private static UpdateLocalProfileDetailsTask updateProfileTask;
	private static UpdateFriendsListLocalTask updateFriendsListTask;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile_tab);
		updateProfileTask = new UpdateLocalProfileDetailsTask(this);
		updateProfileTask.execute();
		updateFriendsListTask = new UpdateFriendsListLocalTask(this);
		updateFriendsListTask.execute();

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
		if (getIntent().getBooleanExtra(MemoryFileNames.COMINGFROMOTHERPROFILE, false))
			actionBar.setSelectedNavigationItem(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.view_profile_tab, menu);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new MeFragment();
				break;
			case 1:
				fragment = new FriendsFragment();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class MeFragment extends Fragment {

		public MeFragment() {
			this.setRetainInstance(true);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			//RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.tab1_view_profile_me, container, false);
			ScrollView rootView = (ScrollView) inflater.inflate(R.layout.tab1_view_profile_me, container, false);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
			try {
				updateProfileTask.get();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
			UserDetails userDetails = (UserDetails) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.USERDETAILS, null));

			TextView txt_userName = (TextView) rootView.findViewById(R.id.txt_username);
			txt_userName.setText(userDetails.getFullName());

			TextView txt_totalPoints = (TextView) rootView.findViewById(R.id.txt_points_earned_username);
			txt_totalPoints.setText(Long.toString(userDetails.getTotalPoints()));
			
			TextView txt_soundBattlesWon = (TextView) rootView.findViewById(R.id.txt_battles_won_username);
			txt_soundBattlesWon.setText(Long.toString(userDetails.getSoundBattlesWon()));
			
			if(userDetails.getLastSoundCheckin() != null){
				TextView txt_lastSoundCheckin = (TextView) rootView.findViewById(R.id.txt_last_sound_checkin_username);
				txt_lastSoundCheckin.setText(userDetails.getLastSoundCheckin().getPlaceName() + " (" + new DecimalFormat("#").format(userDetails.getLastSoundCheckin().getDB()) + " dB)");
			}
				
			TableLayout tbl_layout = (TableLayout) rootView.findViewById(R.id.tbl_badges);
			ArrayList<Integer> badgeIDs = userDetails.getBadgeIDs();
			TableRow badgeRow = null;
			TableRow badgeNameRow = null;
			for(int i = 0; i < badgeIDs.size(); i++){
				if(i % 3 == 0){
					badgeRow = new TableRow(getActivity());
					badgeRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					badgeNameRow = new TableRow(getActivity());
					badgeNameRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				}
				ImageView badge = new ImageView(getActivity());
				badge.setImageResource(Badges.getBadgeImage(badgeIDs.get(i)));
				badge.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
				badgeRow.addView(badge);
				
				TextView badgeName = new TextView(getActivity());
				badgeName.setText(Badges.getBadgeName(badgeIDs.get(i)));
				badgeName.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
				badgeName.setTypeface(null,Typeface.BOLD);
				badgeName.setGravity(Gravity.CENTER_HORIZONTAL);
				badgeNameRow.addView(badgeName);
				
				if(i % 3 == 0){
					tbl_layout.addView(badgeRow);
					tbl_layout.addView(badgeNameRow);
				}
			}
			switch(badgeIDs.size()%3){
			case 1 :
				ImageView extraInvBadge1 = new ImageView(getActivity());
				extraInvBadge1.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
				badgeRow.addView(extraInvBadge1);
				TextView extraInvBadgeName1 = new TextView(getActivity());
				extraInvBadgeName1.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
				badgeNameRow.addView(extraInvBadgeName1);
			case 2 :
				ImageView extraInvBadge2 = new ImageView(getActivity());
				extraInvBadge2.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
				badgeRow.addView(extraInvBadge2);
				TextView extraInvBadgeName2 = new TextView(getActivity());
				extraInvBadgeName2.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1/3f));
				badgeNameRow.addView(extraInvBadgeName2);
				break;
			}
			

			ImageView img_profilePicture = (ImageView) rootView.findViewById(R.id.img_profile_picture);
			Bitmap bm = null;
			try {
				FileInputStream fis = getActivity().openFileInput(MemoryFileNames.PROFILE_PICTURE);
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
			return rootView;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class FriendsFragment extends Fragment {

		private ArrayList<FriendItem> friendItems = new ArrayList<FriendItem>();
		private ArrayList<UserDetails> friends = new ArrayList<UserDetails>();
		private ListView listView;
		
		public FriendsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.tab2_view_profile_friends, container, false);
			listView = (ListView) rootView.findViewById(R.id.list_friends);
			getFriendDetails();
			FriendItemAdapter adapter = new FriendItemAdapter(getActivity(), android.R.id.text1, friendItems);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
					Intent i = new Intent(getActivity(), ViewOtherProfileActivity.class);
					UserDetails userDetails = ((FriendItem) listView.getAdapter().getItem(position)).getUserDetails();
					i.putExtra(MemoryFileNames.OTHER_PROFILE_DETAILS, userDetails);
					startActivity(i);
				}
			});
			return rootView;
		}

		private void getFriendDetails() {
			friendItems.clear();
			friends = new ArrayList<UserDetails>();
			try {
				friends = updateFriendsListTask.get();
				if (!friends.isEmpty()) {
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					friends.add((UserDetails) ObjectSerializer.deserialize(sp.getString(MemoryFileNames.USERDETAILS, null)));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			QuickSort<UserDetails> qs = new QuickSort<UserDetails>(new UserDetails());
			qs.sort(friends);
			for (UserDetails friend : friends) {
				friendItems.add(new FriendItem(friend));
			}
		}
		
		@Override
		public void setUserVisibleHint(boolean isVisibleToUser){
			super.setUserVisibleHint(isVisibleToUser);
			if(isVisibleToUser && friends != null && friends.isEmpty()){
				Toast.makeText(getActivity(), "Add friends during Sound Battles to compare results!", Toast.LENGTH_LONG).show();
			}
		}
	}

}
