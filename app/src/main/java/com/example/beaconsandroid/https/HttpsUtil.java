package com.example.beaconsandroid.https;

import android.content.Context;
import android.media.MediaActionSound;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.beaconsandroid.BeaconApplication;

import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.function.Consumer;

public class HttpsUtil {

    private static HttpsUtil instance;

    private RequestQueue requestQueue;


    private HttpsUtil(Context context) {
        this.requestQueue =  Volley.newRequestQueue(context);
        requestQueue.start();
    }

    public static HttpsUtil getInstance(Context context){
        if (instance == null)instance = new HttpsUtil(context);
        return instance;
    }

    private static final String baseUrl = "https://b3d0ba26.ngrok.io";

    public void notifyRegionEntered(Region region, String deviceId, Consumer<JSONObject> callback) throws JSONException {
        new MediaActionSound().play(3);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, baseUrl + "/message", new JSONObject("{\n" +
                " imei: " + deviceId +
                ", major: "+ (region.getId2() == null ? 0 : region.getId2()) +
                ", minor: " + (region.getId3() == null ? 0 : region.getId3()) +
                ", utcTime: " + new Date().getTime() +
                ", uuid: " + region.getId1() +
                "}"), callback::accept, System.out::println);

        requestQueue.add(request);
    }

    public void notifyRegionExit(Region region) {
        new MediaActionSound().play(2);
    }

    public  void requestPoi(String deviceId, Consumer<JSONObject> consumer) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl + "/poi/current/" + deviceId,null, consumer::accept, System.out::println);
        this.requestQueue.add(request);
    }

    private static void launchPoiActivity(JSONObject x) {

    }


}
