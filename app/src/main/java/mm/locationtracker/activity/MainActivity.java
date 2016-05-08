package mm.locationtracker.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import mm.locationtracker.R;
import mm.locationtracker.database.helper.DatableHandler;
import mm.locationtracker.database.table.LocationTable;
import mm.locationtracker.service.GPSTrackerService;
import mm.locationtracker.utility.ApplicationState;
import mm.locationtracker.utility.CustomToast;
import mm.locationtracker.utility.KMLFileCreator;

public class MainActivity extends AppCompatActivity {

    GPSTrackerService gpsTrackerService;

    Button locationButton;
    Button dumpKMLFile;

    LinearLayout pinContainer;
    EditText pinEditTextView;
    Button submitPin;

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
    }

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
            DatableHandler datableHandler =  new DatableHandler(getApplicationContext(), "LOCATION_COORDINATES");
            ArrayList<LocationTable> locationTableArrayList =  datableHandler.getAllLocation();

            if (locationTableArrayList.isEmpty()) {
                CustomToast.showToast(getApplicationContext(), "No record found in DB !! :(");
            } else {
                boolean status = KMLFileCreator.createKMLFile(locationTableArrayList);

                if (status) {
                    CustomToast.showToast(getApplicationContext(), "File creation successfull");
                } else {
                    CustomToast.showToast(getApplicationContext(), "File creation failed");
                }
            }
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
        locationButton.setVisibility(View.VISIBLE);
        dumpKMLFile.setVisibility(View.VISIBLE);
        ApplicationState.setAccessLevel(ApplicationState.LEAST_ACCESS);

        hideKeyboard();
    }

    private void revealContents() {
        pinContainer.setVisibility(View.GONE);
        locationButton.setText(getResources().getString(R.string.get_current_location));
        dumpKMLFile.setText(getResources().getString(R.string.create_dump_file));
        locationButton.setVisibility(View.VISIBLE);
        dumpKMLFile.setVisibility(View.VISIBLE);
        ApplicationState.setAccessLevel(ApplicationState.FULL_ACCESS);

        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
