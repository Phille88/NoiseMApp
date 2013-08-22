package be.kuleuven.noiseapp.profile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import be.kuleuven.noiseapp.tools.BitmapScaler;
import be.kuleuven.noiseapp.tools.Constants;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

import be.kuleuven.noiseapp.R;
public class FriendItemAdapter extends ArrayAdapter<FriendItem> {

	private ArrayList<FriendItem> friendItems;
	private Context context;

	public FriendItemAdapter(Context context, int textViewResourceId, ArrayList<FriendItem> friendItems) {
		super(context, textViewResourceId, friendItems);
		this.context = context;
		this.friendItems = friendItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_friends, null);
		}

		FriendItem friend = friendItems.get(position);
		if (friend != null) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			long thisUserID = sp.getLong(MemoryFileNames.USERID, 0L);

			TextView userName = (TextView) v.findViewById(R.id.txt_friend_item_name);
			TextView totalPoints = (TextView) v.findViewById(R.id.txt_friend_item_total_points);
			ImageView img_profilePicture = (ImageView) v.findViewById(R.id.img_friend_item_profile_picture);
			TableRow row = (TableRow) v.findViewById(R.id.tableRow_friend_item);

			if (userName != null && totalPoints != null && img_profilePicture != null && row != null) {
				userName.setText(friend.getObfuscatedFullName());
				totalPoints.setText(Long.toString(friend.getTotalPoints()));

				if (position % 2 == 0) {
					row.setBackgroundColor(Color.DKGRAY);
					userName.setTextColor(Color.LTGRAY);
					totalPoints.setTextColor(Color.LTGRAY);
				} else {
					row.setBackgroundColor(Color.LTGRAY);
					userName.setTextColor(Color.DKGRAY);
					totalPoints.setTextColor(Color.DKGRAY);
				}

				if (friend.getUserID() == thisUserID) {
					userName.setTypeface(null, Typeface.BOLD);
					totalPoints.setTypeface(null, Typeface.BOLD);
				} else {
					userName.setTypeface(null, Typeface.NORMAL);
					totalPoints.setTypeface(null, Typeface.NORMAL);
				}

				Bitmap bm = null;
				FileInputStream fis;
				try {
					if(friend.getUserID() == thisUserID)
						fis = context.openFileInput(MemoryFileNames.PROFILE_PICTURE);
					else
						fis = context.openFileInput(MemoryFileNames.PROFILE_PICTURE + "_" + friend.getUserID());
					bm = BitmapFactory.decodeStream(fis);
					fis.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (bm != null) {
					img_profilePicture.setImageBitmap(BitmapScaler.scaleCenterCrop(bm, Constants.AVATARHEIGHT, Constants.AVATARWIDTH));
					img_profilePicture.setMaxHeight(Constants.AVATARHEIGHT);
					img_profilePicture.setMaxWidth(Constants.AVATARWIDTH);
				}
			}

		}
		return v;
	}

	@Override
	public boolean isEnabled(int position) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		long userID = sp.getLong(MemoryFileNames.USERID, 0L);
		if (getItem(position).getUserID() == userID)
			return false;
		return true;
	}

}
