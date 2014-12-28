package com.oubeichen.weather;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oubeichen.weather.ConditionFragment.OnFragmentInteractionListener;

public class AddAlarmActivity extends FragmentActivity implements
        OnFragmentInteractionListener{

    private int mCount;
    
    private TextView mNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConditionFragment.clean();
        setContentView(R.layout.activity_add_alarm);
        mNameTextView = (TextView)findViewById(R.id.alarm_name);

        mCount = getIntent().getIntExtra("Count", -1);
    }
    
    public void addConditionClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frag = ConditionFragment.newInstance();
        transaction.add(R.id.condition_list, frag,
                "Frag" + ConditionFragment.getCount());
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
            int fragCount = ConditionFragment.getCount();
            if(fragCount == 0) {
                Toast toast = Toast.makeText(this, "请添加至少一个条件", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            Alarm alarm = new Alarm();
            alarm.setEnabled(true);
            alarm.setName(mNameTextView.getText().toString());
            List<Alarm.Cond> conds = new ArrayList<Alarm.Cond>();
            alarm.setConds(conds);

            for(int i = 1;i <= fragCount;i++) {
                ConditionFragment frag = (ConditionFragment)getSupportFragmentManager()
                        .findFragmentByTag("Frag" + i);
                Alarm.Cond cond = alarm.new Cond();
                cond.setOpt1(frag.cond_type.getSelectedItemPosition());
                cond.setOpt2(frag.cond_type1.getSelectedItemPosition());
                cond.setOpt3(frag.cond_type2.getSelectedItemPosition());
                cond.setOpt4(frag.cond_type3.getSelectedItemPosition());
                conds.add(cond);
            }
            AlarmManager.addOrEditAlarm(alarm);
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
