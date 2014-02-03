package com.example.utransport;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class ShowStop extends Activity {

    private TextView stopText;
    private TextView inboundText;
    private TextView outboundText;
    private TextView test;
    private BusStop myStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stop);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        new setData().execute();
        /*
        Thread display = new Thread() {
            public void run() {
                try {
                    BusStop theStop = new BusStop(3852);
                    setID(theStop);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } //catch (IOException e) {
                   // e.printStackTrace();
                //}
            }
            public void setID(BusStop theStop) {
                try {
                    stopText = (TextView) findViewById(R.id.show_title);
                    stopText.append(theStop.getRouteNumber());
                    test = (TextView) findViewById(R.id.page_source);
                    test.setText(theStop.getPageSource());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        display.start();
        display.run();
        */
       /* try {
            //"http://www.capmetro.org/STOPS.ASP?ID=3852"
            BusStop myStop = new BusStop(3852);
            test = (TextView) findViewById(R.id.page_source);
            String data = myStop.getPageSource("http://www.capmetro.org/STOPS.ASP?ID=3852").toString();
            //inboundText = (TextView) findViewById(R.id.inbound);
           // outboundText = (TextView) findViewById(R.id.outbound);
           // inboundText.setText(Arrays.toString(myStop.inboundTimes("UT WEEKDAY")));
          //  outboundText.setText(Arrays.toString(myStop.outboundTimes("UT WEEKDAY")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }  catch (URISyntaxException e) {
            e.printStackTrace();
        }
*/
    }

    public class setData extends AsyncTask<BusStop, Integer, BusStop> {

        @Override
        protected BusStop doInBackground(BusStop... params) {
            try {
                BusStop theStop = new BusStop(3852);
                return theStop;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(BusStop myStop) {
            try {
                stopText = (TextView) findViewById(R.id.show_title);
                stopText.append(myStop.getRouteNumber());
                test = (TextView) findViewById(R.id.page_source);
                test.setText(myStop.pageSource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_stop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_show_stop, container, false);
            return rootView;
        }
    }

}
