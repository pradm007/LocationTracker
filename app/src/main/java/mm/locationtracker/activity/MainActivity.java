package mm.locationtracker.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import mm.locationtracker.R;
import mm.locationtracker.database.helper.DatableHandler;
import mm.locationtracker.database.table.LocationTable;
import mm.locationtracker.service.GPSTrackerService;
import mm.locationtracker.utility.CustomToast;
import mm.locationtracker.utility.KMLFileCreator;

public class MainActivity extends AppCompatActivity {

    GPSTrackerService gpsTrackerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsTrackerService = new GPSTrackerService(getApplicationContext());

        Button locationButton = (Button) findViewById(R.id.location_button);
        locationButton.setOnClickListener(locationButtonOnClick);

        Button dumpKMLFile = (Button) findViewById(R.id.location_dump_kml_file);
        dumpKMLFile.setOnClickListener(dumpKMLFileListener);

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
}
