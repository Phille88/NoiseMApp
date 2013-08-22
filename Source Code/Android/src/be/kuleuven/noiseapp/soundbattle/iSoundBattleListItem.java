package be.kuleuven.noiseapp.soundbattle;

import android.view.LayoutInflater;
import android.view.View;

public interface iSoundBattleListItem {
	public int getViewType();
	public View getView(LayoutInflater inflater, View convertView);
}
