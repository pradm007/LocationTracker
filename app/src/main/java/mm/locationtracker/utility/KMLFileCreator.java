package mm.locationtracker.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import mm.locationtracker.database.table.LocationTable;

/**
 * Created by Pradeep Mahato 007 on 07-May-16.
 */
public class KMLFileCreator {

    static String filePath = "/sdcard/";
    static String filePrefix = "LocationTracker/location_history";

    public static boolean createKMLFile(ArrayList<LocationTable> locationTableArrayList) {

        boolean status = true;

        String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n";

        String kmlelement = "", kmlelementStart = "\t<Placemark>\n" +
                "\t<name>Simple placemark</name>\n" +
                "\t<description>" + "location history" + "</description>\n" +
                "\t<Point>\n" + "\t\t<coordinates>";
        String locationCordinates = "";

        for (int i = 0; i < locationTableArrayList.size(); i++) {
            LocationTable locationTable = locationTableArrayList.get(i);
            locationCordinates += locationTable.getLatitude() + "," + locationTable.getLongitude() + "," + 0;

            if (i < locationTableArrayList.size() - 2) {
                locationCordinates += " ";
            }
        }

        kmlelement = kmlelementStart + locationCordinates + "</coordinates>\n" +
                "\t</Point>\n" +
                "\t</Placemark>\n";

        String kmlend = "</kml>";

        ArrayList<String> content = new ArrayList<String>();
        content.add(0, kmlstart);
        content.add(1, kmlelement);
        content.add(2, kmlend);

        String kmltest = content.get(0) + content.get(1) + content.get(2);

        String timeStamp = System.currentTimeMillis() + "";
        String fileName = filePath + filePrefix + timeStamp + ".kml";
        File testexists = new File(fileName);

        if (!testexists.exists()) {
            new File(testexists.getParent()).mkdirs();
        }
        try {
            testexists.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            status = false;
        }

        Writer fwriter;

        try {

            fwriter = new FileWriter(fileName);
            fwriter.write(kmltest);
            fwriter.flush();
            fwriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            status = false;
        }

        /*if (!testexists.exists()) {
            try {

                fwriter = new FileWriter(fileName);
                fwriter.write(kmltest);
                fwriter.flush();
                fwriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                status = false;
            }
        } else {

            String filecontent = "";

            ArrayList<String> newoutput = new ArrayList<String>();

            try {
                BufferedReader in = new BufferedReader(new FileReader(testexists));
                while ((filecontent = in.readLine()) != null)

                    newoutput.add(filecontent);

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                status = false;
            } catch (IOException e1) {
                e1.printStackTrace();
                status = false;
            }

            newoutput.add(2, kmlelement);

            String rewrite = "";
            for (String s : newoutput) {
                rewrite += s;
            }

            try {
                fwriter = new FileWriter(fileName);
                fwriter.write(rewrite);
                fwriter.flush();
                fwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                status = false;
            }

        }*/

        return status;
    }

}