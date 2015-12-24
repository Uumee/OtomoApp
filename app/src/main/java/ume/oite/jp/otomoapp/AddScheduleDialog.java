package ume.oite.jp.otomoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Ume on 2015/07/20.
 */
public class AddScheduleDialog extends DialogFragment {

    private DialogInterface.OnClickListener okClickListener = null;
    private DialogInterface.OnClickListener cancelClickListener = null;

    private LinearLayout dialogLayout = null;
    private ListView scheduleList = null;

    private EditText scheduleEdit = null;
    private TextView dateText = null;
    private TextView titleText = null;
    private TextView relativeText = null;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    Fragment parent = null;

    private int year=0,month=0,date=0;
    private int last_year=0,last_month=0,last_date=0;

    public static AddScheduleDialog getInstance(Calendar calendar, Fragment parent){
        AddScheduleDialog dialog = new AddScheduleDialog();
        dialog.parent = parent;
        Bundle args = new Bundle();
        args.putInt("year", calendar.get(Calendar.YEAR));
        args.putInt("month",calendar.get(Calendar.MONTH));
        args.putInt("date", calendar.get(Calendar.DATE));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle safedInstanceState){

        dialogLayout = (LinearLayout)this.getActivity().getLayoutInflater().inflate(R.layout.dialog_add_schedule, null, false);

        dbHelper = new DatabaseHelper(this.getActivity().getApplicationContext());

        year = this.getArguments().getInt("year");
        month = this.getArguments().getInt("month")+1;
        date = this.getArguments().getInt("date");

        last_year=year;
        last_month=month;
        last_date=date;

        titleText = (TextView)dialogLayout.findViewById(R.id.titleText);
        titleText.setText(" 予　定　追　加 ");

        dateText = (TextView)dialogLayout.findViewById(R.id.dateText);
        relativeText = (TextView)dialogLayout.findViewById(R.id.relativeText);
        updateDateText();

        scheduleEdit = (EditText)dialogLayout.findViewById(R.id.scheduleEdit);

        Button beforeButton = (Button)dialogLayout.findViewById(R.id.beforeButton);
        beforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDate(-1);
            }
        });

        Button afterButton = (Button)dialogLayout.findViewById(R.id.afterButton);
       afterButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               addDate(+1);
           }
       });


        settingOkClickListener();
        settingCancelClickListener();
        settingDateListener();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder .setView(dialogLayout)
                .setPositiveButton("OK", this.okClickListener)
                .setNegativeButton("Cancel", this.cancelClickListener);

        Dialog d = builder.create();
        d.show();

        /*WindowManager wm = (WindowManager)this.getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point pt = new Point();
        disp.getSize(pt);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(lp);
*/
        return d;

    }

    public void setOkClickListener(DialogInterface.OnClickListener listener){
        this.okClickListener = listener;
    }

    public void settingOkClickListener(){
        setOkClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addSchedule();
            }
        });
    }

    public void settingCancelClickListener(){
    }

    public String addSchedule(){
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String schedule = scheduleEdit.getText().toString();
        if(schedule.equals(""))schedule = "empty";
        values.put("Schedule", schedule);
        values.put("BeginTime",year+"-"+month+"-"+date+"-12-00-00");
        Log.d("d",""+year+"-"+month+"-"+date+"-12-00-00");
        values.put("EndTime", last_year + "-" + last_month + "-" + last_date + "-13-00-00");
        database.insert("Sample", null, values);
        database.close();
        return schedule;
    }

    public void settingDateListener(){
        this.dateText.setOnTouchListener(new View.OnTouchListener() {
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
                    if (currentX - lastTouchX > intervalX) {
                        lastTouchX = currentX;
                        addDate(+1);
                    }
                    if (currentX - lastTouchX < -intervalX) {
                        lastTouchX = currentX;
                        addDate(-1);
                    }
                    if (currentY - lastTouchY > intervalY) {
                        lastTouchY = currentY;
                        addMonth(+1);
                    }
                    if (currentY - lastTouchY < -intervalY) {
                        lastTouchY = currentY;
                        addMonth(-1);
                    }
                }
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    v.setBackground(null);
                }
                return true;
            }
        });
    }

    private void addDate(int value){
        Calendar c = Calendar.getInstance();
        c.set(last_year, last_month-1, last_date);
        c.add(Calendar.DATE, value);
        setCalendar(c);
        updateDateText();
    }
    private void addMonth(int value){
        Calendar c = Calendar.getInstance();
        c.set(last_year, last_month - 1, last_date);
        c.add(Calendar.MONTH, value);
        setCalendar(c);
        updateDateText();
    }

    private void setCalendar(Calendar c){
        last_year = c.get(Calendar.YEAR);
        last_month = c.get(Calendar.MONTH)+1;
        last_date = c.get(Calendar.DATE);
    }

    private void updateDateText(){
        dateText.setText(last_year + "/" + last_month + "/" + last_date);
        updateRelativeText(last_year, last_month - 1, last_date);
    }

    private void updateRelativeText(int year,int month,int date){
        Calendar today = Calendar.getInstance();
        today = clearTime(today);
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year,month,date);
        String str = "";
        switch(today.compareTo(c)){
            case 0:
                str = " ( 今日まで ) ";
                relativeText.setTextColor(Color.GRAY);
                break;
            case -1:
                str = " ( " + calcRelativeCalendar(today,c) + "日後まで" +" ) ";
                relativeText.setTextColor(Color.rgb(200, 100, 100));
                break;
            case 1:
                str = " ( "+ calcRelativeCalendar(c,today) + "日前" + " ) ";
                relativeText.setTextColor(Color.rgb(100,100,200));
                break;
            default:
                str = "null";
                relativeText.setTextColor(Color.GRAY);
                break;
        }
        relativeText.setText(str);
    }

    private long calcRelativeCalendar(Calendar before, Calendar after){
        long beforeDate = clearTime(before).getTime().getTime();
        long afterDate = clearTime(after).getTime().getTime();
        return (afterDate - beforeDate) / (1000*60*60*24);

    }

    private Calendar clearTime(Calendar cal) {
        cal.clear(Calendar.AM_PM);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal;
    }
}

