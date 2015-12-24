package ume.oite.jp.otomoapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by Ume on 2015/07/12.
 */
public class CalendarPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<SparseArray<Integer>> dateList;

    static Fragment newInstance(){
        return new CalendarFragment();
    }

    public CalendarPagerAdapter(FragmentManager fm){
        super(fm);
        dateList = new ArrayList<SparseArray<Integer>>();
    }

    @Override
    public int getCount() {
        return dateList.size();
    }

    @Override
    public Fragment getItem(int position) {

        SparseArray<Integer> item = dateList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("page",position);
        bundle.putInt("year",item.get(0));
        bundle.putInt("month",item.get(1));

        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int Position){
        return dateList.get(Position).get(0)+" / "+ (dateList.get(Position).get(1)+1);
    }

    public void add(SparseArray<Integer> item){
        dateList.add(item);
    }

    public void addAll(ArrayList<SparseArray<Integer>> list){
        dateList.addAll(list);
    }

}
