package be.kuleuven.noiseapp.soundbattle;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SoundBattleItemAdapter extends ArrayAdapter<iSoundBattleListItem> {

	private LayoutInflater mInflater;
	
	public SoundBattleItemAdapter(Context context, int textViewResourceId, ArrayList<iSoundBattleListItem> battles){
		super(context, textViewResourceId, battles);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public enum RowType {
        LIST_OPEN_ITEM, LIST_PENDING_ITEM, LIST_FINISHED_ITEM, HEADER_ITEM
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		 return getItem(position).getView(mInflater, convertView);
    }
	
	@Override
    public int getViewTypeCount() {
        return RowType.values().length;
    } 
	
	@Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }
	
	@Override
	public boolean isEnabled(int position){
		if(getItem(position).getViewType() == RowType.HEADER_ITEM.ordinal() || getItem(position).getViewType() == RowType.LIST_PENDING_ITEM.ordinal())
			return false;		
		return true;
	}
}
