package info.bati11.wearprofile.fragments;

import android.graphics.Bitmap;
import android.view.View;

public interface ProfileFragmentListener {
    public void onClickImage(View view);
    public void onClickSync(View view, String name, String description, Bitmap bitmap);
    public Bitmap getProfileImage();
}
