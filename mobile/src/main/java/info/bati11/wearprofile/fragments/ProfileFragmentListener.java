package info.bati11.wearprofile.fragments;

import android.graphics.Bitmap;

public interface ProfileFragmentListener {
    public void onClickImage();
    public void onClickSync(String name, String description, Bitmap bitmap);
    public Bitmap getProfileImage();
}
