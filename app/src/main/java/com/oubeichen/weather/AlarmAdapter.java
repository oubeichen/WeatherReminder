package com.oubeichen.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private Context mContext;
    private final LayoutInflater mInflater;
    private AlarmAdapter thisAdapter = this;
    public AlarmAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
        holder.title.setText(AlarmManager.getAlarms().get(position).getName());
        holder.onoff.setChecked(AlarmManager.getAlarms().get(position).getEnabled());
        
        holder.onoff.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                    boolean checked) {
                AlarmManager.setEnabled(pos, checked);
                thisAdapter.notifyDataSetChanged();
            }
            
        });
        holder.delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String taskname = AlarmManager.getAlarms().get(pos).getName();
                AlertDialog alert = new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("确定要删除任务 " + taskname + " 么？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {// 设置确定按钮
                                    @Override
                                    // 处理确定按钮点击事件
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        AlarmManager.delAlarm(pos);
                                        thisAdapter.notifyDataSetChanged();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {// 设置取消按钮
                                    @Override
                                    // 取消按钮点击事件
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        dialog.cancel();
                                    }
                                }).create();
                alert.show();
            }
        });

        convertView.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddAlarmActivity.class);
                intent.putExtra("Count", pos);
                ((Activity)mContext).startActivityForResult(intent, MainActivity.ADD_ALARM_REQUEST);
            }
        });
        return convertView;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return AlarmManager.getAlarms().size();
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
