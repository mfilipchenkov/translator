package ru.yandex.mobilization.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.yandex.mobilization.activities.FavoritesFragment;
import ru.yandex.mobilization.activities.HistoryFragment;
import ru.yandex.mobilization.activities.TranslatorFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return HistoryFragment.newInstance();
            case 1: return TranslatorFragment.newInstance();
            case 2: return FavoritesFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
