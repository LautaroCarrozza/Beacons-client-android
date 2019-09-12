    package com.example.beaconsandroid.permissions;


    import android.Manifest;
    import android.content.pm.PackageManager;
    import android.os.Build;

    import androidx.appcompat.app.AlertDialog;

    import com.example.beaconsandroid.MainActivity;

    public class PermissionsUtil {

        private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

        public static void reqPermissions(MainActivity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("This app needs location access");
                    builder.setMessage("Please grant location access so this app can detect beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION));
                    builder.show();
                }
            }
        }
    }