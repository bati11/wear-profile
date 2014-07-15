package info.bati11.wearprofile.fragments;

import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.bati11.wearprofile.R;

public class ProfileFragment extends CardFragment {

    private TextView profileNameTextView;

    public static ProfileFragment newInstance(String profileName) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("profileName", profileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        profileNameTextView = (TextView)rootView.findViewById(R.id.profileName);
        profileNameTextView.setText(getArguments().getString("profileName"));
        return rootView;
    }

    public void changeProfileName(String profileName) {
        if (profileNameTextView != null) {
            profileNameTextView.setText(profileName);
        }
    }
}
