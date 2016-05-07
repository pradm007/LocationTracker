package mm.locationtracker.database.table;

/**
 * Created by Pradeep Mahato 007 on 07-May-16.
 */
public class LocationTable {

    int _id;
    double latitude;
    double longitude;
    double timestamp;

    public LocationTable() {
    }

    public LocationTable(double latitude, double longitude, double timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public LocationTable(int _id, double latitude, double longitude, double timestamp) {
        this._id = _id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }
}
