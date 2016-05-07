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

                Toast.makeText(getApplicationContext(), locationStr, Toast.LENGTH_SHORT).show();
            } else {
                gpsTrackerService.showSettingsAlert();
                Toast.makeText(getApplicationContext(), "Cannot get location !! :(", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener dumpKMLFileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatableHandler datableHandler =  new DatableHandler(getApplicationContext(), "LOCATION_COORDINATES");
            ArrayList<LocationTable> locationTableArrayList =  datableHandler.getAllLocation();

            if (locationTableArrayList.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No record found in DB !! :(", Toast.LENGTH_SHORT).show();
            } else {
                boolean status = KMLFileCreator.createKMLFile(locationTableArrayList);

                if (status) {
                    Toast.makeText(getApplicationContext(), "File creation successfull", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "File creation failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
