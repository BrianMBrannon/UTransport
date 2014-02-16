package com.example.utransport;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class ShowStop extends Activity {

    private TextView stopText;
    private TextView inboundText;
    private TextView outboundText;
    private TextView test;
    private BusStop myStop;
    //stopNumber will later be given by finding the closest route
    private int stopNumber = 5039;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stop);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //NOTE that the fileName is the stopNumber in a .txt file.  Eg. 3852.txt
        File myFile = getApplicationContext().getFileStreamPath(stopNumber + ".txt");
        //delete the file for testing purposes
        //myFile.delete();
        //if the file exists we can read the times from the device itself
        if (!myFile.exists()) {
            //Set up BusStop and Activity UI
            new setDataFromInternet().execute();
        }
        else {
            setDataFromPhone(myFile);
        }
    }

    public class setDataFromInternet extends AsyncTask<BusStop, Integer, BusStop> {

        @Override
        protected BusStop doInBackground(BusStop... params) {
            try {
                BusStop theStop = new BusStop(stopNumber);
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
                inboundText = (TextView) findViewById(R.id.inbound);
                inboundText.append(Double.toString(myStop.getLatitude()));
                outboundText = (TextView) findViewById(R.id.outbound);
                outboundText.append(Double.toString(myStop.getLongitude()));
                test = (TextView) findViewById(R.id.page_source);
                test.setText(myStop.tester);

                try {
                    FileOutputStream fileWriter;
                    String fileName = myStop.getRouteNumber() + ".txt";
                    fileWriter = openFileOutput(fileName, MODE_APPEND);
                    fileWriter.write((fileName + "\n").getBytes());
                    fileWriter.write((Double.toString(myStop.getLatitude()) + "\n").getBytes());
                    fileWriter.write((Double.toString(myStop.getLongitude()) + "\n").getBytes());
                    //Weekday to be later made into a variable
                    fileWriter.write((myStop.inboundTimes("WEEKDAY") + "\n").getBytes());
                    fileWriter.write((myStop.outboundTimes("WEEKDAY") + "\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //i may not need to return a BusStop Object
    }

    public void setDataFromPhone(File myFile) {
        try {
            FileReader fileReader = new FileReader(myFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            stopText = (TextView) findViewById(R.id.show_title);
            stopText.setText(bufferedReader.readLine());
            inboundText = (TextView) findViewById(R.id.inbound);
            inboundText.setText(bufferedReader.readLine());
            outboundText = (TextView) findViewById(R.id.outbound);
            outboundText.setText(bufferedReader.readLine());
            test = (TextView) findViewById(R.id.page_source);
            test.setText("I READ FROM THE PHONE!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
