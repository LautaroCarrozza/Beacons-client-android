package com.example.beaconsandroid.https;

import android.content.Context;
import android.media.MediaActionSound;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.function.Consumer;

public class HttpsUtil {

    private static HttpsUtil instance;

    private RequestQueue requestQueue;


    /**
     * Constructor
     * @param context A {@link Context} to use for calling {@link Volley#newRequestQueue(Context)}.
     */
    private HttpsUtil(Context context) {
        this.requestQueue =  Volley.newRequestQueue(context);
        requestQueue.start();
    }

    /**
     * Returns an {@link HttpsUtil} instance or creates a new one if it hasn't been initialized
     * @param context A for calling {@link #HttpsUtil(Context)}
     * @return A started {@link HttpsUtil} instance
     */
    public static HttpsUtil getInstance(Context context){
        if (instance == null)instance = new HttpsUtil(context);
        return instance;
    }

    private static final String baseUrl = "https://3ceae7c5.ngrok.io";

    /**
     * Send a request notifying when a phone enters a region
     * @param region from a beacon
     * @param deviceId phone imei
     * @param callback function for request
     * @throws JSONException indicates a problem with de JSON api
     */
    public void notifyRegionEntered(Region region, String deviceId, Consumer<JSONObject> callback) throws JSONException {
        new MediaActionSound().play(3);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl + "/message/" +
                region.getId1() +
                "/" + (region.getId3() == null ? 0 : region.getId3()) +
                "/" + (region.getId2() == null ? 0 : region.getId2()) +
                "/" + deviceId
                , null, callback::accept, System.out::println);

        requestQueue.add(request);
    }

    /**
     * Play a sound when a beacon its no longer detected
     * @param region from a beacon when phone stops detecting it
     */
    public void notifyRegionExit(Region region) {
        new MediaActionSound().play(2);
    }

    /**
     * Get Request to server, the points of interests from the regions detected by the phone
     * @param deviceId phone id
     * @param consumer jsonObject promise
     */
    public void requestPoi(String deviceId, Consumer<JSONObject> consumer) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl + "/poi/current/" + deviceId,null, consumer::accept, System.out::println);
        this.requestQueue.add(request);
    }

    /**
     * Get Request for regions, to server
     * Region is the area of a beacon with its identifications
     * @param consumer jsonObject promise
     */
    public void requestRegions(Consumer<JSONArray> consumer){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, baseUrl + "/beacon", null, consumer::accept, System.out::println);
        this.requestQueue.add(request);
    }

    private static void launchPoiActivity(JSONObject x) {

    }


}
