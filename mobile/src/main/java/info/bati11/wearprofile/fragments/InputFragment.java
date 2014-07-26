package info.bati11.wearprofile.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import info.bati11.wearprofile.R;

public class InputFragment extends android.support.v4.app.Fragment {

    private ProfileFragmentListener profileFragmentListener;

    private ImageButton imageButton;
    private ImageView imageView;
    private Button syncButton;

    public static InputFragment newInstance() {
        InputFragment fragment = new InputFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.profileFragmentListener = (ProfileFragmentListener)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        imageView = (ImageView)view.findViewById(R.id.profile_image);
        imageButton = (ImageButton)view.findViewById(R.id.image_button);
        syncButton = (Button)view.findViewById(R.id.sync_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragmentListener.onClickImage();
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragmentListener.onClickSync("", "", null);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bitmap bitmap = profileFragmentListener.getProfileImage();
        if (bitmap != null) imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        profileFragmentListener = null;
    }
}
