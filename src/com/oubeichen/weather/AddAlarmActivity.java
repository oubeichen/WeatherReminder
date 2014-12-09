package com.oubeichen.weather;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.oubeichen.weather.ConditionFragment.OnFragmentInteractionListener;
import com.oubeichen.weather.common.logger.Log;

public class AddAlarmActivity extends FragmentActivity implements
        OnFragmentInteractionListener{

    private int count;

    private String STORAGE_FILENAME = "AlarmStorage.json";
    
    private TextView mNameTextView;
    
    private JSONObject root;
    private JSONArray alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConditionFragment.clean();
        setContentView(R.layout.activity_add_alarm);
        mNameTextView = (TextView)findViewById(R.id.alarm_name);

        count = getIntent().getIntExtra("Count", -1);

        byte[] bbuf = new byte[100000];
        try {
            // 读存储
            FileInputStream fin = openFileInput(STORAGE_FILENAME);
            int len = fin.read(bbuf);
            fin.close();
            root = new JSONObject(new String(bbuf, 0, len));
            alarms = root.getJSONArray("alarms");
        } catch (Exception ex) {
            root = new JSONObject();
            alarms = new JSONArray();
            try {
                root.put("alarms", alarms);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            }
        }
    }
    
    public void addConditionClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frag = ConditionFragment.newInstance();
        transaction.add(R.id.condition_list, frag,
                "Frag" + ConditionFragment.getCount());
        Log.i("Fragment", "Add Frag" + ConditionFragment.getCount());
        transaction.commit();
    }

    public void delConditionClick(View view) {
        if(ConditionFragment.getCount() == 0){
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frag = getSupportFragmentManager()
                .findFragmentByTag("Frag" + ConditionFragment.getCount());
        Log.i("Fragment", "Del Frag" + ConditionFragment.getCount());
        transaction.remove(frag);
        transaction.commit();
        ConditionFragment.del();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO Auto-generated method stub
        
    }
    
    public void onOKClick(View view) {
        try {
            JSONObject thisalarm;
            if(count == -1){
                thisalarm = new JSONObject();
            } else {
                thisalarm = alarms.getJSONObject(count);
            }
            alarms.put(thisalarm);
            JSONArray conds = new JSONArray();
            thisalarm.put("name", mNameTextView.getText());
            thisalarm.put("conds", conds);

            List<Fragment> frags = getSupportFragmentManager().getFragments();
            Iterator<Fragment> it = frags.iterator();
            while (it.hasNext()) {
                ConditionFragment frag = (ConditionFragment) it.next();
                JSONObject cond = new JSONObject();
                cond.put("opt1", frag.cond_type.getSelectedItemPosition());
                cond.put("opt2", frag.cond_type1.getSelectedItemPosition());
                cond.put("opt3", frag.cond_type2.getSelectedItemPosition());
                cond.put("opt4", frag.cond_type3.getSelectedItemPosition());
                conds.put(cond);
            }

            // 写存储
            FileOutputStream fout = openFileOutput(STORAGE_FILENAME, Context.MODE_PRIVATE);
            fout.write(root.toString().getBytes());
            fout.close();
            setResult(RESULT_OK);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
