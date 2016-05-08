package mm.locationtracker.utility;

import android.content.Context;

import java.util.ArrayList;

import mm.locationtracker.database.helper.DatableHandler;
import mm.locationtracker.database.table.LocationTable;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class DumpKMLFileInvoker {

    Context context;

    public DumpKMLFileInvoker(Context context) {
        this.context = context;
    }

    public String dumpTheFile() {
        DatableHandler datableHandler =  new DatableHandler(context, "LOCATION_COORDINATES");
        ArrayList<LocationTable> locationTableArrayList =  datableHandler.getAllLocation();
        String filePath = "";

        if (locationTableArrayList.isEmpty()) {
            CustomToast.showToast(context, "No record found in DB !! :(");
        } else {
            filePath = KMLFileCreator.createKMLFile(locationTableArrayList);
            boolean status = !filePath.isEmpty();

            try {
                if (status) {
                    CustomToast.showToast(context, "File creation successfull");
                } else {
                    CustomToast.showToast(context, "File creation failed");
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }
}
