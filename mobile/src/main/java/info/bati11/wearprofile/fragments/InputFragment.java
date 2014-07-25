package info.bati11.wearprofile.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import info.bati11.wearprofile.R;

public class InputFragment extends android.support.v4.app.Fragment {

    private SyncButtonListener syncButtonListener;

    private Button syncButton;

    public static InputFragment newInstance() {
        Log.d("TAG", "InputFragment.newInstance");
        InputFragment fragment = new InputFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        syncButton = (Button)view.findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncButtonListener.exec("", "", null);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.syncButtonListener = (SyncButtonListener)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        syncButtonListener = null;
    }
}
