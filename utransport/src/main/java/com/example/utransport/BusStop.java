package com.example.utransport;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * Created by bubba on 1/24/14.
 * This is the class for a single Route
 */

public class BusStop {

    private String id = "NoIDinit";
    public String latYLong = "NoLatOrLongInit";
    private double latitude;
    private double longitude;
    private InputStream pageSource;
    //private String[] times;
    private String URL = "http://www.capmetro.org/STOPS.ASP?ID=";

    public BusStop(int id) throws URISyntaxException {
        URL += id;
        this.id = Integer.toString(id);
        try {
            this.pageSource = retrieveSourceStream();
            //this.id = searchFor("Stop ID ", ' ');
            latAndLong();
            this.latitude = Double.parseDouble(searchForLat());
            //this.longitude = Double.parseDouble(searchFor(longPrecedent, ')'));
            this.longitude = Double.parseDouble(searchForLong());
            //this.times = times();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BusStop() {
        throw new IllegalArgumentException("Must supply a URL.");
    }

    public double getLatitude() throws IOException {
        return latitude;
    }

    public double getLongitude() throws IOException {
        return longitude;
    }

    public String getRouteNumber() throws IOException { return id; }

    //public String[] getTimes() { return times; }


    /*
     * SOURCE OF ERRORS:    Need a new thread to access internet (Async Task)
     *                      Need INTERNET permissions
     */

    public InputStream retrieveSourceStream() throws IOException {

        //BufferedReader input = null;
        //String data = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(URL);
        //The below code crashes the app  -- SOLVED: replace old URL info with URI
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream web = entity.getContent();
//        input = new BufferedReader(new InputStreamReader(web));
//        StringBuffer sb = new StringBuffer("");
//        String line = "";
//        String nl = System.getProperty("line.separator");
        //input.readLine();
        //input.readLine();
        //data = input.readLine();
//        while((line = input.readLine()) != null) {
//            sb.append(line + nl);
//        }
        //input.close();
        //data = sb.toString();
        return web;

        //throw new IllegalArgumentException("End of getPageSource reached.");
    }
/*
    //length is the length of the desired content (e.g. the id has length 4)
    //indicator is what directly precedes the desired content
    private String searchFor(String indicator, int length) throws IOException {
        if(indicator.length() < 1) {
            throw new IllegalArgumentException("The indicator must exist.");
        }

        InputStream stream = url.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line;
        int currentIndicatorID = 0;
        int matchingChars = 0;

        while((line = reader.readLine()) != null) {
            line = reader.readLine();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == indicator.charAt(currentIndicatorID)) {
                    matchingChars++;
                    currentIndicatorID++;
                    if(matchingChars == indicator.length()) {
                        //indicator (in <title>) has been found; the ID now follows it.
                        String id = "";
                        for(int j = 1; j <= length; j++) {
                            //NOTE- assumes the ID is 'length' characters long
                            //NOTE- assumes there is no text after the ID
                            id += line.charAt(i + j);
                        }
                        reader.close();
                        stream.close();
                        return id;
                    }
                } else {
                    matchingChars = 0;
                    currentIndicatorID = 0;
                }
            }
        }
        reader.close();
        stream.close();
        return "N/A";
    }
    */
    //indicator is what precedes the desired content
    //delim is what follows the desired content
    private String searchFor(String indicator, char delim) throws IOException {
        if(indicator.length() < 1) {
            throw new IllegalArgumentException("The indicator must exist.");
        }
        //InputStream stream = pageSource.
        BufferedReader reader = new BufferedReader(new InputStreamReader(pageSource));

        String line;
        int currentIndicatorID = 0;
        int matchingChars = 0;

        while((line = reader.readLine()) != null) {
            line = reader.readLine();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == indicator.charAt(currentIndicatorID)) {
                    matchingChars++;
                    currentIndicatorID++;
                    if(matchingChars == indicator.length()) {
                        //indicator (in <title>) has been found; the content now follows it.
                        String content = "";
                        int j = 1;
                        while (line.charAt(i + j) != delim) {
                            content += line.charAt(i + j);
                            j++;
                        }
                        reader.close();
                        pageSource.close();
                        return content;
                    }
                } else {
                    matchingChars = 0;
                    currentIndicatorID = 0;
                }
            }
        }
        reader.close();
        pageSource.close();
        return "N/A";
    }

    private void latAndLong() throws IOException {
        //extra space needed for searchForLong
        this.latYLong = searchFor("GLatLng(", ')') + ' ';
    }

    private String searchForLat() {
        int index = 0;
        char delim = ',';
        String latitude = "";
        while(index < latYLong.length()) {
            if (latYLong.charAt(index) == delim) {
                return latitude;
            }
            latitude += latYLong.charAt(index);
            index++;
        }
        return "Searched; nothing found.";
    }

    //The delim is WRONG for this method.
    //pre - latitude has been found
    private String searchForLong() {
        int index = Double.toString(latitude).length() + 1;
        char delim = ' ';
        String longitude = "";
        while(index < latYLong.length()) {
            if (latYLong.charAt(index) == delim) {
                return longitude;
            }
            longitude += latYLong.charAt(index);
            index++;
        }
        return "Searched; nothing found.";
    }

    //returns the distance of a straight line between the two stops
    //perhaps the Google Maps API has a better method
   /* public double distanceTo(BusStop route) throws IOException {
        //Taking the absolute value results in an incorrect latitude and longitude
        //For the purpose of finding the distance between the two, this is irrelevant
        double routeLat = Math.abs(route.getLatitude());
        double routeLon = Math.abs(route.getLongitude());
        latitude = Math.abs(latitude);
        longitude = Math.abs(longitude);

        return Math.sqrt(Math.pow(routeLat - latitude, 2) + Math.pow(routeLon - longitude, 2));
    }*/

    //return all times associated
    //modified search
    /*public String[] times(String indicator, int bound) throws IOException {
        //bound 5 = inbound
        //bound 11 = outbound
        String[] times;
        String line;
        int currentIndicatorID = 0;
        int matchingChars = 0;


        while((line = pageSource.readLine()) != null) {
            line = pageSource.readLine();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == indicator.charAt(currentIndicatorID)) {
                    matchingChars++;
                    currentIndicatorID++;
                    if(matchingChars == indicator.length()) {
                        //indicator (type of route) has been found; the times soon follow.
                        for (int j = 0; j < bound; j++) {
                            pageSource.readLine();
                        }
                        String lineOfTimes = pageSource.readLine();
                        return timesToArray(lineOfTimes);
                    }
                } else {
                    matchingChars = 0;
                    currentIndicatorID = 0;
                }
            }
        }
        pageSource.close();
        //stream.close();
        throw new IllegalArgumentException("No times were found.");
    }

    public String[] inboundTimes(String indicator) throws IOException {
        return times(indicator, 5);
    }

    public String[] outboundTimes(String indicator) throws IOException {
        return times(indicator, 11);
    }

    private String[] timesToArray(String times) {
        //each time is formatted as such: " 0:00*M" with a "<br>" at the end
        int numTimes = (times.length() - 4) / 8;
        String[] timesArray = new String[numTimes];
        for (int i = 0; i < numTimes; i++) {
            timesArray[i] = times.substring(0 + i * 8, 8 + i * 8);
        }

        return timesArray;
    }*/
}
