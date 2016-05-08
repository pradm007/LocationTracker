package mm.locationtracker.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mm.locationtracker.database.table.LocationTable;

/**
 * Created by Pradeep Mahato 007 on 07-May-16.
 */
public class DatableHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Location Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TIMESTAMP = "timestamp";

    public DatableHandler(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    public DatableHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatableHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + getDatabaseName() + "(" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT,"
                +  KEY_TIMESTAMP + " TEXT" + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + getDatabaseName();
        db.execSQL(DROP_TABLE);

        //Create the table again
        onCreate(db);
    }

    // All CRUD operations

    /**
     * TO Add Location Values
     * @param locationTable
     */
    public void addLocation(LocationTable locationTable) {

        SQLiteDatabase  db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, locationTable.getLatitude());
        values.put(KEY_LONGITUDE, locationTable.getLongitude());
        values.put(KEY_TIMESTAMP, locationTable.getTimestamp());

        db.insert(getDatabaseName(), null, values);
        db.close();
    }

    public void deleteDb() {
        SQLiteDatabase  db = this.getWritableDatabase();

        onUpgrade(db, 0, 1);
    }

    public LocationTable getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(getDatabaseName(), new String[] {KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_TIMESTAMP},
                KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        LocationTable locationTable = new LocationTable(Integer.parseInt(cursor.getString(0)),
                cursor.getDouble(1), cursor.getDouble(2), cursor.getDouble(3));

        return locationTable;
    }

    public ArrayList<LocationTable> getAllLocation() {
        ArrayList<LocationTable> locationTableArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + getDatabaseName();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LocationTable locationTable =  new LocationTable();
                locationTable.set_id(Integer.parseInt(cursor.getString(0)));
                locationTable.setLatitude(cursor.getDouble(1));
                locationTable.setLongitude(cursor.getDouble(2));
                locationTable.setTimestamp(cursor.getDouble(3));

                locationTableArrayList.add(locationTable);
            } while (cursor.moveToNext());
        }

        return locationTableArrayList;
    }

}
