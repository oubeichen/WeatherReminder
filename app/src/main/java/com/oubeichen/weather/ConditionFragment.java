package com.oubeichen.weather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 在AddAlarmActivity中成批显示，每个Fragment代表一个提醒的条件
 */
public class ConditionFragment extends Fragment {

    private ArrayAdapter<String> condAdapter;
    private ArrayAdapter<String> dayAdapter;
    private ArrayAdapter<String> indexAdapter;
    private ArrayAdapter<String> qwAdapter;
    private ArrayAdapter<String> fsAdapter;
    private ArrayAdapter<String> cyAdapter;
    private ArrayAdapter<String> ydAdapter;
    private ArrayAdapter<String> xcAdapter;
    private ArrayAdapter<String> lsAdapter;
    private ArrayAdapter<String> tempAdapter;

    private Bundle mBundle;
    private static int count = 0;

    private Boolean autoSelect = true;
    private int number;
    
    public Spinner mCondType1;
    public Spinner mCondType2;
    public Spinner mCondType3;
    public Spinner mCondType4;
    private TextView mNameTextView;

    public static ConditionFragment newInstance(Bundle bundle) {
        bundle.putInt("count", count);
        ConditionFragment fragment = new ConditionFragment();
        fragment.setArguments(bundle);
        count++;
        return fragment;
    }
    
    public ConditionFragment(){
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mBundle = getArguments();
            this.number = mBundle.getInt("count");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initSpinners();

        LinearLayout layout_out = new LinearLayout(getActivity());
        LinearLayout layout_in = new LinearLayout(getActivity());
        layout_out.setOrientation(LinearLayout.VERTICAL);
        layout_in.setOrientation(LinearLayout.HORIZONTAL);
        layout_out.addView(mNameTextView);
        layout_out.addView(mCondType1);
        layout_out.addView(layout_in);
        layout_in.addView(mCondType2);
        layout_in.addView(mCondType3);
        layout_in.addView(mCondType4);

        return layout_out;
    }
    
    private void initSpinners() {
        String[] condTypes = getResources().getStringArray(R.array.condition_type);
        condAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, condTypes);
        String[] dayTypes = getResources().getStringArray(R.array.day_type);
        dayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, dayTypes);
        String[] indexTypes = getResources().getStringArray(R.array.index_type);
        indexAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, indexTypes);
        String[] qwTypes = getResources().getStringArray(R.array.qw_type);
        qwAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, qwTypes);
        String[] fsTypes = getResources().getStringArray(R.array.fs_type);
        fsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, fsTypes);
        String[] cyTypes = getResources().getStringArray(R.array.cy_type);
        cyAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, cyTypes);
        String[] ydTypes = getResources().getStringArray(R.array.yd_type);
        ydAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, ydTypes);
        String[] xcTypes = getResources().getStringArray(R.array.xc_type);
        xcAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, xcTypes);
        String[] lsTypes = getResources().getStringArray(R.array.ls_type);
        lsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, lsTypes);
        String[] tempVals = getResources().getStringArray(R.array.temp_val);
        tempAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, tempVals);

        mCondType1 = new Spinner(getActivity());
        mCondType2 = new Spinner(getActivity());
        mCondType3 = new Spinner(getActivity());
        mCondType4 = new Spinner(getActivity());

        mNameTextView = new TextView(getActivity());
        mNameTextView.setText("条件" + (number + 1));
        mCondType2.setTag("type1_cond" + number);
        mCondType3.setTag("type2_cond" + number);
        mCondType4.setTag("type3_cond" + number);

        //绑定 Adapter到控件
        mCondType1.setAdapter(condAdapter);
        mCondType2.setAdapter(qwAdapter);
        mCondType3.setAdapter(tempAdapter);
        mCondType4.setAdapter(dayAdapter);
        mCondType1.setSelection(0, true);
        mCondType1.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter,
                                       View view, int position, long id) {
                Log.i("condition", position + " " + id);

                Spinner tp1 = (Spinner) ((View) view.getParent().getParent()).findViewWithTag("type1_cond" + number);
                Spinner tp2 = (Spinner) ((View) view.getParent().getParent()).findViewWithTag("type2_cond" + number);
                Spinner tp3 = (Spinner) ((View) view.getParent().getParent()).findViewWithTag("type3_cond" + number);

                if (position < 2) {
                    tp1.setAdapter(qwAdapter);
                    tp3.setVisibility(Spinner.VISIBLE);
                } else {
                    tp1.setAdapter(indexAdapter);
                    tp3.setVisibility(Spinner.GONE);
                }
                switch (position) {
                    case 0:
                    case 1:
                        tp2.setAdapter(tempAdapter);
                        break;
                    case 2:
                        tp2.setAdapter(fsAdapter);
                        break;
                    case 3:
                        tp2.setAdapter(cyAdapter);
                        break;
                    case 4:
                        tp2.setAdapter(ydAdapter);
                        break;
                    case 5:
                        tp2.setAdapter(xcAdapter);
                        break;
                    case 6:
                        tp2.setAdapter(lsAdapter);
                        break;
                }
                if (autoSelect) { // 初始化放在这里....
                    mCondType2.setSelection(mBundle.getInt("opt2", 0), true);
                    mCondType3.setSelection(mBundle.getInt("opt3", 0), true);
                    mCondType4.setSelection(mBundle.getInt("opt4", 0), true);
                    autoSelect = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {

            }

        });

        if (getArguments() != null) {
            if (mBundle.getInt("opt1", -1) != -1) {
                mCondType1.setSelection(mBundle.getInt("opt1", 0), true);
                // 若这里不为0，则另外三个下拉框不能在这里初始化，因为onCreatView完成之后会触发一次OnItemSelected事件
                if(mCondType1.getSelectedItemPosition() == 0 && autoSelect) {
                    mCondType2.setSelection(mBundle.getInt("opt2", 0), true);
                    mCondType3.setSelection(mBundle.getInt("opt3", 0), true);
                    mCondType4.setSelection(mBundle.getInt("opt4", 0), true);
                    autoSelect = false;
                }
            } else {
                autoSelect = false;
            }
        }
    }

    public static void clean(){
        count = 0;
    }

    public static void del(){
        count--;
    }

    public static int getCount(){
        return count;
    }
}
