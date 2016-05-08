package mm.locationtracker.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class CustomDate {

    public static String getCurrentFormattedDate() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

}
