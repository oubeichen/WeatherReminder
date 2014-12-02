package com.oubeichen.weather;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.oubeichen.weather.ConditionFragment.OnFragmentInteractionListener;
import com.oubeichen.weather.common.logger.Log;

public class AddAlarmActivity extends FragmentActivity implements
        OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConditionFragment.clean();
        setContentView(R.layout.activity_add_alarm);
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
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
