package mm.locationtracker.service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import mm.locationtracker.database.helper.DatableHandler;
import mm.locationtracker.database.table.LocationTable;
import mm.locationtracker.utility.CustomConstants;
import mm.locationtracker.utility.CustomToast;

import static android.location.LocationManager.*;

/**
 * Created by Pradeep Mahato 007 on 07-May-16.
 */
public class GPSTrackerService extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    private static float LOCATION_ACCURACY = 10;//mtrs
    // The minimum distance to change Updates in meters
    private static float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0.10f; // 10 meters

    // The minimum time between updates in milliseconds
    private static long MIN_TIME_BW_UPDATES = 1000 * 2; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTrackerService(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = getLatitude();
        double longitude = getLongitude();

        if (latitude != -1.0 && longitude != -1.0) {
            String locationStr = "On LocationChanged Current location is \nLatitude : " + latitude + "\nLongitude : " + longitude;
            insertToDb(latitude, longitude);

            CustomToast.showToast(mContext, locationStr);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean canGetLocation() {
        return canGetLocation;
    }

    private void insertToDb(double latitude, double logitude) {
        DatableHandler datableHandler =  new DatableHandler(mContext, "LOCATION_COORDINATES");

        datableHandler.addLocation(new LocationTable(latitude, logitude, System.currentTimeMillis()));
    }

    /*
       Get the location from network provider first. If network provider is disabled, then we get the location from GPS provider
     */
    public Location getLocation() {

        try {

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                //Nothing is enabled...
            } else {
                canGetLocation = true;

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        Intent trackingIntent = new Intent(CustomConstants.SEND_TRACKING_MAIL);
                        sendBroadcast(trackingIntent);

                        return null;
                    }
                    locationManager.requestLocationUpdates(NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        location.setAccuracy(LOCATION_ACCURACY);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {

                    if (locationManager == null) {

                        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        location.setAccuracy(LOCATION_ACCURACY);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public double getLatitude() {
        if (location != null) {
            return location.getLatitude();
        }

        return -1.0;
    }

    public double getLongitude() {
        if (location != null) {
            return location.getLongitude();
        }

        return -1.0;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void setMinDistanceChangeForUpdates(Context context, String minDistanceChangeForUpdates) {
        try {
            float minDistanceChangeForUpdatesFloat = Float.parseFloat(minDistanceChangeForUpdates);
            MIN_DISTANCE_CHANGE_FOR_UPDATES = minDistanceChangeForUpdatesFloat;
            getLocation();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            CustomToast.showToast(context, "Invalid input");
        }
    }

    public void setMinTimeBwUpdates(Context context, String minTimeBwUpdates) {
        try {
            long minTimeBwUpdatesLong = Long.parseLong(minTimeBwUpdates);
            MIN_TIME_BW_UPDATES = minTimeBwUpdatesLong;
            getLocation();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            CustomToast.showToast(context, "Invalid input");
        }
    }
}
