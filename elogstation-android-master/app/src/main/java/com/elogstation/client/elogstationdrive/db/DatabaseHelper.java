package com.elogstation.client.elogstationdrive.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.elogstation.client.elogstationdrive.Constants;
import com.elogstation.client.elogstationdrive.httpclient.HttpPostAsyncTask;
import com.iosix.eldblelib.EldDataRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "elogstation_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create tables
        db.execSQL(EldModel.CREATE_TABLE);
        db.execSQL(DefaultEldModel.CREATE_TABLE);
        db.execSQL(DeviceModel.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + EldModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DefaultEldModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DeviceModel.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public int countDeviceTrackingUploaded(){
        String selectQuery = "SELECT  COUNT(*) FROM " + DeviceModel.TABLE_NAME  + " where " +
                DeviceModel.COLUMN_uploaded + "= 'true'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                return cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return 0;
    }
    public int countDeviceTrackingTotal(){

        String selectQuery = "SELECT  COUNT(*) FROM " + DeviceModel.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                return cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return 0;
    }

    public List<DeviceModel> selectDeviceTrackingNotUploaded(){

        List<DeviceModel> deviceModels = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DeviceModel.TABLE_NAME + " where " +
                DeviceModel.COLUMN_uploaded + "= 'false' limit " + Constants.uploadLimit;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                try {

                    DeviceModel deviceModel = new DeviceModel(
                            cursor.getInt(cursor.getColumnIndex(DeviceModel.COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(DeviceModel.COLUMN_STATUSTYPE)),
                            cursor.getString(cursor.getColumnIndex(DeviceModel.COLUMN_ELDID)),
                            cursor.getString(cursor.getColumnIndex(DeviceModel.COLUMN_vin)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_rpm)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_speed)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_odometer)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_tripDistance)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_engineHours)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_tripHours)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_voltage)),
                            cursor.getString(cursor.getColumnIndex(DeviceModel.COLUMN_gpsDateTime)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_latitude)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_longitude)),
                            cursor.getInt(cursor.getColumnIndex(DeviceModel.COLUMN_gpsSpeed)),
                            cursor.getInt(cursor.getColumnIndex(DeviceModel.COLUMN_course)),
                            cursor.getInt(cursor.getColumnIndex(DeviceModel.COLUMN_numSats)),
                            cursor.getInt(cursor.getColumnIndex(DeviceModel.COLUMN_mslAlt)),
                            cursor.getDouble(cursor.getColumnIndex(DeviceModel.COLUMN_dop)),
                            cursor.getInt(cursor.getColumnIndex(DeviceModel.COLUMN_sequence)),
                            cursor.getString(cursor.getColumnIndex(DeviceModel.COLUMN_firmwareVersion)),
                            cursor.getString(cursor.getColumnIndex(DeviceModel.COLUMN_uploaded))

                    );


                    deviceModels.add(deviceModel);
                }catch (Exception e){
                    Log.d("error","ere");
                }
            } while (cursor.moveToNext());
        }
        return deviceModels;
    }

    public int updateDeviceTrackingUploaded(String[] ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DeviceModel.COLUMN_uploaded, "true");

        String joined = TextUtils.join(",", ids);

        // updating row
        return db.update(DeviceModel.TABLE_NAME, values, DeviceModel.COLUMN_ID + " in (" +
        joined +")",
                null);
    }

    public void insertDeviceTracking(EldDataRecord eldDataRecord, String eldSelectedFromAPIBluetooth) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
//        values.put(DeviceModel.COLUMN_ID, );
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.dateTimeFormat);
        String date = sdf.format(eldDataRecord.getGpsDateTime());

        values.put(DeviceModel.COLUMN_STATUSTYPE, HttpPostAsyncTask.statusType);
        values.put(DeviceModel.COLUMN_ELDID, eldSelectedFromAPIBluetooth);
        values.put(DeviceModel.COLUMN_vin, eldDataRecord.getVin());
        values.put(DeviceModel.COLUMN_rpm, eldDataRecord.getRpm());
        values.put(DeviceModel.COLUMN_speed, eldDataRecord.getSpeed());
        values.put(DeviceModel.COLUMN_odometer, eldDataRecord.getOdometer());
        values.put(DeviceModel.COLUMN_tripDistance, eldDataRecord.getTripDistance());
        values.put(DeviceModel.COLUMN_engineHours, eldDataRecord.getEngineHours());
        values.put(DeviceModel.COLUMN_tripHours, eldDataRecord.getTripHours());
        values.put(DeviceModel.COLUMN_voltage, eldDataRecord.getVoltage());
        values.put(DeviceModel.COLUMN_gpsDateTime, date);
        values.put(DeviceModel.COLUMN_latitude, eldDataRecord.getLattitude());
        values.put(DeviceModel.COLUMN_longitude, eldDataRecord.getLongitude());
        values.put(DeviceModel.COLUMN_gpsSpeed, eldDataRecord.getGpsSpeed());
        values.put(DeviceModel.COLUMN_course, eldDataRecord.getCourse());
        values.put(DeviceModel.COLUMN_numSats, eldDataRecord.getNumSats());
        values.put(DeviceModel.COLUMN_mslAlt, eldDataRecord.getMslAlt());
        values.put(DeviceModel.COLUMN_dop, eldDataRecord.getDop());
        values.put(DeviceModel.COLUMN_sequence, eldDataRecord.getSequence());
        values.put(DeviceModel.COLUMN_firmwareVersion, eldDataRecord.getFirmwareVersion());
        values.put(DeviceModel.COLUMN_uploaded, "false");

        // insert row
        db.insert(DeviceModel.TABLE_NAME, null, values);

        // close db connection
        db.close();

    }

    public long insertEld(Long id, String eldId, String name) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(EldModel.COLUMN_ID, id);
        values.put(EldModel.COLUMN_ELDID, eldId);
        values.put(EldModel.COLUMN_ELDNAME, name);

        // insert row
        db.insert(EldModel.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public long insertDefaultEldAPI(String eldId) {
        return insertDefaultEld(0l, eldId);
    }

//    public long insertDefaultEldBluetooth(String eldId) {
//        return insertDefaultEld(1l, eldId);
//    }

    public long insertDefaultEld(Long id, String eldId) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(EldModel.COLUMN_ELDID, eldId);

        // insert row
        values.put(EldModel.COLUMN_ID, id);
        long rowInserted = db.insert(DefaultEldModel.TABLE_NAME, null, values);
        if(rowInserted == -1){
            db.update(DefaultEldModel.TABLE_NAME, values, DefaultEldModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        }

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public DefaultEldModel getDefaultEldAPI(){
        return getDefaultEld(0l);
    }

//    public DefaultEldModel getDefaultEldBluetooth(){
//        return getDefaultEld(1l);
//    }

    public DefaultEldModel getDefaultEld(Long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();
        DefaultEldModel eld = new DefaultEldModel();
        try {
            Cursor cursor = db.query(DefaultEldModel.TABLE_NAME,
                    new String[]{EldModel.COLUMN_ID, EldModel.COLUMN_ELDID},
                    EldModel.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null)
                cursor.moveToFirst();

            // prepare note object
            eld = new DefaultEldModel(
                    cursor.getInt(cursor.getColumnIndex(DefaultEldModel.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(DefaultEldModel.COLUMN_ELDID)));

            // close the db connection
            cursor.close();
        }catch ( Exception e){
            return null;
        }

        return eld;
    }

    public EldModel getEld(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(EldModel.TABLE_NAME,
                new String[]{EldModel.COLUMN_ID, EldModel.COLUMN_ELDID, EldModel.COLUMN_ELDNAME},
                EldModel.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        EldModel eld = new EldModel(
                cursor.getInt(cursor.getColumnIndex(EldModel.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(EldModel.COLUMN_ELDID)),
                cursor.getString(cursor.getColumnIndex(EldModel.COLUMN_ELDNAME)));

        // close the db connection
        cursor.close();

        return eld;
    }

    public List<EldModel> getAllElds() {
        List<EldModel> elds = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + EldModel.TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                EldModel eld = new EldModel();
                eld.setId(cursor.getInt(cursor.getColumnIndex(EldModel.COLUMN_ID)));
                eld.setEldId(cursor.getString(cursor.getColumnIndex(EldModel.COLUMN_ELDID)));
                eld.setName(cursor.getString(cursor.getColumnIndex(EldModel.COLUMN_ELDNAME)));

                elds.add(eld);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return elds;
    }

    public List<String> getAllEldsString() {
        List<String> elds = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + EldModel.TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                elds.add(cursor.getString(cursor.getColumnIndex(EldModel.COLUMN_ELDID)));
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return elds;
    }

    public int getEldsCount() {
        String countQuery = "SELECT  * FROM " + EldModel.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateEld(EldModel eld) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EldModel.COLUMN_ELDNAME, eld.getName());
        values.put(EldModel.COLUMN_ELDID, eld.getEldId());

        // updating row
        return db.update(EldModel.TABLE_NAME, values, EldModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(eld.getId())});
    }

    public void deleteEld(EldModel eld) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EldModel.TABLE_NAME, EldModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(eld.getId())});
        db.close();
    }

    public void deleteAllElds(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EldModel.TABLE_NAME, null, null);
        db.close();
    }


    public void deleteDefaultElds(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DefaultEldModel.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteDeviceModel(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DeviceModel.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAll(){
        deleteAllElds();
        deleteDefaultElds();
        deleteDeviceModel();
    }
}
