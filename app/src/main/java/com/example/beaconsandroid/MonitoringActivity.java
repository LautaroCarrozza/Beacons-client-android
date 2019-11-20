package com.example.beaconsandroid;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.altbeacon.beacon.BeaconManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MonitoringActivity extends Activity implements AdapterView.OnItemClickListener {
    protected static final String TAG = "MonitoringActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private ListView listView;
    private HashMap<String, Poi> poiByBeacon = new HashMap<>();

    /**
     * Called when activity is starting
     * Initialize {@link BeaconApplication}
     * Starts app in background
     * Asks for phone permissions
     * @param savedInstanceState If the activity is being re-initialized after
     *      previously being shut down then this Bundle contains the data it most
     *      recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        verifyBluetooth();

        BeaconApplication application = ((BeaconApplication) this.getApplicationContext());
        application.setMonitoringActivity(this);

        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);

        // Android M Permission check
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons in the background.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @TargetApi(23)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }

            });
            builder.show();
        }

    }

    /**
     * Callback for the result from requesting permissions
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults he grant results for the corresponding permissions
     *      which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *      or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    /**
     * Passes and set {@link MonitoringActivity} instance to {@link BeaconApplication#setMonitoringActivity(MonitoringActivity)}
     * @see Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        BeaconApplication application = ((BeaconApplication) this.getApplicationContext());
        application.setMonitoringActivity(this);
    }

    /**
     * @see Activity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        //((BeaconApplication) this.getApplicationContext()).setMonitoringActivity(null);
    }

    /**
     * Checks is bluetooth setting is on. In case its off notifies the user.
     */
    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //finish();
                        //System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    //finish();
                    //System.exit(0);
                }

            });
            builder.show();

        }

    }

    /**
     * Called when active pois is updated.
     * Updates view with active pois.
     * @param activePois list of actual points of interest in phones radar
     */
    public void updateData(List<Poi> activePois) {
        poiByBeacon = new HashMap<>();
        List<String> poisTittle = new ArrayList<>();
        for (Poi poi : activePois) {
            poisTittle.add(poi.getTitle());
            poiByBeacon.put(poi.getTitle(), poi);
        }

        listView.setAdapter(new ArrayAdapter<>(MonitoringActivity.this, android.R.layout.simple_list_item_1, poisTittle));

    }

    /**
     * Callback method to be invoked when an item in this adapter view has been clicked
     * {@link ListItemDetail#onCreate(Bundle)} and shows html of clicked item.
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this
     *             will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);

        Poi poi = poiByBeacon.get(parent.getItemAtPosition(position));

        if (poi == null)
            return;

        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, ListItemDetail.class);
        intent.putExtra("position", position);
        intent.putExtra("itemcontent", poi.getHtmlContent());
        intent.putExtra("itemtitle", poi.getTitle());
        // Or / And
        intent.putExtra("id", id);
        startActivity(intent);
    }
}