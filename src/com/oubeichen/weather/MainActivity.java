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

import org.json.JSONException;
import org.json.JSONObject;

import com.oubeichen.weather.common.activities.SampleActivityBase;
import com.oubeichen.weather.common.logger.Log;
import com.oubeichen.weather.common.logger.LogFragment;
import com.oubeichen.weather.common.logger.LogWrapper;
import com.oubeichen.weather.common.logger.MessageOnlyLogFilter;

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
    private class DownloadTask extends AsyncTask<String, Void, String> {

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
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
            String weather = result.substring(result.indexOf("w['南京']=") + 9,
                    result.indexOf("}];") + 1);
            String time = result.substring(result.indexOf("add={") + 4, result.indexOf("};window") + 1);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(weather);
                // update tempature
                TextView temp_day = ((TextView) findViewById(R.id.temp_day));
                temp_day.setText(jsonObject.getString("t1") + "℃");
                temp_day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
                TextView temp_night = ((TextView) findViewById(R.id.temp_night));
                temp_night.setText(jsonObject.getString("t2") + "℃");
                temp_night.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
                
                // update weather
                ((TextView) findViewById(R.id.weather_day))
                        .setText(jsonObject.getString("s1"));
                ((TextView) findViewById(R.id.weather_night))
                        .setText(jsonObject.getString("s2"));
                
                //update wind
                ((TextView) findViewById(R.id.wind_day))
                        .setText(jsonObject.getString("d1") + jsonObject.getString("p1") + "级");
                ((TextView) findViewById(R.id.wind_night))
                        .setText(jsonObject.getString("d2") + jsonObject.getString("p2") + "级");
                
                jsonObject = new JSONObject(time);
                ((TextView) findViewById(R.id.update_time_text))
                        .setText(jsonObject.getString("update"));
            } catch (JSONException e) {
            }

        }
    }

    /** Initiates the fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream, 500);
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
        reader = new InputStreamReader(stream, "GBK");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
    
    public void refresh(View v){
        new DownloadTask().execute("http://php.weather.sina.com.cn/iframe/index/w_cl.php?code=js&day=0&city=%E5%8D%97%E4%BA%AC");
    }
}
