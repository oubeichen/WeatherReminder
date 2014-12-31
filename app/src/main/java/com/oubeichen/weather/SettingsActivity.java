package com.oubeichen.weather;

import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by oubeichen on 2014/12/31 0031.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
        setTitle(getResources().getString(R.string.settings_title));
    }

    public static class SettingFragment extends PreferenceFragment {

        public class PrefListener implements Preference.OnPreferenceChangeListener {

            public PrefListener(String key) {
                super();
                // 自动设置summary为读取的值，如果值为空则设置默认值
                Preference preference = findPreference(key);
                if (EditTextPreference.class.isInstance(preference)) {
                    // EditText
                    EditTextPreference etp = (EditTextPreference) preference;
                    if(etp.getText() != null) {
                        etp.setSummary(etp.getText());
                    } else {
                        etp.setText("");
                    }
                } else if (ListPreference.class.isInstance(preference)) {
                    // List
                    ListPreference lp = (ListPreference) preference;
                    if(lp.getValue() != null) {
                        lp.setSummary(lp.getEntry());
                    } else {
                        lp.setValueIndex(0);
                    }
                }
                // 监听设置值修改事件
                preference.setOnPreferenceChangeListener(this);
            }

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // 自动修改summary
                if (EditTextPreference.class.isInstance(preference)) {
                    // EditText
                    EditTextPreference etp = (EditTextPreference) preference;
                    if(etp.getText() != null) {
                        etp.setSummary(etp.getText());
                    }
                } else if (ListPreference.class.isInstance(preference)) {
                    // List
                    ListPreference lp = (ListPreference) preference;
                    if(lp.getValue() != null) {
                        lp.setSummary(lp.getEntries()[lp.findIndexOfValue(newValue.toString())]);
                    }
                }
                return true;
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.settings);

            // 下面做两个设置的监听
            new PrefListener("preference_city");
            new PrefListener("preference_refresh_interval");
        }
    }
}
