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
import java.util.ArrayList;
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
                test.append("\n" + minutesToTime(nextTime(outboundMinutes)));

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
            String outBoundLine = bufferedReader.readLine();
            test.append("\n" + outBoundLine);
            test.append("\nRead from phone.");
            test.append("\nNextTime: " + minutesToTime(nextTime(stringLineToArray(outBoundLine))));
            test.append("\n752 = " + minutesToTime(752));
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
     * convert minutes to time. e.g. 752 becomes 12:32pm
     */
    private String minutesToTime(int minutes) {
        String time = "";
        String amOrPm = "";
        if (minutes >= 720) {
            amOrPm = "pm";
            minutes -= 720;
        }
        else
            amOrPm = "am";

        //find hours
        if (minutes / 60 == 0)
            time += "12:";
        else
            time += minutes / 60 + ":";
        if (minutes % 60 == 0) time += minutes % 60;
        time += minutes % 60;
        time += amOrPm;
        return time;
    }

    /**
     * Find the next time using a special binary search
     */
    private int nextTime(int[] times) {
        return nextTime(times, 0, times.length, 0);
    }

    private int nextTime(int[] times, int startIndex, int endIndex, int closestTimeSoFar) {
        if (startIndex >= endIndex) return closestTimeSoFar;
        //base case

        int currentMostRelevantIndex = (startIndex + endIndex) / 2;
        if (times[currentMostRelevantIndex] < deviceMinutes) {
            closestTimeSoFar = times[currentMostRelevantIndex + 1];
            return nextTime(times, currentMostRelevantIndex + 1, endIndex, closestTimeSoFar);
        }
        else if (times[currentMostRelevantIndex] > deviceMinutes) {
            closestTimeSoFar = times[currentMostRelevantIndex];
            return nextTime(times, startIndex, currentMostRelevantIndex, closestTimeSoFar);
        }
        else {
            //times[currentMostRelevantIndex] == deviceMinutes
            return times[currentMostRelevantIndex];
        }
    }

    /**
     * Time a string written to a file and turn it into a searchable array of ints
     */
    private int[] stringLineToArray(String line) {
        //int[] with correct amount of entries
        ArrayList<Integer> times = new ArrayList<Integer>();
        StringBuffer stringBuffer = new StringBuffer(line);
        //I start variables at 1 instead of 0 to avoid the inevitable '['
        int singleTimeStartIndex = 1;
        int singleTimeEndIndex = 1;
        for (int i = 1; i < stringBuffer.length(); i++) {
            if (stringBuffer.charAt(i) == ' ') {
                System.out.println("FOUND A SPACE");
                times.add(Integer.parseInt(stringBuffer.substring(singleTimeStartIndex, singleTimeEndIndex - 1)));
                System.out.println(times);
                //subtract two to avoid the ", "
                singleTimeStartIndex = i + 1;
                singleTimeEndIndex++;
            }
            else {
                System.out.println("NO SPACE");
                singleTimeEndIndex++;
            }
        }
        //Solve Fencepost Problem, for loop cannot add the very last integer
        //Instead of taking the last 4 digits, take all digits until a ' ' to be flexible.
        //O(1) basically since the only options are three or four iterations
        int index = stringBuffer.length() - 2; //-2 to avoid inevitable ']'
        while (stringBuffer.charAt(index) != ' ') {
            index--;
        }
        times.add(Integer.parseInt(stringBuffer.substring(index + 1, stringBuffer.length() - 1)));
        return intArrayListToArray(times);
    }

    //Helper method to convert an arraylist of integers to a native array
    private int[] intArrayListToArray(ArrayList<Integer> list) {
        int[] newArray = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArray[i] = list.get(i);
        }
        return newArray;
    }


}
