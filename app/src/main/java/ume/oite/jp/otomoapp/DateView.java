package ume.oite.jp.otomoapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Ume on 15/12/24.
 */
public class DateView extends LinearLayout {

    final String[] dowString = {"日","月","火","水","木","金","土"};
    final int SIZE = 18;
    Calendar calendar = null;

    TextView yearText,monthdateText,dowText;

    public DateView(Context context) {
        super(context);
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOrientation(LinearLayout.VERTICAL);

        update();


    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    void setCalendar(Calendar c){
        this.calendar = (Calendar)c.clone();
        this.removeAllViews();
        update();
    }

    Calendar getCalendar(){
        return this.calendar;
    }

    void update(){
        if(calendar == null)calendar = Calendar.getInstance();

        yearText = new TextView(this.getContext());
        monthdateText = new TextView(this.getContext());
        dowText=new TextView(this.getContext());

        yearText.setText(calendar.get(Calendar.YEAR)+" 年");
        yearText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        param1.weight=1.0f;
        yearText.setTextSize(SIZE);
        this.addView(yearText,param1);

        monthdateText.setText((calendar.get(Calendar.MONTH) + 1)+" / "+calendar.get(Calendar.DATE));
        monthdateText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        param2.weight=1.0f;
        monthdateText.setTextSize(SIZE);
        this.addView(monthdateText,param2);

        dowText.setText(dowString[calendar.get(Calendar.DAY_OF_WEEK)-1]+" 曜日");
        dowText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        param3.weight=1.0f;
        dowText.setTextSize(SIZE);
        this.addView(dowText,param3);
    }

    void addDate(int date){
        calendar.add(Calendar.DATE,date);
        this.removeAllViews();
        update();
    }
    void addMonth(int month){
        calendar.add(Calendar.MONTH,month);
        this.removeAllViews();
        update();
    }

}
