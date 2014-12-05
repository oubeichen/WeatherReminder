package com.oubeichen.weather;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
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

    private String PREF_BASE = "alarm";
    
    private TextView mNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConditionFragment.clean();
        setContentView(R.layout.activity_add_alarm);
        mNameTextView = (TextView)findViewById(R.id.alarm_name);

        count = getIntent().getIntExtra("Count", 1);
    }
    
    public void addConditionClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment frag = ConditionFragment.newInstance("aaa", "bbb");
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

        List<Fragment> frags = getSupportFragmentManager().getFragments();
        Iterator<Fragment> it = frags.iterator();
        int i = 1;
        SharedPreferences storage = getSharedPreferences(PREF_BASE + count, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = storage.edit();
        editor.clear().commit();
        while(it.hasNext()){
            ConditionFragment frag = (ConditionFragment)it.next();
            editor.putInt("cond_" + i, frag.cond_type.getSelectedItemPosition());
            editor.putInt("cond1_" + i, frag.cond_type1.getSelectedItemPosition());
            editor.putInt("cond2_" + i, frag.cond_type2.getSelectedItemPosition());
            editor.putInt("cond3_" + i, frag.cond_type3.getSelectedItemPosition());
            i++;
        }
        editor.putInt("count", i);
        editor.putString("name", mNameTextView.getText().toString());
        editor.commit();
        setResult(RESULT_OK);
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
