package com.ridewave.ridewave;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RouteDrawer {

    private GoogleMap mMap;
    private OnRouteDrawnListener mListener;

    public RouteDrawer(GoogleMap map, OnRouteDrawnListener listener) {
        mMap = map;
        mListener = listener;
    }

    public void drawRoute(LatLng origin, LatLng destination) {
        String url = getDirectionsUrl(origin, destination);
        new FetchDirectionsTask().execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyA46EMPXIoNqP_IFId9kQKRYRKSrclntoE";
    }

    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, PolylineOptions> {

        @Override
        protected PolylineOptions doInBackground(String... jsonData) {
            JSONObject jObject;
            PolylineOptions lineOptions = new PolylineOptions();
            try {
                jObject = new JSONObject(jsonData[0]);
                JSONArray routes = jObject.getJSONArray("routes");
                for (int i = 0; i < routes.length(); i++) {
                    JSONObject route = routes.getJSONObject(i);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String encodedString = overviewPolyline.getString("points");
                    lineOptions.addAll(decodePoly(encodedString));
                    lineOptions.width(10);
                    lineOptions.color(Color.BLUE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return lineOptions;
        }

        @Override
        protected void onPostExecute(PolylineOptions lineOptions) {
            super.onPostExecute(lineOptions);
            if (lineOptions != null) {
                mListener.onRouteDrawn(lineOptions);
            }
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new java.util.ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public interface OnRouteDrawnListener {
        void onRouteDrawn(PolylineOptions polylineOptions);
    }
}
