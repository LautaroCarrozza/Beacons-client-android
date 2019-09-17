package com.example.beaconsandroid.https;

import android.content.Context;
import android.media.MediaActionSound;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.beaconsandroid.BeaconApplication;

import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class HttpsUtil {

    private static final String baseUrl = "https://7f34a7f8.ngrok.io";

    public static void notifyRegionEntered(Region region, Context context, String deviceId) throws JSONException {
        new MediaActionSound().play(3);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, baseUrl + "/message", new JSONObject("{\n" +
                " imei: " + deviceId +
                ", major: "+ (region.getId2() == null ? 0 : region.getId2()) +
                ", minor: " + (region.getId3() == null ? 0 : region.getId3()) +
                ", utcTime: " + new Date().getTime() +
                ", uuid: " + region.getId1() +
                "}"), x -> requestPoi(requestQueue,deviceId), System.out::println);
        requestQueue.add(request);
    }

    public static void notifyRegionExit(Region region, BeaconApplication mainActivity) {
        new MediaActionSound().play(2);
    }

    public static void requestPoi(RequestQueue queue, String deviceId) {
        try {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, baseUrl + "/message", new JSONObject("{ " +
                    " imei: " + deviceId +
                    "}"), HttpsUtil::launchPoiActivity, System.out::println);
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void launchPoiActivity(JSONObject x) {

    }


}
