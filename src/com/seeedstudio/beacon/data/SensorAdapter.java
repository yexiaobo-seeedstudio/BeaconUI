package com.seeedstudio.beacon.data;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.seeedstudio.library.Atom;

public class SensorAdapter extends ArrayAdapter<Atom> {

    public SensorAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Atom object) {
        super.add(object);
    }

    @Override
    public Atom getItem(int position) {
        return super.getItem(position);
    }
}
