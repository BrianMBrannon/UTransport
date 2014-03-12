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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.Arrays;

public class ShowStop extends Activity {

    private TextView stopText;
    private TextView inboundText;
    private TextView outboundText;
    private TextView test;

    private int deviceMinutes;
    private String dayOfWeek;
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

        MobileDevice myDevice = new MobileDevice();
        this.dayOfWeek = myDevice.getWeekday();
        this.deviceMinutes = myDevice.getAllMinutes();

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
                BusStop theStop = new BusStop(stopNumber, dayOfWeek);
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
                int[] inboundMinutes = timesToMinutes(myStop.getInboundTimes());
                int[] outboundMinutes = timesToMinutes(myStop.getOutboundTimes());
                test.setText(Arrays.toString(inboundMinutes));
                test.append("\n" + Arrays.toString(outboundMinutes));

                try {
                    FileOutputStream fileWriter;
                    String fileName = myStop.getRouteNumber() + ".txt";
                    fileWriter = openFileOutput(fileName, MODE_APPEND);
                    fileWriter.write((fileName.substring(0, 5) + "\n").getBytes());
                    fileWriter.write((Double.toString(myStop.getLatitude()) + "\n").getBytes());
                    fileWriter.write((Double.toString(myStop.getLongitude()) + "\n").getBytes());
                    fileWriter.write((Arrays.toString(inboundMinutes) + "\n").getBytes());
                    fileWriter.write((Arrays.toString(outboundMinutes) + "\n").getBytes());
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
            stopText.setText(bufferedReader.readLine().substring(0,4));
            inboundText = (TextView) findViewById(R.id.inbound);
            inboundText.setText(bufferedReader.readLine());
            outboundText = (TextView) findViewById(R.id.outbound);
            outboundText.setText(bufferedReader.readLine());
            test = (TextView) findViewById(R.id.page_source);
            MobileDevice myDevice = new MobileDevice();
            test.setText(myDevice.getWeekday() + " " + deviceMinutes);
            test.append("\n" + bufferedReader.readLine());
            test.append("\n" + bufferedReader.readLine());
            test.append("\nRead from phone.");
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

    /**
     * Convert times to minutes. i.e. 12:32pm becomes 752
     */
    private int[] timesToMinutes(String[] times) {
        int[] timesInMinutes = new int[times.length];
        for (int i = 0; i < times.length; i++) {
            int minutes = 0;
            if (times[i].charAt(6) == 'p') {
                minutes += 720; //if pm then add all minutes for elapsed am
            }
            if (!times[i].substring(1,3).equals("12")) {
                minutes += Integer.parseInt(times[i].substring(1,3)) * 60; //hours to minutes
            }
            minutes += Integer.parseInt(times[i].substring(4,6));
            timesInMinutes[i] = minutes;
        }
        return timesInMinutes;
    }

    /**
     * Find the next time using a binary search
     */
    private int nextTime(int[] times) {
        int currentMostRelevantIndex = times.length / 2;
        int myTime = deviceMinutes;
        boolean found = false;
        while (!found) {

        }
        return currentMostRelevantIndex;
    }

}
