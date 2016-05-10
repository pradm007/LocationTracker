package mm.locationtracker.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import mm.locationtracker.R;
import mm.locationtracker.service.GPSTrackerService;
import mm.locationtracker.service.TrackingNotifierService;
import mm.locationtracker.utility.ApplicationState;
import mm.locationtracker.utility.CustomConstants;
import mm.locationtracker.utility.CustomToast;
import mm.locationtracker.utility.DumpKMLFileInvoker;
import mm.locationtracker.utility.SendMailInvoker;

/**
 * Created by Pradeep Mahato 007 on 07-May-16.
 */
public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getSimpleName();

    GPSTrackerService gpsTrackerService;

    Button locationButton;
    Button dumpKMLFile;

    LinearLayout pinContainer;
    EditText pinEditTextView;
    Button submitPin;
    Button sendMail;
    Button hideMe;
    Button sendSMS;
    Button requestAccess;

    EditText minDistEdtV;
    Button submitMinDisEdtV;
    EditText minTimeEdtV;
    Button submitMinTimeEdtV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsTrackerService = new GPSTrackerService(getApplicationContext());
        ApplicationState.setAccessLevel(ApplicationState.LEAST_ACCESS);

        locationButton = (Button) findViewById(R.id.location_button);
        locationButton.setOnClickListener(locationButtonOnClick);

        dumpKMLFile = (Button) findViewById(R.id.location_dump_kml_file);
        dumpKMLFile.setOnClickListener(dumpKMLFileListener);

        pinContainer = (LinearLayout) findViewById(R.id.pinContainer);
        pinEditTextView = (EditText) findViewById(R.id.pinTextView);
        submitPin = (Button) findViewById(R.id.submitPin);
        submitPin.setOnClickListener(submitPinClickListener);

        sendMail = (Button) findViewById(R.id.sendMail);
        sendMail.setOnClickListener(sendMailClickListener);

        minDistEdtV = (EditText) findViewById(R.id.minDistEdtV);
        submitMinDisEdtV = (Button) findViewById(R.id.submitMinDisEdtV);
        submitMinDisEdtV.setOnClickListener(submitMinDisEdtVClickListner);

        minTimeEdtV = (EditText) findViewById(R.id.minTimeEdtV);
        submitMinTimeEdtV = (Button) findViewById(R.id.submitMinTimeEdtV);
        submitMinTimeEdtV.setOnClickListener(submitMinTimeEdtVClickListner);

        hideMe = (Button) findViewById(R.id.hideMe);
        hideMe.setOnClickListener(hideMeListner);

        sendSMS = (Button) findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(sendSMSListener);

        requestAccess = (Button) findViewById(R.id.requestAccess);
        requestAccess.setOnClickListener(requestAccessListener);

        Intent trackingNotifierService = new Intent(getApplicationContext(), TrackingNotifierService.class);
        startService(trackingNotifierService);

        registerReceiver(broadcastReceiver, new IntentFilter(CustomConstants.SEND_TRACKING_MAIL));
    }

    private void spyMode() {

        ApplicationState.setAccessLevel(ApplicationState.LEAST_ACCESS);

        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, mm.locationtracker.activity.MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        SendMailInvoker sendMailInvoker = new SendMailInvoker(MainActivity.this);
        sendMailInvoker.sendMail();

        finish();
    }

    private void sendSMS() {

        if (gpsTrackerService.canGetLocation()) {
            double latitude = gpsTrackerService.getLatitude();
            double longitude = gpsTrackerService.getLongitude();

            String locationStr = "Current location is \nLatitude : " + latitude + "\nLongitude : " + longitude;

            CustomToast.showToast(getApplicationContext(), locationStr);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+919703980576", null, locationStr, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            gpsTrackerService.showSettingsAlert();
            CustomToast.showToast(getApplicationContext(), "Cannot get location");
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    CustomConstants.TRACKING_LOCATION_REQUEST_CODE);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CustomConstants.TRACKING_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    gpsTrackerService = new GPSTrackerService(getApplicationContext());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    View.OnClickListener hideMeListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CustomToast.showToast(getApplicationContext(), "Bye bye !! :)");
            spyMode();
        }
    };

    View.OnClickListener sendSMSListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendSMS();
        }
    };

    View.OnClickListener requestAccessListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gpsTrackerService = new GPSTrackerService(getApplicationContext());
        }
    };

    View.OnClickListener submitMinDisEdtVClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gpsTrackerService.setMinDistanceChangeForUpdates(getApplicationContext(), minDistEdtV.getText().toString());
        }
    };

    View.OnClickListener submitMinTimeEdtVClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gpsTrackerService.setMinTimeBwUpdates(getApplicationContext(), minTimeEdtV.getText().toString());
        }
    };

    View.OnClickListener locationButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (gpsTrackerService.canGetLocation()) {
                gpsTrackerService.getLocation();
                double latitude = gpsTrackerService.getLatitude();
                double longitude = gpsTrackerService.getLongitude();

                String locationStr = "Current location is \nLatitude : " + latitude + "\nLongitude : " + longitude;

                Log.i(TAG, locationStr);
                CustomToast.showToast(getApplicationContext(), locationStr);
            } else {
                gpsTrackerService.showSettingsAlert();
                CustomToast.showToast(getApplicationContext(), "Cannot get location");
            }
        }
    };

    View.OnClickListener dumpKMLFileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DumpKMLFileInvoker dumpKMLFileInvoker = new DumpKMLFileInvoker(MainActivity.this);
            dumpKMLFileInvoker.dumpTheFile();
        }
    };

    View.OnClickListener sendMailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SendMailInvoker sendMailInvoker = new SendMailInvoker(MainActivity.this);
            sendMailInvoker.sendMail();
        }
    };


    View.OnClickListener submitPinClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (pinEditTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.correct_pin))) {
                revealContents();
            } else {
                hideContents();
            }
        }
    };

    private void hideContents() {
        pinContainer.setVisibility(View.GONE);
        locationButton.setText(getResources().getString(R.string.get_current_location_hidden));
        dumpKMLFile.setText(getResources().getString(R.string.create_dump_file_hidden));
        locationButton.setVisibility(View.GONE);
        dumpKMLFile.setVisibility(View.GONE);
        sendMail.setVisibility(View.GONE);
        sendSMS.setVisibility(View.GONE);
        ApplicationState.setAccessLevel(ApplicationState.LEAST_ACCESS);

        hideKeyboard();

        SendMailInvoker sendMailInvoker = new SendMailInvoker(MainActivity.this);
        sendMailInvoker.sendMail();

        finish();
    }

    private void revealContents() {
        pinContainer.setVisibility(View.GONE);
        locationButton.setText(getResources().getString(R.string.get_current_location));
        dumpKMLFile.setText(getResources().getString(R.string.create_dump_file));
        locationButton.setVisibility(View.VISIBLE);
        dumpKMLFile.setVisibility(View.VISIBLE);
        sendMail.setVisibility(View.VISIBLE);
        hideMe.setVisibility(View.VISIBLE);
        sendSMS.setVisibility(View.VISIBLE);
        ApplicationState.setAccessLevel(ApplicationState.FULL_ACCESS);

        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
