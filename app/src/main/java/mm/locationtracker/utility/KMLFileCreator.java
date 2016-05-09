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

        kmlelement = getDataConstructed(locationTableArrayList) + "\t</Document>\n";
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

    private static String getDataConstructed(ArrayList<LocationTable> locationTableArrayList) {

        String kmlElement = "";

        String kmlElementPrefix = "<Document>\n" +
                "\t\t<name> " + "History till " + CustomDate.getCurrentFormattedDate() + " </name>\n" +
                "\t\t<description><![CDATA[]]></description>\n";

        String locationCordinates = "";

        String kmlelementStart = "\t\t<Folder>\n" + "\t\t\t<name> Tracker Layer </name>\n";
        String kmlelementInternalPoint = "";

        // For point marker
        for (int i = 0; i < locationTableArrayList.size(); i++) {

            kmlelementInternalPoint += "\t\t\t<Placemark>\n" +
                    "\t\t\t\t<name>Point " + i + " </name>\n" +
                    "\t\t\t\t<description><![CDATA[" + CustomDate.getCurrentFormattedDate() + "]]></description>\n" +
                    "\t\t\t\t<styleUrl>#icon-503-DB4436</styleUrl>\n" +
                    "\t\t\t\t<Point>\n" +
                    "\t\t\t\t\t<coordinates>";
            LocationTable locationTable = locationTableArrayList.get(i);
            locationCordinates = locationTable.getLongitude() + "," + locationTable.getLatitude() + "," + "0.0";

            kmlelementInternalPoint +=  locationCordinates + "</coordinates>\n\t\t\t\t</Point>\n" +
                    "\t\t\t</Placemark>\n";

        }

        String kmlelementInternalPoint2 = "\t\t\t<Placemark>\n" +
                "\t\t\t\t<name>Line 4</name>\n" +
                "\t\t\t\t<styleUrl>#line-000000-1-nodesc</styleUrl>\n" +
                "\t\t\t\t<LineString>\n" +
                "\t\t\t\t\t<tessellate>1</tessellate>\n" +
                "\t\t\t\t\t<coordinates>";
        locationCordinates = "";
        //For line marker
        for (int i = 0; i < locationTableArrayList.size(); i++) {
            LocationTable locationTable = locationTableArrayList.get(i);
            locationCordinates += locationTable.getLongitude() + "," + locationTable.getLatitude() + "," + "0.0 ";
        }

        kmlelementInternalPoint2 += locationCordinates + "</coordinates>\n" +
                "\t\t\t\t</LineString>\n" +
                "\t\t\t</Placemark>";
        String kmlelementEnd = "\t\t</Folder>\n" +
                "\t\t<Style id='icon-503-DB4436-normal'>\n" +
                "\t\t\t<IconStyle>\n" +
                "\t\t\t\t<color>ff3644DB</color>\n" +
                "\t\t\t\t<scale>1.1</scale>\n" +
                "\t\t\t\t<Icon>\n" +
                "\t\t\t\t\t<href>http://www.gstatic.com/mapspro/images/stock/503-wht-blank_maps.png</href>\n" +
                "\t\t\t\t</Icon>\n" +
                "\t\t\t\t<hotSpot x='16' y='31' xunits='pixels' yunits='insetPixels'>\n" +
                "\t\t\t\t</hotSpot>\n" +
                "\t\t\t</IconStyle>\n" +
                "\t\t\t<LabelStyle>\n" +
                "\t\t\t\t<scale>0.0</scale>\n" +
                "\t\t\t</LabelStyle>\n" +
                "\t\t</Style>\n" +
                "\t\t<Style id='icon-503-DB4436-highlight'>\n" +
                "\t\t\t<IconStyle>\n" +
                "\t\t\t\t<color>ff3644DB</color>\n" +
                "\t\t\t\t<scale>1.1</scale>\n" +
                "\t\t\t\t<Icon>\n" +
                "\t\t\t\t\t<href>http://www.gstatic.com/mapspro/images/stock/503-wht-blank_maps.png</href>\n" +
                "\t\t\t\t</Icon>\n" +
                "\t\t\t\t<hotSpot x='16' y='31' xunits='pixels' yunits='insetPixels'>\n" +
                "\t\t\t\t</hotSpot>\n" +
                "\t\t\t</IconStyle>\n" +
                "\t\t\t<LabelStyle>\n" +
                "\t\t\t\t<scale>1.1</scale>\n" +
                "\t\t\t</LabelStyle>\n" +
                "\t\t</Style>\n" +
                "\t\t<StyleMap id='icon-503-DB4436'>\n" +
                "\t\t\t<Pair>\n" +
                "\t\t\t\t<key>normal</key>\n" +
                "\t\t\t\t<styleUrl>#icon-503-DB4436-normal</styleUrl>\n" +
                "\t\t\t</Pair>\n" +
                "\t\t\t<Pair>\n" +
                "\t\t\t\t<key>highlight</key>\n" +
                "\t\t\t\t<styleUrl>#icon-503-DB4436-highlight</styleUrl>\n" +
                "\t\t\t</Pair>\n" +
                "\t\t</StyleMap>\n" +
                "\t\t<Style id='line-000000-1-nodesc-normal'>\n" +
                "\t\t\t<LineStyle>\n" +
                "\t\t\t\t<color>ff000000</color>\n" +
                "\t\t\t\t<width>1</width>\n" +
                "\t\t\t</LineStyle>\n" +
                "\t\t\t<BalloonStyle>\n" +
                "\t\t\t\t<text><![CDATA[<h3>$[name]</h3>]]></text>\n" +
                "\t\t\t</BalloonStyle>\n" +
                "\t\t</Style>\n" +
                "\t\t<Style id='line-000000-1-nodesc-highlight'>\n" +
                "\t\t\t<LineStyle>\n" +
                "\t\t\t\t<color>ff000000</color>\n" +
                "\t\t\t\t<width>2.0</width>\n" +
                "\t\t\t</LineStyle>\n" +
                "\t\t\t<BalloonStyle>\n" +
                "\t\t\t\t<text><![CDATA[<h3>$[name]</h3>]]></text>\n" +
                "\t\t\t</BalloonStyle>\n" +
                "\t\t</Style>\n" +
                "\t\t<StyleMap id='line-000000-1-nodesc'>\n" +
                "\t\t\t<Pair>\n" +
                "\t\t\t\t<key>normal</key>\n" +
                "\t\t\t\t<styleUrl>#line-000000-1-nodesc-normal</styleUrl>\n" +
                "\t\t\t</Pair>\n" +
                "\t\t\t<Pair>\n" +
                "\t\t\t\t<key>highlight</key>\n" +
                "\t\t\t\t<styleUrl>#line-000000-1-nodesc-highlight</styleUrl>\n" +
                "\t\t\t</Pair>\n" +
                "\t\t</StyleMap>";

        kmlElement = kmlElementPrefix + kmlelementStart + kmlelementInternalPoint + kmlelementInternalPoint2 + kmlelementEnd;


        return kmlElement;
    }


}
