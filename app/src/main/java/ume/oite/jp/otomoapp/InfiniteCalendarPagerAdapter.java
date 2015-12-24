package ume.oite.jp.otomoapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Ume on 15/12/24.
 */
public class InfiniteCalendarPagerAdapter extends FragmentPagerAdapter {

    CalendarFragment[] fragList;

    public InfiniteCalendarPagerAdapter(FragmentManager fm,CalendarFragment[] fragList){
        super(fm);
        this.fragList = fragList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragList[position];
    }

    @Override
    public int getCount() {
        return fragList.length;
    }

    @Override
    public CharSequence getPageTitle(int Position){
        return ""+Position;
    }
}
