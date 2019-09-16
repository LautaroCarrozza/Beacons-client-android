package com.example.beaconsandroid.https;


import android.app.DownloadManager;
import android.media.MediaActionSound;

import org.altbeacon.beacon.Region;

public class Https {

    public static void notifyRegionEnetered(Region region){
        new MediaActionSound().play(3);

    }
    public static void notifyRegionExit(Region region){
        new MediaActionSound().play(2);
    }


}
