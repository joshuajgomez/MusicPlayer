package com.joshgomez.musicplayer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.ResUtil;

import java.util.ArrayList;

public class StringAdapter extends BaseAdapter {

    private ArrayList<String> arrayList;
    private Context context;
    private int imageRes;

    public StringAdapter(ArrayList<String> arrayList, Context context, int imageRes) {
        this.arrayList = arrayList;
        this.context = context;
        this.imageRes = imageRes;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.string_list_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.title);
        ImageView imageView = convertView.findViewById(R.id.image);
        textView.setText(String.valueOf(getItem(position)));
        imageView.setImageResource(ResUtil.getResId(imageRes, context));
        return convertView;
    }
}
