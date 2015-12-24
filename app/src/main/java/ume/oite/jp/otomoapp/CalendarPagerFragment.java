package ume.oite.jp.otomoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * Created by Ume on 15/12/24.
 */
public class CalendarPagerFragment extends Fragment {

    protected Calendar curMonth;
    ViewPager pager;
    private int mSelectedPageIndex = 1;
    FragmentPagerAdapter adapter;

    private static final int PAGE_LEFT = 0;
    private static final int PAGE_MIDDLE = 1;
    private static final int PAGE_RIGHT = 2;

    public static Fragment newInstance(){
        return new CalendarPagerFragment();
    }

    public CalendarPagerFragment(){

    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.viewpager_calendar, container, false);

        final CalendarFragment[] fragList = new CalendarFragment[3];
        curMonth = Calendar.getInstance();
        Calendar prevMonth,nextMonth;
        prevMonth = (Calendar)curMonth.clone();
        nextMonth = (Calendar)curMonth.clone();
        prevMonth.add(Calendar.MONTH,-1);
        nextMonth.add(Calendar.MONTH,+1);
        fragList[0]=CalendarFragment.newInstance(prevMonth.get(Calendar.YEAR),prevMonth.get(Calendar.MONTH));
        fragList[1]=CalendarFragment.newInstance(curMonth.get(Calendar.YEAR),curMonth.get(Calendar.MONTH));
        fragList[2]=CalendarFragment.newInstance(nextMonth.get(Calendar.YEAR),nextMonth.get(Calendar.MONTH));

        pager = (ViewPager)layout.findViewById(R.id.calendarPager);
        adapter = new InfiniteCalendarPagerAdapter(this.getFragmentManager(),fragList);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state== ViewPager.SCROLL_STATE_IDLE){
                    if(mSelectedPageIndex<PAGE_MIDDLE){
                        fragList[0].onPreviousMonth();
                        fragList[1].onPreviousMonth();
                        fragList[2].onPreviousMonth();
                    }else if(mSelectedPageIndex>PAGE_MIDDLE){
                        fragList[0].onNextMonth();
                        fragList[1].onNextMonth();
                        fragList[2].onNextMonth();
                    }
                    pager.setCurrentItem(1,false);
                }
            }
        });


        pager.setCurrentItem(1,false);
        pager.setAdapter(adapter);

        return layout;
    }
}
