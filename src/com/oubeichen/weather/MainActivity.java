/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.oubeichen.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import com.oubeichen.weather.common.activities.SampleActivityBase;
import com.oubeichen.weather.common.logger.Log;
import com.oubeichen.weather.common.logger.LogFragment;
import com.oubeichen.weather.common.logger.LogWrapper;
import com.oubeichen.weather.common.logger.MessageOnlyLogFilter;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";
    
    public static final String SOURCE_URL = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=101190101";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }
    
    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class RefreshWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadFromNetwork(urls[0]);
            } catch (IOException e) {
              return getString(R.string.connection_error);
            }
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(String result) {
            //Log.i(TAG, result);
            String weather = result;
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(weather);
                // update today start
                JSONObject realtime = jsonObject.getJSONObject("realtime");
                
                TextView temp_day = ((TextView) findViewById(R.id.temp_day));
                temp_day.setText(realtime.getString("temp") + "℃");
                temp_day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
                // update weather
                ((TextView) findViewById(R.id.weather_day))
                        .setText(realtime.getString("weather"));
                //update wind
                ((TextView) findViewById(R.id.wind_day))
                        .setText(realtime.getString("WD") + realtime.getString("WS"));
                
                ((TextView) findViewById(R.id.update_time_text))
                        .setText(realtime.getString("time"));
                // update today end
                // update other days
                JSONObject forecast = jsonObject.getJSONObject("forecast");
                JSONObject today = jsonObject.getJSONObject("today");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
                SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
                // today
                Date date = sdf.parse(today.getString("date"));
                
                ((TextView) findViewById(R.id.temp_day1))
                        .setText(forecast.getString("temp1"));
                ((TextView) findViewById(R.id.weather_day1))
                        .setText(forecast.getString("weather1"));
                ((TextView) findViewById(R.id.date1))
                        .setText(sdf2.format(date));
                // day1
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                ((TextView) findViewById(R.id.temp_day2))
                        .setText(forecast.getString("temp2"));
                ((TextView) findViewById(R.id.weather_day2))
                        .setText(forecast.getString("weather2"));
                ((TextView) findViewById(R.id.date2))
                        .setText(sdf2.format(calendar.getTime()));
                // day2
                calendar.add(Calendar.DATE, 1);
                ((TextView) findViewById(R.id.temp_day3))
                        .setText(forecast.getString("temp3"));
                ((TextView) findViewById(R.id.weather_day3))
                        .setText(forecast.getString("weather3"));
                ((TextView) findViewById(R.id.date3))
                        .setText(sdf2.format(calendar.getTime()));
                // day3
                calendar.add(Calendar.DATE, 1);
                ((TextView) findViewById(R.id.temp_day4))
                        .setText(forecast.getString("temp4"));
                ((TextView) findViewById(R.id.weather_day4))
                        .setText(forecast.getString("weather4"));
                ((TextView) findViewById(R.id.date4))
                        .setText(sdf2.format(calendar.getTime()));
                // day4
                calendar.add(Calendar.DATE, 1);
                ((TextView) findViewById(R.id.temp_day5))
                        .setText(forecast.getString("temp5"));
                ((TextView) findViewById(R.id.weather_day5))
                        .setText(forecast.getString("weather5"));
                ((TextView) findViewById(R.id.date5))
                        .setText(sdf2.format(calendar.getTime()));

            } catch (JSONException e) {
            } catch (ParseException e) {
            }
        }
    }

    /** Initiates the fetch operation. */
    private String loadFromNetwork(String url) throws IOException {
        InputStream stream = null;
        String str = null;
        try {
            stream = downloadUrl(url);
            str = readIt(stream, 50000);//500KB

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
        // END_INCLUDE(get_inputstream)
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @param len Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
    
    public void refresh(View v){
        new RefreshWeatherTask().execute(SOURCE_URL);
    }
}
