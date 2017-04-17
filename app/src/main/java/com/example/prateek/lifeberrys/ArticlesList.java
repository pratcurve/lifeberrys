package com.example.prateek.lifeberrys;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ArticlesList extends AppCompatActivity {

    private static int NUM_PAGES = 5;
    public List<LifeBerrysXmlParser.Item> items = null;
    ViewPager mPager;
    PagerAdapter mPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private List<LifeBerrysXmlParser.Item> slidingImagesList;
    private ProgressBar progressBar;
    private ListView articlesList;
    private ListAdapter listAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String urlByCategory = "http://www.lifeberrys.com/rss/app/databycategory.php?catid=";
    private String URL = "http://www.lifeberrys.com/rss/app/index.php?id=";
    private static int catid;
    private Toolbar toolbar;
    Timer timer;
    int page = 0;
    private static String lastId, firstId;
    private static boolean loadingMore = false;
    private static boolean onMainList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_articles_list2);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu_black_24dp));
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setShowHideAnimationEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //navigation drawer options
        final String menuOptions[] = {
                "Fashion",
                "Beauty",
                "Healthy Living",
                "Mates & Me",
                "Household",
                "Hunger Struck",
                "Holidays",
                "Entertainment",
                "Astrology",
                "All Articles"
        };

        //Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);

        //Initialising Array adapter for naavigation Drawer
        ArrayAdapter mDrawerAdapter = new DrawerCustomAdapter(this, menuOptions);
        mDrawerListView.setAdapter(mDrawerAdapter);
        mDrawerListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        catid = position+1;
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        mDrawerListView.setItemChecked(position, true);
                        progressBar.setVisibility(View.VISIBLE);
                        getSupportActionBar().setTitle(menuOptions[position]);
                        if (position == 9) {
                            getSupportActionBar().setTitle("Categories");
                            new DownloadXml().execute(URL);
                            onMainList = true;
                        } else  {
                            new DownloadXml().execute(urlByCategory+catid);
                            onMainList = false;
                        }
                    }
                }
        );

//        drawerToggle.setHomeAsUpIndicator(R.drawable.icon_nav);
//        drawer.setDrawerListener(drawerToggle);
//        //navigation drawer open and close
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // Articles List header
        View header = getLayoutInflater().inflate(R.layout.image_slide_pager, null);

        //Article list footer
        TextView footer = (TextView) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.article_list_footer, null, false);


        items = (ArrayList<LifeBerrysXmlParser.Item>)getIntent().getSerializableExtra("itemList");
        slidingImagesList = new ArrayList<LifeBerrysXmlParser.Item>(items);

        //articels list intializing and adding header footer
        articlesList = (ListView) findViewById(R.id.articlesList);
        articlesList.addFooterView(footer);
        articlesList.addHeaderView(header);

        //image slider on artilces list
        mPager = (ViewPager)header.findViewById(R.id.articleListPager);
        mPagerAdapter = new ImageSliderPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        pageSwitcher(4);

        //progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);


        //article list scrolling listener, hide toolbar on scrolling list upwards and
        //show it again on scrolling list downwards
        articlesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == articlesList.getId()) {
                    final int currentFirstVisibleItem = articlesList.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        getSupportActionBar().hide();
//                        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        getSupportActionBar().show();
//                        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                        setSupportActionBar(toolbar);
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });

        //array adapter article list
        listAdapter = new CustomAdapter(this, items);
        articlesList.setAdapter(listAdapter);
        articlesList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        position -= articlesList.getHeaderViewsCount();
                        Intent intent = new Intent(ArticlesList.this, ScreenSlidePagerActivity.class);
                        intent.putExtra("itemList", (Serializable) items);
                        intent.putExtra("position", position);
                        Log.i("Position", String.valueOf(position));
                        startActivity(intent);
                    }
                }
        );
    }

    //show more click listener
    public void loadMore(View v) {
        loadingMore = true;
        progressBar.setVisibility(View.VISIBLE);
        firstId = items.get(0).guID;
        lastId = items.get(items.size() - 1).guID;
        if (onMainList) {
            String loadMoreUrl = URL + lastId;
            new DownloadXml().execute(loadMoreUrl);
        } else {
            String loadMoreUrl = urlByCategory + catid + "&id=" + lastId;
            new DownloadXml().execute(loadMoreUrl);
        }
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page >= NUM_PAGES) { // the number of pages are 5
                        page = 0;
                    } else {
                        mPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    private class ImageSliderPagerAdapter extends FragmentStatePagerAdapter {
        public ImageSliderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
//            HashMap<String, String> map = items.get(position);
            return fragmentImageSlidePage.newInstance(position, slidingImagesList);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.articles_toolbar, menu);
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
        if (item.getItemId() == android.R.id.home) {
            if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }else{
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }

        }

//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListView);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException, URISyntaxException {
        Log.i("", "download url CAlled from aticles list");
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection() ;
        conn.setReadTimeout(15000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException, URISyntaxException {
        InputStream stream = null;
        // Instantiate the parser
        LifeBerrysXmlParser lifeBerrysXmlParser = new LifeBerrysXmlParser();
//        Log.i("", "LoadXmlFromNetwork called from downloadXml : articles list");
        try {
            stream = downloadUrl(urlString);
            if (loadingMore) {
                items.addAll(lifeBerrysXmlParser.parse(stream));
                loadingMore = false;
            } else {
                items = lifeBerrysXmlParser.parse(stream);
            }
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) stream.close();
        }

        return null;
    }

    private class DownloadXml extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                if (!loadingMore) {
                    items = new ArrayList<>();
                }
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return getResources().getString(R.string.xml_error);
            } catch (URISyntaxException e) {
                return getResources().getString(R.string.new_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (items != null) {
                listAdapter = new CustomAdapter(ArticlesList.this, items);
                articlesList.setAdapter(listAdapter);
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
}
