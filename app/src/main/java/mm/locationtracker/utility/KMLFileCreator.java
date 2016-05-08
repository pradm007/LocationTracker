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

    public static String createKMLFile(ArrayList<LocationTable> locationTableArrayList) {

        boolean status = true;
        String fileNameRevertBack = "";
        String timeStamp = System.currentTimeMillis() + "";

        String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n";

        String kmlelement = "", kmlelementStart = "";
        String kmlElementPrefix = "<Document>\n" +
                "    <name>MM Locations</name>\n" +
                "    <description>The cookie</description>\n" +
                "    <Style id=\"yellowLineGreenPoly\">\n" +
                "      <LineStyle>\n" +
                "        <color>7f00ffff</color>\n" +
                "        <width>4</width>\n" +
                "      </LineStyle>\n" +
                "      <PolyStyle>\n" +
                "        <color>7f00ff00</color>\n" +
                "      </PolyStyle>\n" +
                "    </Style>";

        String locationCordinates = "";

        for (int i = 0; i < 20; i++) {

            kmlelementStart = "\t<Placemark>\n" +
                    "\t<name>" + i + "</name>\n" +
                    "\t<description>" + "At " + CustomDate.getCurrentFormattedDate() + "</description>\n" +
                    "\t<LineString>\n" + "\t\t<coordinates>";

            LocationTable locationTable = locationTableArrayList.get(i);
            locationCordinates = locationTable.getLongitude() + "," + locationTable.getLatitude() + "," + 0;

            if (i < locationTableArrayList.size() - 2) {
                locationCordinates += " ";
            }

            kmlelement += kmlelementStart + locationCordinates + "</coordinates>\n" +
                    "\t\t<tessellate>1</tessellate>\n" +
                    "\t</LineString>\n" +
                    "\t</Placemark>\n";

        }

        kmlelement = kmlElementPrefix + kmlelement + "\t</Document>\n";
        String kmlend = "</kml>";

        ArrayList<String> content = new ArrayList<String>();
        content.add(0, kmlstart);
        content.add(1, kmlelement);
        content.add(2, kmlend);

        String kmltest = content.get(0) + content.get(1) + content.get(2);

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

        if (status) {
            fileNameRevertBack = fileName;
        } else {
            fileNameRevertBack = "";
        }
        return fileNameRevertBack;
    }

}
