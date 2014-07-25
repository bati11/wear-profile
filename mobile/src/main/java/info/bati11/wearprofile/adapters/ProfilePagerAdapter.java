package info.bati11.wearprofile.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import info.bati11.wearprofile.fragments.InputFragment;
import info.bati11.wearprofile.fragments.LoadTwitterFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment result;
        if (position == 0) {
            result = InputFragment.newInstance();
        } else {
            result = LoadTwitterFragment.newInstance();
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
}
