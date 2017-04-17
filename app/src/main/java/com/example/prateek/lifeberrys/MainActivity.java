package com.example.prateek.lifeberrys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

//    public static final String WIFI = "Wi-Fi";
//    public static final String ANY = "Any";
    private static final String URL = "http://www.lifeberrys.com/rss/app/";

//    LifeBerrysXmlParser.Item item = new LifeBerrysXmlParser.Item();
    List<LifeBerrysXmlParser.Item> items =null;

    private ProgressBar progressBar;

    public void loadPage() {
        Log.i("", "load Page method called from on created");
        if (isInternetConnected(getApplicationContext())) {
            new DownloadXmlTask().execute(URL);
        } else {
            buildDialog(MainActivity.this).show();
        }
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("Check your internet Connection");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loadPage();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }

        });
        return builder;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean isInternetConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException, URISyntaxException {
        InputStream stream = null;
        // Instantiate the parser
        LifeBerrysXmlParser lifeBerrysXmlParser = new LifeBerrysXmlParser();
        Log.i("", "LoadXmlFromNetwork called from downloadXml");
        try {
            Log.i("", "try block LoadXmlFromNetwork");
            stream = downloadUrl(urlString);
            items = lifeBerrysXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) stream.close();
        }

        return null;
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException, URISyntaxException {
        Log.i("", "download url CAlled from ");
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

    private class DownloadXmlTask extends AsyncTask<String, Integer, String>  {
        @Override
        protected String doInBackground(String... urls) {
            try {
                Log.i("", "do in Back, DownloadXmlTask called from loadUrl");
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
            Log.i("", "Here in post execute");
//            setContentView(R.layout.activity_main);
            if (items != null) {
                Intent intent = new Intent(getBaseContext(), ArticlesList.class);
                intent.putExtra("itemList", (Serializable) items);
                startActivity(intent);
            } else {
                buildDialog(MainActivity.this).show();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);
        loadPage();
    }
//
//    public void feeds(View view) {
//        Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
//        startActivity(intent);
//    }

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
}
