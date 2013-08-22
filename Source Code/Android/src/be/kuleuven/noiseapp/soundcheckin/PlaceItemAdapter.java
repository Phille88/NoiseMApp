package be.kuleuven.noiseapp.soundcheckin;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import be.kuleuven.noiseapp.R;
public class PlaceItemAdapter extends ArrayAdapter<PlaceItem>{

    private ArrayList<PlaceItem> places;
    private Context context;

    public PlaceItemAdapter(Context context, int textViewResourceId, ArrayList<PlaceItem> places) {
        super(context, textViewResourceId, places);
        this.places = places;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_places, null);
        }
            
	    PlaceItem user = places.get(position);
	    if (user != null) {
            TextView username = (TextView) v.findViewById(R.id.txt_item_name);

        if (username != null) {
                username.setText(user.getName());
        }
    }
    return v;
    }
}