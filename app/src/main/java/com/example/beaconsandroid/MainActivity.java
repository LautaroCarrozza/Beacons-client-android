package com.example.beaconsandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beaconsandroid.https.Https;
import com.example.beaconsandroid.permissions.PermissionsUtil;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static boolean started = false;
    private BeaconManager beaconManager;
    protected static final String TAG = "MonitoringActivity";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private static final String REGION_1 = "F57D5029-A509-4BC7-A5C0-444F8EB88EEE";
    private static final String REGION_2 = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6";
    private static final List<Region> regions = Arrays.asList(
            new Region("REGION_1", Identifier.fromUuid(UUID.fromString(REGION_1)), null, null),
            new Region("REGION_2", Identifier.fromUuid(UUID.fromString(REGION_2)), null, null)
    );


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        startService(new Intent(this, MyService.class));

        PermissionsUtil.reqPermissions(this);



        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.start_button);
        TextView mainMessage = findViewById(R.id.main_message);


        button.setOnClickListener(view -> {
            if (started){
                mainMessage.setText("Started");
                button.setText("Stop");
                new MediaActionSound().play(3);

            }else {
                mainMessage.setText("Stopped");
                button.setText("Start");
                new MediaActionSound().play(2);

            }
            started = !started;
        });

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();

        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                Https.notifyRegionEnetered(region);
                System.out.println("FOUND BEACON IN: " + region.getUniqueId());

            }

            @Override
            public void didExitRegion(Region region) {
                Https.notifyRegionExit(region);
                System.out.println("LOST BEACON IN: " + region.getUniqueId());
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + (state == 1 ? "Seeing":"Not seeing"));
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + (state == 1 ? "Seeing":"Not seeing"));

            }
        });


            regions.forEach(region -> {
                try {
                    beaconManager.startMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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


}
