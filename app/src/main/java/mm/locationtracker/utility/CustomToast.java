package mm.locationtracker.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class CustomToast {

    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String message, int duration) {
        if (ApplicationState.getAccessLevel().equalsIgnoreCase(ApplicationState.FULL_ACCESS)) {
            Toast.makeText(context, message, duration).show();
        }
    }

}
