package com.seeedstudio.beacon.ui.tutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.seeedstudio.beacon.ui.R;

public class TutorAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    // tutor image array
    private static final int[] tutorImages = { R.drawable.tutor01,
            R.drawable.tutor02, R.drawable.tutor03, R.drawable.tutor04,
            R.drawable.tutor05 };

    public TutorAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    };

    @Override
    public int getCount() {
        return tutorImages.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tutor_image_item, null);
        }
        ((ImageView) convertView.findViewById(R.id.imgView))
                .setImageResource(tutorImages[position]);
        return convertView;
    }

}
