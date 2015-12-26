package ume.oite.jp.otomoapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class CalendarFragment extends Fragment{

    private View calendarLayout = null;
    private Calendar calendar = Calendar.getInstance();
    private int year = 0,month = 0;

    private ViewGroup dateGroup;

    TextView info = null;

    final static String YEAR_PARAM = "YEAR";
    final static String MONTH_PARAM = "MONTH";

    public static CalendarFragment newInstance(int year,int month){
        CalendarFragment fragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(YEAR_PARAM,year);
        bundle.putInt(MONTH_PARAM,month);
        fragment.setArguments(bundle);
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        year = bundle.getInt(YEAR_PARAM);
        month = bundle.getInt(MONTH_PARAM);

        calendarLayout = inflater.inflate(R.layout.fragment_calendar, container, false);

        info = (TextView)calendarLayout.findViewById(R.id.calendarInfo);

        makeCalendar();

        return calendarLayout;
    }

    public void makeCalendar(){

        calendar.clear();
        calendar.set(year, month, 1);

        info.setText(year + " / " + (month+1));

        calendar.add(Calendar.DATE, -(calendar.get(Calendar.DAY_OF_WEEK) - 1));

        ViewGroup[] weeks = new ViewGroup[6];
        weeks[0] = (ViewGroup) calendarLayout.findViewById(R.id.week1);
        weeks[1] = (ViewGroup) calendarLayout.findViewById(R.id.week2);
        weeks[2] = (ViewGroup) calendarLayout.findViewById(R.id.week3);
        weeks[3] = (ViewGroup) calendarLayout.findViewById(R.id.week4);
        weeks[4] = (ViewGroup) calendarLayout.findViewById(R.id.week5);
        weeks[5] = (ViewGroup) calendarLayout.findViewById(R.id.week6);

        for(ViewGroup week : weeks){

            /*if(calendar.get(Calendar.MONTH)>month && week==weeks[5]){
                ((LinearLayout)calendarLayout).removeView(week);
                break;
            }*/

            for(int i=0;i<week.getChildCount();i++){

               dateGroup = (ViewGroup)week.getChildAt(i);
                final TextView dateView = (TextView)dateGroup.getChildAt(0);

                dateView.setText(String.valueOf(calendar.get(Calendar.DATE)));
                if(this.isToday(calendar)==true)dateGroup.setBackgroundResource(R.drawable.background_shape_today);
                else dateGroup.setBackgroundResource(R.drawable.background_shape_default);
                if(calendar.get(Calendar.MONTH)!=month)dateGroup.setBackgroundResource(R.drawable.background_shape_other);

                /*SQLiteDatabase db = helper.getReadableDatabase();
                Cursor c = db.rawQuery("select schedule from Sample where BeginTime = '" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE) + "-12-00-00'", null);
                c.moveToFirst();
                int index = 1;
                while(c.moveToNext()){
                    TextView t = (TextView) group.getChildAt(index);
                    t.setText(c.getString(0));
                    t.setBackgroundColor(RandomColor());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(4,0,4,0);
                    t.setLayoutParams(lp);
                    index ++ ;
                    if(index>4)break;
                }
                db.close();*/

                dateGroup.setOnTouchListener(new View.OnTouchListener() {
                    int date=calendar.get(Calendar.DATE);
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (v.isFocused() == false) {
                            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                                v.requestFocus();
                            }
                        } else {
                            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                                ((MainActivity)getActivity()).moveCalendar2Portal(year,month,date);
                            }
                        }
                        return true;
                    }

                    /*
                    int y = calendar.get(Calendar.YEAR);
                    int m = calendar.get(Calendar.MONTH);
                    int d = calendar.get(Calendar.DATE);
                    Fragment f = fragment;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (v.isFocused() == false) {
                            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                                v.requestFocus();
                            }
                        } else {
                            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                                Calendar c = Calendar.getInstance();
                                c.set(y, m, d);
                            }
                        }
                        return true;
                    }
                    */
                });


                calendar.add(Calendar.DATE, +1);
            }
        }
    }

    private boolean isToday(Calendar c) {
        Calendar today = Calendar.getInstance();
        if(c.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            if(c.get(Calendar.MONTH) == today.get(Calendar.MONTH)){
                if(c.get(Calendar.DATE) == today.get(Calendar.DATE)){
                    return true;
                }
            }
        }
        return false;
    }

    public Calendar getCalendar(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        return c;
    }

    protected final void onNextMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.add(Calendar.MONTH, 1);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        makeCalendar();
    }

    protected final void onPreviousMonth(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.add(Calendar.MONTH,-1);
        year =  c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        makeCalendar();
    }

}