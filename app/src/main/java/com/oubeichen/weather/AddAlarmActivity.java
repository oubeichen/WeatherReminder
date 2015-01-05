package com.oubeichen.weather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddAlarmActivity extends FragmentActivity {

    private int mCount;
    
    private Alarm mAlarm;
    
    private TextView mNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConditionFragment.clean();
        setContentView(R.layout.activity_add_alarm);
        mNameTextView = (TextView)findViewById(R.id.alarm_name);

        mCount = getIntent().getIntExtra("Count", -1);
        
        if(mCount != -1) {
            mAlarm = AlarmManager.getAlarms().get(mCount);
            mNameTextView.setText(mAlarm.getName());
            for(Alarm.Cond cond : mAlarm.getConds()) {
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("opt1", cond.getOpt1());
                bundle.putInt("opt2", cond.getOpt2());
                bundle.putInt("opt3", cond.getOpt3());
                bundle.putInt("opt4", cond.getOpt4());
                Fragment frag = ConditionFragment.newInstance(bundle);
                transaction.add(R.id.condition_list, frag,
                        "Frag" + ConditionFragment.getCount());
                transaction.commit();
            }
            
        }
    }
    
    public void addConditionClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frag = ConditionFragment.newInstance(new Bundle());
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
    
    public void onOKClick(View view) {
        try {
            int fragCount = ConditionFragment.getCount();
            if(fragCount == 0) {
                Toast toast = Toast.makeText(this, "请添加至少一个条件", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if(mAlarm == null){
                mAlarm = new Alarm();
            }
            mAlarm.setEnabled(true);
            mAlarm.setName(mNameTextView.getText().toString());
            List<Alarm.Cond> conds = new ArrayList<Alarm.Cond>();
            mAlarm.setConds(conds);

            for(int i = 1;i <= fragCount;i++) {
                ConditionFragment frag = (ConditionFragment)getSupportFragmentManager()
                        .findFragmentByTag("Frag" + i);
                Alarm.Cond cond = mAlarm.new Cond();
                cond.setOpt1(frag.mCondType1.getSelectedItemPosition());
                cond.setOpt2(frag.mCondType2.getSelectedItemPosition());
                cond.setOpt3(frag.mCondType3.getSelectedItemPosition());
                cond.setOpt4(frag.mCondType4.getSelectedItemPosition());
                conds.add(cond);
            }
            AlarmManager.addOrEditAlarm(mAlarm);
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
