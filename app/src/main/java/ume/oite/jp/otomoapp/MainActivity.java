package ume.oite.jp.otomoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ViewPager viewPager;
    TabLayout tabLayout;

    PortalFragment portal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //いつもの
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity_main);

        //ツールバー
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        //ファブ
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        //ここから追加

        //タブ・ViewPager
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.pager);

        portal = PortalFragment.newInstance();

        //Fragmentを切り替えることができるようにするAdapter
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position){
                    case 0:
                        return CalendarPagerFragment.newInstance();
                    case 1:
                        return portal;
                    default:
                        return TestFragment.newInstance(position);
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch(position){
                    case 0:
                        return "Calendar";
                    case 1:
                        return "portal";
                    default :
                        return "test";
                }
            }
        };

        //ViewPagerにAdapterとListenerを設定
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        //tabLayoutとViewPagerをオートマチックに接続
        //http://androhi.hatenablog.com/entry/2015/06/17/083000
        tabLayout.setupWithViewPager(viewPager);

    }


    public void moveCalendar2Portal(int year,int month,int date){
        viewPager.setCurrentItem(1,true);
        Calendar c = Calendar.getInstance();
        c.set(year,month,date);
        portal.setCalendar(c);
        tabLayout.setSelected(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class TestFragment extends Fragment{
        public TestFragment(){

        }
        public static TestFragment newInstance(int page){
            Bundle args = new Bundle();
            args.putInt("page",page);
            TestFragment fragment = new TestFragment();
            fragment.setArguments(args);
            return fragment;
        }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int page = getArguments().getInt("page", 0);
            View view = inflater.inflate(R.layout.fragment_test, container, false);
            ((TextView) view.findViewById(R.id.page_text)).setText("Page " + page);
            return view;
        }
    }


}
