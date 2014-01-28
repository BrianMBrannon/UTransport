package com.example.utransport;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

public class ShowStop extends Activity {

    private TextView inboundText;
    private TextView outboundText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stop);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        try {
            BusStop myStop = new BusStop(3852);
            //inboundText = (TextView) findViewById(R.id.inbound);
           // outboundText = (TextView) findViewById(R.id.outbound);
           // inboundText.setText(Arrays.toString(myStop.inboundTimes("UT WEEKDAY")));
          //  outboundText.setText(Arrays.toString(myStop.outboundTimes("UT WEEKDAY")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } //catch (IOException e) {
     //       e.printStackTrace();
      //  }
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
