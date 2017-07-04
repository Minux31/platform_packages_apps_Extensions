/*
 * Copyright (C) 2017 AospExtended rom
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
package org.aospextended.extensions;

import android.content.Context;
import java.util.List;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.PreferenceFragment;
import android.provider.Settings;
import java.util.ArrayList;
import com.android.internal.logging.nano.MetricsProto;
import android.provider.SearchIndexableResource;
import org.aospextended.extensions.preference.SystemSettingSwitchPreference; 
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import org.aospextended.extensions.BatteryLightPreference;


public class BatteryLightSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private ColorPickerPreference mLowColor;
    private ColorPickerPreference mMediumColor;
    private ColorPickerPreference mFullColor;
    private ColorPickerPreference mReallyFullColor;
    private SystemSettingSwitchPreference mLowBatteryBlinking;

    private static final String KEY_CATEGORY_GENERAL = "general_section";
    private static final String FAST_COLOR_PREF = "fast_color";
    private static final String FAST_CHARGING_LED_PREF = "fast_charging_led_enabled";
    private static final String KEY_CATEGORY_FAST_CHARGE = "fast_color_cat";
    private PreferenceCategory mColorCategory;
    private PreferenceGroup mColorPrefs;
    private SystemSettingSwitchPreference mFastBatteryLightEnabledPref;
    private BatteryLightPreference mFastColorPref;


private boolean mFastBatteryLightEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_light_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        mColorCategory = (PreferenceCategory) findPreference("battery_light_cat");

        mLowBatteryBlinking = (SystemSettingSwitchPreference)prefSet.findPreference("battery_light_low_blinking");
        if (getResources().getBoolean(
                        com.android.internal.R.bool.config_ledCanPulse)) {
            mLowBatteryBlinking.setChecked(Settings.System.getIntForUser(getContentResolver(),
                            Settings.System.BATTERY_LIGHT_LOW_BLINKING, 0, UserHandle.USER_CURRENT) == 1);
            mLowBatteryBlinking.setOnPreferenceChangeListener(this);
	        } else {
            prefSet.removePreference(mLowBatteryBlinking);
        }

        if (getResources().getBoolean(com.android.internal.R.bool.config_multiColorBatteryLed)) {
            int color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_LOW_COLOR, 0xFFFF0000,
                            UserHandle.USER_CURRENT);
            mLowColor = (ColorPickerPreference) findPreference("battery_light_low_color");
            mLowColor.setAlphaSliderEnabled(false);
            mLowColor.setNewPreviewColor(color);
            mLowColor.setOnPreferenceChangeListener(this);

            color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_MEDIUM_COLOR, 0xFFFFFF00,
                            UserHandle.USER_CURRENT);
            mMediumColor = (ColorPickerPreference) findPreference("battery_light_medium_color");
            mMediumColor.setAlphaSliderEnabled(false);
            mMediumColor.setNewPreviewColor(color);
            mMediumColor.setOnPreferenceChangeListener(this);

            color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_FULL_COLOR, 0xFF00FF00,
                            UserHandle.USER_CURRENT);
            mFullColor = (ColorPickerPreference) findPreference("battery_light_full_color");
            mFullColor.setAlphaSliderEnabled(false);
            mFullColor.setNewPreviewColor(color);
            mFullColor.setOnPreferenceChangeListener(this);

            color = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_REALLYFULL_COLOR, 0xFF00FF00,
                            UserHandle.USER_CURRENT);
            mReallyFullColor = (ColorPickerPreference) findPreference("battery_light_reallyfull_color");
            mReallyFullColor.setAlphaSliderEnabled(false);
            mReallyFullColor.setNewPreviewColor(color);
            mReallyFullColor.setOnPreferenceChangeListener(this);


            mFastBatteryLightEnabledPref = (SystemSettingSwitchPreference)prefSet.findPreference(FAST_CHARGING_LED_PREF);
             mFastColorPref = (BatteryLightPreference) prefSet.findPreference(FAST_COLOR_PREF);
            mFastColorPref.setOnPreferenceChangeListener(this);
             // Does the Device support fast charge ?
            if (!getResources().getBoolean(com.android.internal.R.bool.config_FastChargingLedSupported)) 
{
                prefSet.removePreference(prefSet.findPreference(KEY_CATEGORY_FAST_CHARGE));
            }


        } else {
            prefSet.removePreference(mColorCategory);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.EXTENSIONS;
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshDefault();
    }



private void refreshDefault() {
        ContentResolver resolver = getContentResolver();
        Resources res = getResources();


if (mFastColorPref != null) {
            int fastColor = Settings.System.getInt(resolver, Settings.System.FAST_BATTERY_LIGHT_COLOR,
                    res.getInteger(com.android.internal.R.integer.config_notificationsFastBatteryARGB));
            mFastColorPref.setColor(fastColor);
        }
}



/**
     * Updates the default or application specific notification settings.
     *
     * @param key of the specific setting to update
     * @param color
     */
    protected void updateValues(String key, Integer color) {
        ContentResolver resolver = getContentResolver();

if (key.equals(FAST_COLOR_PREF)) {
            Settings.System.putInt(resolver, Settings.System.FAST_BATTERY_LIGHT_COLOR, color);
        }
   }


protected void resetColors() {
        ContentResolver resolver = getActivity().getContentResolver();
        Resources res = getResources();

Settings.System.putInt(resolver, Settings.System.FAST_BATTERY_LIGHT_COLOR,
                res.getInteger(com.android.internal.R.integer.config_notificationsFastBatteryARGB));
refreshDefault();
}

   private void updateEnablement(boolean showOnlyWhenFull) { if 
(mFastBatteryLightEnabledPref != null) {
            mFastBatteryLightEnabledPref.setEnabled(!showOnlyWhenFull);
        }

}

    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (preference.equals(mLowColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_LOW_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mMediumColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_MEDIUM_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mFullColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_FULL_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mReallyFullColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.BATTERY_LIGHT_REALLYFULL_COLOR, color,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mLowBatteryBlinking) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.BATTERY_LIGHT_LOW_BLINKING, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mLowBatteryBlinking.setChecked(value);
            return true;
        }
        return false;
    }
public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.battery_light_settings;
                    result.add(sir);
                    return result;
     }

   @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    final Resources res = context.getResources();
                    if (!res.getBoolean(com.android.internal.R.bool.config_FastChargingLedSupported)) 
{
                        result.add(FAST_CHARGING_LED_PREF);
                        result.add(FAST_COLOR_PREF);
                    }
                    return result;
         }
     };
  }
