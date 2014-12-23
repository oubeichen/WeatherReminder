package com.oubeichen.weather;

import com.oubeichen.weather.common.logger.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {

    public final class ViewHolder{
        public TextView title;
        public Switch onoff;
        public ImageView delete;
    }

    private final LayoutInflater mInflater;
    private AlarmAdapter thisAdapter = this;
    public AlarmAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;
        if (convertView == null) {
            holder=new ViewHolder(); 
            convertView = mInflater.inflate(R.layout.alarm_list_item, parent, false);
            holder.title = (TextView)convertView.findViewById(R.id.alarm_name);
            holder.onoff = (Switch)convertView.findViewById(R.id.alarm_switch);
            holder.delete = (ImageView)convertView.findViewById(R.id.button_delete);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.title.setText((String)AlarmManager.getTitle().get(position));
        holder.onoff.setChecked(AlarmManager.getEnabled().get(position));
        
        holder.onoff.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                    boolean checked) {
                AlarmManager.setEnabled(pos, checked);
                Log.i("AlarmAdapter", "set " + pos + " " + checked);
                thisAdapter.notifyDataSetChanged();
            }
            
        });
        holder.delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlarmManager.delAlarm(pos);
                thisAdapter.notifyDataSetChanged();
            }
        });
        return convertView;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return AlarmManager.getCount();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }
}
