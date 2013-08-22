package be.kuleuven.noiseapp.soundbattle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import be.kuleuven.noiseapp.R;
public class SoundBattleItem implements iSoundBattleListItem {
	

	private String opponentName;
	private int finishedLocations;
	private long soundBattleID;
	private int viewType;
	
	public SoundBattleItem(long soundBattleID, String opponentName, int finishedLocations, int viewType){
		this.soundBattleID = soundBattleID;
		this.opponentName = opponentName;
		this.finishedLocations = finishedLocations;
		this.viewType = viewType;
	}

	/**
	 * @return the opponentName
	 */
	public String getOpponentName() {
		return opponentName;
	}

	/**
	 * @param opponentName the opponentName to set
	 */
	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}

	/**
	 * @return the finishedLocations
	 */
	public int getFinishedLocations() {
		return finishedLocations;
	}

	/**
	 * @param finishedLocations the finishedLocations to set
	 */
	public void setFinishedLocations(int finishedLocations) {
		this.finishedLocations = finishedLocations;
	}

	/**
	 * @return the soundBattleID
	 */
	public long getSoundBattleID() {
		return soundBattleID;
	}

	/**
	 * @param soundBattleID the soundBattleID to set
	 */
	public void setSoundBattleID(long soundBattleID) {
		this.soundBattleID = soundBattleID;
	}
	
	@Override
	public int getViewType() {
       return viewType;
    }
	 
	 @Override
	public View getView(LayoutInflater inflater, View convertView) {
	    View v = convertView;
	    if (v == null) {
	        v = inflater.inflate(R.layout.list_item_soundbattles, null);
	    }
	           
	    TextView opponentName = (TextView) v.findViewById(R.id.txt_item_opponentName);
	    TextView finishedLocations = (TextView) v.findViewById(R.id.txt_item_finishedLocations);
	    if (opponentName != null) {
	    	opponentName.setText(getOpponentName());
	    }
	    if (finishedLocations != null && getFinishedLocations() != 3) {
	        finishedLocations.setText(getFinishedLocations() + " out of 3 recordings");
	    }
	    return v;
}
}