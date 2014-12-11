package com.oubeichen.weather;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class AlarmAdapter<T> extends ArrayAdapter<T> {

    public AlarmAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
    }
}
