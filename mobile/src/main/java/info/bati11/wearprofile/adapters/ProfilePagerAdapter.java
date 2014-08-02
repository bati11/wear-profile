package info.bati11.wearprofile.adapters;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import info.bati11.wearprofile.fragments.InputFragment;
import info.bati11.wearprofile.fragments.LoadTwitterFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private InputFragment inputFragment;
    private LoadTwitterFragment loadTwitterFragment;

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment result;
        if (position == 0) {
            if (inputFragment == null) inputFragment = InputFragment.newInstance();
            result = inputFragment;
        } else {
            if (loadTwitterFragment == null) loadTwitterFragment = LoadTwitterFragment.newInstance();
            result = loadTwitterFragment;
        }
        return result;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;
        if (position == 0) title = "input";
        else              title = "twitter";
        return title;
    }

    public void setProfile(String name, String description) {
        inputFragment.setProfile(name, description);
    }

    public void setProfileImage(Bitmap bitmap) {
        inputFragment.setProfileImage(bitmap);
    }
}
