package info.bati11.wearprofile.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

import info.bati11.wearprofile.fragments.ProfileFragment;

public class CardFragmentGridPagerAdapter extends FragmentGridPagerAdapter {
    private ProfileFragment nameFragment;
    private ProfileFragment descriptionFragment;

    private String name;
    private String description;

    public CardFragmentGridPagerAdapter(FragmentManager fm, String name, String description) {
        super(fm);
        this.name = name;
        this.description = description;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        CardFragment fragment = null;
        if (col == 0) {
            if (nameFragment == null) nameFragment = ProfileFragment.newInstance(name);
            fragment = nameFragment;
        } else if (col == 1) {
            if (descriptionFragment == null) descriptionFragment = ProfileFragment.newInstance(description);
            fragment = descriptionFragment;
        }
        if (fragment != null) {
            fragment.setCardGravity(Gravity.BOTTOM);
            fragment.setExpansionEnabled(true);
            fragment.setExpansionDirection(CardFragment.EXPAND_DOWN);
            fragment.setExpansionFactor(1.0f);
        }
        return fragment;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 2;
    }

    public int getEnableCardCount() {
        int result = 0;
        if (!"".equals(name)) result++;
        if (!"".equals(description)) result++;
        return result;
    }
}
