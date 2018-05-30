/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.settings.notification;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.RestrictedListPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class PeekSnoozeTimeController extends AbstractPreferenceController
        implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private static final String TAG = "PeekSnoozeTimeController";
    private static final String KEY_PEEK_SNOOZE_TIME = "heads_up_snooze_time";
    private int mDefaultValue;

    public PeekSnoozeTimeController(Context context) {
        super(context);
        Resources systemUiResources;
        try {
            systemUiResources = context.getPackageManager().getResourcesForApplication("com.android.systemui");
            mDefaultValue = systemUiResources.getInteger(systemUiResources.getIdentifier(
                    "com.android.systemui:integer/heads_up_default_snooze_length_ms", null, null));
        } catch (Exception e) {
        }
    }

    @Override
    public String getPreferenceKey() {
        return KEY_PEEK_SNOOZE_TIME;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        RestrictedListPreference pref = (RestrictedListPreference) preference;
        int snoozeTimeOut = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HEADS_UP_NOTIFICATION_SNOOZE, mDefaultValue);

        int valueIndex = pref.findIndexOfValue(String.valueOf(snoozeTimeOut));
        pref.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        // show user-configured value instead of default summary
        if (valueIndex >= 0) {
            pref.setSummary(pref.getEntry());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = (String) newValue;
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.HEADS_UP_NOTIFICATION_SNOOZE, Integer.valueOf(value));
        updateState(preference);
        return true;
    }
}
