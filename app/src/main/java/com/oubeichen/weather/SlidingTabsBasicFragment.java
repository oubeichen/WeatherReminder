/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oubeichen.weather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oubeichen.weather.common.view.SlidingTabLayout;

import java.util.List;

/**
 * 管理viewpager的Fragment
 */
public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    public static final String PREFS_NAME = "WeatherStorage";

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 1:
                    return getResources().getString(R.string.clothes_pager_title);
                case 2:
                    return getResources().getString(R.string.reminder_pager_title);
                default:
                    return getResources().getString(R.string.weather_pager_title);
            }
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            // Inflate a new layout from our resources
            if(position == 0){
                view = getActivity().getLayoutInflater().inflate(R.layout.weather_pager,
                        container, false);
                SharedPreferences storage = getActivity()
                        .getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
                if(WeatherManager.loadWeather(storage)){
                    updateWeatherView(view);
                }
            } else if(position == 1) {
                view = getActivity().getLayoutInflater().inflate(R.layout.clothes_pager,
                        container, false);
                SharedPreferences storage = getActivity()
                        .getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
                if(WeatherManager.loadWeather(storage)){
                    updateClothesView(view);
                }
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.remind_pager,
                        container, false);
                updateAlarmsView(view);
            }
            // Add the newly created View to the ViewPager
            container.addView(view);

            Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

            // Return the View
            return view;
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

        @Override
        public int getItemPosition(Object object) {
         return POSITION_NONE;
        }
    }
    
    public ViewPager getViewPager(){
        return mViewPager;
    }
    
    public static void updateWeatherView(View view){
        // update realtime start
        TextView temp_day = ((TextView) view.findViewById(R.id.temp_day));
        if(temp_day == null){
            Log.i(LOG_TAG, "textview is null!");
            return;
        }
        temp_day.setText(WeatherManager.temp_today);
        temp_day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        // update weather
        int drawableid;
        drawableid = Utils.getDrawable(WeatherManager.weather_today);
        ((ImageView) view.findViewById(R.id.image_day))
                .setImageResource(drawableid);
        
        ((TextView) view.findViewById(R.id.weather_day))
                .setText(WeatherManager.weather_today);
        // update wind
        ((TextView) view.findViewById(R.id.wind_day))
                .setText(WeatherManager.wind_direction_today
                        + WeatherManager.wind_scale_today);

        // update date and time
        ((TextView) view.findViewById(R.id.date_text))
                .setText(WeatherManager.date_text);
        ((TextView) view.findViewById(R.id.update_time_text))
                .setText("更新时间：" + WeatherManager.update_time);
        // update realtime end

        //show cityname
        ((TextView) view.findViewById(R.id.cityname_text))
                .setText(WeatherManager.cityname);

        // update forecast
        drawableid = Utils.getDrawable(WeatherManager.weather_day[1]);
        ((ImageView) view.findViewById(R.id.image_day1))
                .setImageResource(drawableid);
        ((TextView) view.findViewById(R.id.temp_day1))
                .setText(WeatherManager.temp_day[1]);
        ((TextView) view.findViewById(R.id.weather_day1))
                .setText(WeatherManager.weather_day[1]);
        ((TextView) view.findViewById(R.id.date1))
                .setText(WeatherManager.date_day[1]);
        // day1
        drawableid = Utils.getDrawable(WeatherManager.weather_day[2]);
        ((ImageView) view.findViewById(R.id.image_day2))
                .setImageResource(drawableid);
        ((TextView) view.findViewById(R.id.temp_day2))
                .setText(WeatherManager.temp_day[2]);
        ((TextView) view.findViewById(R.id.weather_day2))
                .setText(WeatherManager.weather_day[2]);
        ((TextView) view.findViewById(R.id.date2))
                .setText(WeatherManager.date_day[2]);
        // day2
        drawableid = Utils.getDrawable(WeatherManager.weather_day[3]);
        ((ImageView) view.findViewById(R.id.image_day3))
                .setImageResource(drawableid);
        ((TextView) view.findViewById(R.id.temp_day3))
                .setText(WeatherManager.temp_day[3]);
        ((TextView) view.findViewById(R.id.weather_day3))
                .setText(WeatherManager.weather_day[3]);
        ((TextView) view.findViewById(R.id.date3))
                .setText(WeatherManager.date_day[3]);
        // day3
        drawableid = Utils.getDrawable(WeatherManager.weather_day[4]);
        ((ImageView) view.findViewById(R.id.image_day4))
                .setImageResource(drawableid);
        ((TextView) view.findViewById(R.id.temp_day4))
                .setText(WeatherManager.temp_day[4]);
        ((TextView) view.findViewById(R.id.weather_day4))
                .setText(WeatherManager.weather_day[4]);
        ((TextView) view.findViewById(R.id.date4))
                .setText(WeatherManager.date_day[4]);
        // day4
        drawableid = Utils.getDrawable(WeatherManager.weather_day[5]);
        ((ImageView) view.findViewById(R.id.image_day5))
                .setImageResource(drawableid);
        ((TextView) view.findViewById(R.id.temp_day5))
                .setText(WeatherManager.temp_day[5]);
        ((TextView) view.findViewById(R.id.weather_day5))
                .setText(WeatherManager.weather_day[5]);
        ((TextView) view.findViewById(R.id.date5))
                .setText(WeatherManager.date_day[5]);
    }
    
    public static void updateClothesView(View view){
        // update living index start
        // index1
        TextView living_name1 = ((TextView) view.findViewById(R.id.living_name1));
        if(living_name1 == null){
            Log.i(LOG_TAG, "textview is null!");
            return;
        }
        living_name1.setText(WeatherManager.living_name[0]);
        //living_name1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        ((TextView) view.findViewById(R.id.living_index1))
                .setText(WeatherManager.living_index[0]);
        ((TextView) view.findViewById(R.id.living_description1))
                .setText(WeatherManager.living_des[0]);
        // index2
        ((TextView) view.findViewById(R.id.living_name2))
                .setText(WeatherManager.living_name[1]);
        ((TextView) view.findViewById(R.id.living_index2))
                .setText(WeatherManager.living_index[1]);
        ((TextView) view.findViewById(R.id.living_description2))
                .setText(WeatherManager.living_des[1]);
        // index3
        ((TextView) view.findViewById(R.id.living_name3))
                .setText(WeatherManager.living_name[2]);
        ((TextView) view.findViewById(R.id.living_index3))
                .setText(WeatherManager.living_index[2]);
        ((TextView) view.findViewById(R.id.living_description3))
                .setText(WeatherManager.living_des[2]);
        // index4
        ((TextView) view.findViewById(R.id.living_name4))
                .setText(WeatherManager.living_name[3]);
        ((TextView) view.findViewById(R.id.living_index4))
                .setText(WeatherManager.living_index[3]);
        ((TextView) view.findViewById(R.id.living_description4))
                .setText(WeatherManager.living_des[3]);
        // index5
        ((TextView) view.findViewById(R.id.living_name5))
                .setText(WeatherManager.living_name[4]);
        ((TextView) view.findViewById(R.id.living_index5))
                .setText(WeatherManager.living_index[4]);
        ((TextView) view.findViewById(R.id.living_description5))
                .setText(WeatherManager.living_des[4]);
        // update living index end
    }
    private void updateAlarmsView(View view) {
        try {
            // 读存储
            ListView listView = (ListView)view.findViewById(R.id.remind_list);
            List<Alarm> list = AlarmManager.loadAlarm();
            AlarmAdapter adapter = new AlarmAdapter(getActivity());
            listView.setAdapter(adapter);

            if(list.size() > 0) {
                ((TextView)view.findViewById(R.id.remind_empty_view))
                    .setVisibility(TextView.GONE);
            }
        } catch (Exception ex) {
        }
    }
}
