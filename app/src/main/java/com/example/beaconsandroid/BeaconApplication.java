package com.example.beaconsandroid;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaActionSound;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.beaconsandroid.https.HttpsUtil;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconApplication extends Application implements BootstrapNotifier {

    protected static final String TAG = "MonitoringActivity";

    private List<Region> regions = new ArrayList<>();
    private int beaconCount = 0;

    private List<RegionBootstrap> regionBootstraps;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private MonitoringActivity monitoringActivity;
    private String cumulativeLog = "";
    private String deviceId;
    private HttpsUtil httpsUtil;
    private Map<Region, Poi> activePois = new HashMap<>();
    private List<Poi> pois = new ArrayList<>();
    private ListItemDetail listItemDetail;

    @Override
    public void onCreate() {
        httpsUtil = HttpsUtil.getInstance(this);
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        httpsUtil.requestRegions(jsonArray -> {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject beacon = jsonArray.getJSONObject(i);
                    regions.add(new Region("Beacon " + i, Identifier.parse(beacon.getString("uuid")),
                            Identifier.parse(beacon.getString("major")),
                            Identifier.parse(beacon.getString("minor"))));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            // wake up the app when a beacon is seen
            regionBootstraps = new ArrayList<>(regions.size());
            for (Region r : regions) {
                regionBootstraps.add(new RegionBootstrap(this, r));
            }
        });

        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);


        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, MonitoringActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notificationChannel1",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            builder.setChannelId(channel.getId());
        }

        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);

    }

    public void disableMonitoring() {
        for (RegionBootstrap regionBootstrap : regionBootstraps) {
            if (regionBootstrap != null) {
                regionBootstrap.disable();
            }
        }
        regionBootstraps = null;
    }

    public void enableMonitoring() {
        regionBootstraps = new ArrayList<>(regions.size());
        for (Region r : regions) {
            regionBootstraps.add(new RegionBootstrap(this, r));
        }
    }


    @Override
    public void didEnterRegion(Region region) {
        try {

            this.httpsUtil.notifyRegionEntered(region, deviceId, (response) -> {
                this.httpsUtil.requestPoi(deviceId, jsonObject -> {
                    try {
                        System.out.println(jsonObject.get("title"));
                        Poi poi = new Poi(jsonObject.getString("title"), jsonObject.getString("html"));

                        activePois.put(region, poi);

                        if (!pois.contains(poi))
                            pois.add(poi);

                        monitoringActivity.updateData(pois);

                        sendNotification(poi.getTitle(), poi.getHtmlContent());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });
            });

        } catch (JSONException e) {
            Log.i(TAG, e.toString());
        }


        Log.d(TAG, "did enter region.");
        if (!haveDetectedBeaconsSinceBoot) {
            Log.d(TAG, "auto launching BeaconApplication");

            Intent intent = new Intent(this, MonitoringActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            haveDetectedBeaconsSinceBoot = true;
        } else {
            if (monitoringActivity != null) {
                // logToDisplay("I see a beacon again");
            } else {
                Log.d(TAG, "Sending notification.");
            }
        }

    }


    @Override
    public void didExitRegion(Region region) {

        Poi poi = activePois.get(region);
        pois.remove(poi);

        if (listItemDetail != null)
            listItemDetail.finish();

        if (monitoringActivity != null)
            monitoringActivity.updateData(pois);


        this.httpsUtil.notifyRegionExit(region);
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        if (state == 1) {
            new MediaActionSound().play(3);
        } else {
            new MediaActionSound().play(2);
        }
    }

    private void sendNotification(String title, String body) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "notificationChannel1")
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_launcher_foreground);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MonitoringActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void setMonitoringActivity(MonitoringActivity activity) {
        this.monitoringActivity = activity;
    }


    public void setListItemDetail(ListItemDetail listItemDetail) {
        this.listItemDetail = listItemDetail;
    }
}
