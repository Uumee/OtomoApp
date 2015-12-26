package ume.oite.jp.otomoapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Calendar;

/**
 * Created by Ume on 15/12/24.
 */
public class PortalFragment extends Fragment {

    FloatingActionButton addFab,listenFab;
    DateView dateView ;
    PortalFragment fragment ;
    ListView list;
    DatabaseHelper helper;

    public static PortalFragment newInstance(){
        return new PortalFragment();
    }

    public PortalFragment(){
        fragment = this;
    }

    void setCalendar(Calendar c){
        dateView.setCalendar(c);
        ScheduleUpdate();
    }

    void ScheduleUpdate(){

        int year = dateView.getCalendar().get(Calendar.YEAR);
        int month = dateView.getCalendar().get(Calendar.MONTH);
        int date = dateView.getCalendar().get(Calendar.DATE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(),R.layout.list_item);
        helper=new DatabaseHelper(this.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from Sample where BeginTime = ' "+year+"-"+month+"-"+date+"-12-00-00 '",null);
        if(c.getCount()>0) {
            c.moveToFirst();
            do {
                arrayAdapter.add(c.getString(1));
                Log.d("d",c.getString(1));
            } while (c.moveToNext());
            list.setAdapter(arrayAdapter);
        }

        db.close();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_portal, container, false);

        dateView = (DateView)layout.findViewById(R.id.dateView);
        dateView.setOnTouchListener(new View.OnTouchListener(){

                float lastTouchX, lastTouchY, currentX, currentY;
                float intervalX = 50, intervalY = 100;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        lastTouchX = event.getX();
                        lastTouchY = event.getY();
                        v.setBackgroundColor(Color.GRAY);
                    }
                    if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                        currentX = event.getX();
                        currentY = event.getY();

                        if (currentY - lastTouchY > intervalY) {
                            lastTouchY = currentY;
                            dateView.addDate(+1);
                            ScheduleUpdate();
                        }
                        if (currentY - lastTouchY < -intervalY) {
                            lastTouchY = currentY;
                            dateView.addDate(-1);
                            ScheduleUpdate();
                        }
                    }
                    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        v.setBackground(null);
                    }
                    return true;
                }
        });


        addFab = (FloatingActionButton) layout.findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddScheduleDialog.getInstance(dateView.getCalendar(),fragment).show(fragment.getChildFragmentManager(),"scheduleAdd");
                ScheduleUpdate();
            }
        });

        list = (ListView)layout.findViewById(R.id.list);

        ScheduleUpdate();





        return layout;
    }
}
