package mm.locationtracker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    GPSTrackerService gpsTrackerService;

    Button locationButton;
    Button dumpKMLFile;

    LinearLayout pinContainer;
    EditText pinEditTextView;
    Button submitPin;
    Button sendMail;

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

        Intent trackingNotifierService = new Intent(getApplicationContext(), TrackingNotifierService.class);
        startService(trackingNotifierService);

        registerReceiver(broadcastReceiver, new IntentFilter(CustomConstants.SEND_TRACKING_MAIL));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SendMailInvoker sendMailInvoker = new SendMailInvoker(MainActivity.this);
            sendMailInvoker.sendMail();
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
                double latitude = gpsTrackerService.getLatitude();
                double longitude = gpsTrackerService.getLongitude();

                String locationStr = "Current location is \nLatitude : " + latitude + "\nLongitude : " + longitude;

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
        ApplicationState.setAccessLevel(ApplicationState.FULL_ACCESS);

        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
