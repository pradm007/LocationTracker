package mm.locationtracker.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import mm.locationtracker.R;
import mm.locationtracker.service.GPSTrackerService;

public class MainActivity extends AppCompatActivity {

    GPSTrackerService gpsTrackerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsTrackerService = new GPSTrackerService(getApplicationContext());

        Button locationButton = (Button) findViewById(R.id.location_button);
        locationButton.setOnClickListener(locationButtonOnClick);

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
}
