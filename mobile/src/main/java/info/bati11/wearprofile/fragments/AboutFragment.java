package info.bati11.wearprofile.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import info.bati11.wearprofile.BuildConfig;
import info.bati11.wearprofile.R;

public class AboutFragment extends PreferenceFragment {

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }
    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_abount);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findPreference("version_name").setSummary(BuildConfig.VERSION_NAME);
    }
}
