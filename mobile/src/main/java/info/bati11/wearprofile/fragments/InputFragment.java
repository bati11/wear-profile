package info.bati11.wearprofile.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import info.bati11.wearprofile.R;

public class InputFragment extends android.support.v4.app.Fragment {

    private ProfileFragmentListener profileFragmentListener;

    private EditText nameEditTextView;
    private EditText descriptionEditTextView;

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
        nameEditTextView = (EditText)view.findViewById(R.id.name_edit_text);
        descriptionEditTextView = (EditText)view.findViewById(R.id.description_edit_text);
        imageView = (ImageView)view.findViewById(R.id.profile_image);
        imageButton = (ImageButton)view.findViewById(R.id.image_button);
        syncButton = (Button)view.findViewById(R.id.sync_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragmentListener.onClickImage(view);
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragmentListener.onClickSync(
                        view,
                        nameEditTextView.getText().toString(),
                        descriptionEditTextView.getText().toString(),
                        ((BitmapDrawable)imageView.getDrawable()).getBitmap());
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

    public void setProfile(String name, String description) {
        nameEditTextView.setText(name);
        descriptionEditTextView.setText(description);
    }

    public void setProfileImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
