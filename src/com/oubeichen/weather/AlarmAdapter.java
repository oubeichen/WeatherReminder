package com.oubeichen.weather;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class AlarmAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater mFactory;
    private final Context mContext;
    private final List<T> mList;
    private final List<Boolean> mEnabled;

    public AlarmAdapter(Context context, int resource, List<T> objects, List<Boolean> enabled) {
        super(context, resource, objects);
        mContext = context;
        mFactory = LayoutInflater.from(context);
        mList = objects;
        mEnabled = enabled;
        // TODO Auto-generated constructor stub
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newView(mContext, parent);
        } else {
            v = convertView;
        }

        return v;
    }
    private View newView(Context context, ViewGroup parent) {
        final View view = mFactory.inflate(R.layout.alarm_list_item, parent, false);
        //setNewHolder(view);
        return view;
    }
}
