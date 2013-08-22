package be.kuleuven.noiseapp.noisehunt;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.kuleuven.noiseapp.points.Badge;
import be.kuleuven.noiseapp.points.Point;
import be.kuleuven.noiseapp.points.RecordingPoints;
import be.kuleuven.noiseapp.tools.MemoryFileNames;

import be.kuleuven.noiseapp.R;
public class NoiseHuntPointsActivity extends Activity {
	RecordingPoints rp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_points);
		setupActionBar();
		
		rp = (RecordingPoints) getIntent().getSerializableExtra(MemoryFileNames.NOISEHUNT_POINTS);
		
		if(!rp.getBadges().isEmpty())
			showBadgePopup();
		
		createPointDescriptions();
	}

	private void showBadgePopup() {
		LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
	    View popupView = layoutInflater.inflate(R.layout.popup_badge, null);
	    
	    final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
	    
	    TextView txt_badge_desc = (TextView) popupView.findViewById(R.id.txt_badge_description);
	    txt_badge_desc.setText(rp.getBadges().get(0).getDescription());

	    TextView txt_badge = (TextView) popupView.findViewById(R.id.txt_badge);
	    txt_badge.setText(rp.getBadges().get(0).getName());

	    ImageView img_badge = (ImageView) popupView.findViewById(R.id.img_badge);
	    img_badge.setBackgroundResource(rp.getBadges().get(0).getImage());
	    
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
	    findViewById(R.id.layout_randomrecord_points).post(new Runnable() {
	    	   @Override
			public void run() {
	    		    popupWindow.showAtLocation(findViewById(R.id.layout_randomrecord_points), Gravity.CENTER_HORIZONTAL, 0, 0);
	    	   }
	    	});
	}

	private void createPointDescriptions() {
		ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
		for(Point p : rp.getPoints()){
			TableRow tr = new TableRow(this);
			TextView descriptionToAdd = new TextView(this);
			descriptionToAdd.setText(p.getDescription());
			descriptionToAdd.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			
			TextView pointToAdd = new TextView(this);
			pointToAdd.setText("+ " + p.getPoint());
			pointToAdd.setGravity(Gravity.RIGHT);
			pointToAdd.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1));
			
			tr.addView(descriptionToAdd);
			tr.addView(pointToAdd);
			
			tableRows.add(tr);
		}
		
		for(Badge b : rp.getBadges()){
			TableRow tr = new TableRow(this);
			TextView descriptionToAdd = new TextView(this);
			descriptionToAdd.setText(b.getDescription());descriptionToAdd.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			
			
			TextView pointToAdd = new TextView(this);
			pointToAdd.setText("+ " + b.getPoint());
			pointToAdd.setGravity(Gravity.RIGHT);
			pointToAdd.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1));
			
			tr.addView(descriptionToAdd);
			tr.addView(pointToAdd);

			tableRows.add(tr);
		}
		
		TableRow totalRow = new TableRow(this);
		TableLayout.LayoutParams trlp = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		trlp.setMargins(0, 15, 0, 0);
		totalRow.setLayoutParams(trlp);

		TextView descriptionToAdd = new TextView(this);
		descriptionToAdd.setText("Total:");
		descriptionToAdd.setTextSize(20);
		descriptionToAdd.setTypeface(null, Typeface.BOLD);
		descriptionToAdd.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		
		TextView pointToAdd = new TextView(this);
		pointToAdd.setText("+ " + rp.getTotalPoints());
		pointToAdd.setTextSize(20);
		pointToAdd.setTypeface(null, Typeface.BOLD);
		pointToAdd.setGravity(Gravity.RIGHT);
		pointToAdd.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1));
		
		totalRow.addView(descriptionToAdd);
		totalRow.addView(pointToAdd);
		
		tableRows.add(totalRow);
		
		TableLayout tbl_points = (TableLayout) this.findViewById(R.id.tbl_points);
		for(TableRow tr : tableRows)
			tbl_points.addView(tr);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(getIntent().getIntExtra(MemoryFileNames.NOISEHUNT_TITLE, 0));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.random_record_points, menu);
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
			//NavUtils.navigateUpFromSameTask(this);
			Intent homeIntent = new Intent(this, NoiseHuntActivity.class);
			NavUtils.navigateUpTo(this, homeIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
