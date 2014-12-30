package com.oubeichen.weather;

import android.app.Activity;
import android.net.Uri;
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

import java.util.concurrent.locks.Condition;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link ConditionFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link ConditionFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class ConditionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String[] condTypes;
    private ArrayAdapter<String> condAdapter;
    private String[] dayTypes;
    private ArrayAdapter<String> dayAdapter;
    private String[] indexTypes;
    private ArrayAdapter<String> indexAdapter;
    private String[] qwTypes;
    private ArrayAdapter<String> qwAdapter;
    private String[] fsTypes;
    private ArrayAdapter<String> fsAdapter;
    private String[] cyTypes;
    private ArrayAdapter<String> cyAdapter;
    private String[] ydTypes;
    private ArrayAdapter<String> ydAdapter;
    private String[] xcTypes;
    private ArrayAdapter<String> xcAdapter;
    private String[] lsTypes;
    private ArrayAdapter<String> lsAdapter;
    private String[] tempVals;
    private ArrayAdapter<String> tempAdapter;

    private OnFragmentInteractionListener mListener;
    
    private static int count = 0;

    private int number;
    
    public Spinner cond_type;
    public Spinner cond_type1;
    public Spinner cond_type2;
    public Spinner cond_type3;

    public static ConditionFragment newInstance() {
        Bundle bundle = new Bundle();
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

        condTypes = getResources().getStringArray(R.array.condition_type);
        condAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, condTypes);
        dayTypes = getResources().getStringArray(R.array.day_type);
        dayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, dayTypes);
        indexTypes = getResources().getStringArray(R.array.index_type);
        indexAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, indexTypes);
        qwTypes = getResources().getStringArray(R.array.qw_type);
        qwAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, qwTypes);
        fsTypes = getResources().getStringArray(R.array.fs_type);
        fsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, fsTypes);
        cyTypes = getResources().getStringArray(R.array.cy_type);
        cyAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, cyTypes);
        ydTypes = getResources().getStringArray(R.array.yd_type);
        ydAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, ydTypes);
        xcTypes = getResources().getStringArray(R.array.xc_type);
        xcAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, xcTypes);
        lsTypes = getResources().getStringArray(R.array.ls_type);
        lsAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, lsTypes);
        tempVals = getResources().getStringArray(R.array.temp_val);
        tempAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, tempVals);

        cond_type = new Spinner(getActivity());
        cond_type1 = new Spinner(getActivity());
        cond_type2 = new Spinner(getActivity());
        cond_type3 = new Spinner(getActivity());

        if (getArguments() != null) {
            this.number = getArguments().getInt("count");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        TextView cond_text = new TextView(getActivity());
        cond_text.setText("条件" + (number + 1));
        cond_type1.setTag("type1_cond" + number);
        cond_type2.setTag("type2_cond" + number);
        cond_type3.setTag("type3_cond" + number);

        LinearLayout layout_out = new LinearLayout(getActivity());
        LinearLayout layout_in = new LinearLayout(getActivity());
        layout_out.setOrientation(LinearLayout.VERTICAL);
        layout_in.setOrientation(LinearLayout.HORIZONTAL);
        layout_out.addView(cond_text);
        layout_out.addView(cond_type);
        layout_out.addView(layout_in);
        layout_in.addView(cond_type1);
        layout_in.addView(cond_type2);
        layout_in.addView(cond_type3);

        //绑定 Adapter到控件
        cond_type.setAdapter(condAdapter);
        cond_type1.setAdapter(qwAdapter);
        cond_type2.setAdapter(tempAdapter);
        cond_type3.setAdapter(dayAdapter);
        cond_type.setOnItemSelectedListener(new OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapter,
                    View view, int position, long id) {
                Spinner tp1 = (Spinner)((View)view.getParent().getParent()).findViewWithTag("type1_cond" + number);
                Spinner tp2 = (Spinner)((View)view.getParent().getParent()).findViewWithTag("type2_cond" + number);
                Spinner tp3 = (Spinner)((View)view.getParent().getParent()).findViewWithTag("type3_cond" + number);

                if(position < 2){
                    tp1.setAdapter(qwAdapter);
                    tp3.setVisibility(Spinner.VISIBLE);
                } else {
                    tp1.setAdapter(indexAdapter);
                    tp3.setVisibility(Spinner.GONE);
                }
                switch(position){
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                
            }
            
        });
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            if (bundle.getInt("opt1", -1) != -1) {
                Log.i("condition", "set opts" + bundle.getInt("opt1"));
                cond_type.setSelection(bundle.getInt("opt1"));
                cond_type1.setSelection(bundle.getInt("opt2"));
                cond_type2.setSelection(bundle.getInt("opt3"));
                cond_type3.setSelection(bundle.getInt("opt4"));
            }
        }
        return layout_out;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated to
     * the activity and potentially other fragments contained in that activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
