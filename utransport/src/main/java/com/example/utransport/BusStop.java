package com.example.utransport;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bubba on 1/24/14.
 * This is the class for a single Route
 */

public class BusStop {

    private URL url;
    private String id = "";
    private double latitude;
    private double longitude;
    //private String[] times;
    private static final int linesUntillTimes = 5;
    private static final String beginningOfURL = "http://www.capmetro.org/STOPS.ASP?ID=";

    public BusStop (String address) throws MalformedURLException {
        this.url = new URL(address);
        try {
            this.id = searchFor("Stop ID ", ' ');
            this.latitude = Double.parseDouble(searchFor("GLatLng(", ','));
            String longPrecedent = latitude + ",";
            this.longitude = Double.parseDouble(searchFor(longPrecedent, ')'));
            //this.times = times();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public BusStop(int id) throws MalformedURLException {
        this(beginningOfURL + id);
    }

    public BusStop() throws MalformedURLException {
        throw new IllegalArgumentException("Must supply a URL.");
    }

    public Double getLatitude() throws IOException {
        return latitude;
    }

    public Double getLongitude() throws IOException {
        return longitude;
    }

    public String getRouteNumber() throws IOException { return id; }

    //public String[] getTimes() { return times; }

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

    //indicator is what precedes the desired content
    //delim is what follows the desired content
    private String searchFor(String indicator, char delim) throws IOException {
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
                        int j = 1;
                        while (line.charAt(i + j) != delim) {
                            id += line.charAt(i + j);
                            j++;
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

    //returns the distance of a straight line between the two stops
    //perhaps the Google Maps API has a better method
    public double distanceTo(BusStop route) throws IOException {
        //Taking the absolute value results in an incorrect latitude and longitude
        //For the purpose of finding the distance between the two, this is irrelevant
        double routeLat = Math.abs(route.getLatitude());
        double routeLon = Math.abs(route.getLongitude());
        latitude = Math.abs(latitude);
        longitude = Math.abs(longitude);

        return Math.sqrt(Math.pow(routeLat - latitude, 2) + Math.pow(routeLon - longitude, 2));
    }

    //return all times associated
    //modified search
    public String[] times(String indicator, int bound) throws IOException {
        //bound 5 = inbound
        //bound 11 = outbound
        String[] times;
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
                        //indicator (type of route) has been found; the times soon follow.
                        for (int j = 0; j < bound; j++) {
                            reader.readLine();
                        }
                        String lineOfTimes = reader.readLine();
                        return timesToArray(lineOfTimes);
                    }
                } else {
                    matchingChars = 0;
                    currentIndicatorID = 0;
                }
            }
        }
        reader.close();
        stream.close();
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
    }
}
