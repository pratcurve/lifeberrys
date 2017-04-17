package com.example.prateek.lifeberrys;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ScreenSlidePagerActivity extends FragmentActivity {

//    Number of pages to show
    private static int NUM_PAGES;

    //pager widget, handles animation and allow swiping to previous and next page in the activity
    private ViewPager mPager;
    TextView feeds_title;
    private static List<LifeBerrysXmlParser.Item> items;
    private static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = (ArrayList<LifeBerrysXmlParser.Item>)getIntent().getSerializableExtra("itemList");
        position = getIntent().getIntExtra("position", 0);
        NUM_PAGES = items.size();
        setContentView(R.layout.activity_screen_slide);

        feeds_title = (TextView) findViewById(R.id.main_feeds);
        doTask();

        //Instantiate a ViewPager and PagerAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        PagerAdapter mPagerAdapter = new ScreenSliderPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position, false);
    }

    public void doTask() {
        String category = items.get(position).category;
        final ArrayList<String> mainHeading = new ArrayList<>();
        final ArrayList<Integer> positions = new ArrayList<>();
        for (int j = 0; j < items.size(); j++) {
            if (items.get(j).category.equals(category)) {
                mainHeading.add(items.get(j).mainHeading);
                positions.add(j);
            }
        }
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                if (i>=mainHeading.size()){
                    i = 0;
                }
                feeds_title.setText(mainHeading.get(i));
                final int goToFeed = i;
                handler.postDelayed(this, 3000);
                feeds_title.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPager.setCurrentItem(positions.get(goToFeed));
                            }
                        }
                );
                i++;
            }
        });
    }

    private class ScreenSliderPagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSliderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            HashMap<String, String> map = items.get(position);
            return fragmentScreenSlidePage.newInstance(position, items);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_screen_slide_pager, menu);
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
}
