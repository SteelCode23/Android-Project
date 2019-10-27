package com.elogstation.client.elogstationdrive.db;

import java.util.Date;

public class DeviceModel {

    public static final String TABLE_NAME = "devicetrackings";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STATUSTYPE = "statusType";
    public static final String COLUMN_ELDID = "eldId";
    public static final String COLUMN_vin = "vin";
    public static final String COLUMN_rpm = "rpm";
    public static final String COLUMN_speed = "speeed";
    public static final String COLUMN_odometer = "odometer";
    public static final String COLUMN_tripDistance = "tripDistance";
    public static final String COLUMN_engineHours = "engineHours";
    public static final String COLUMN_tripHours = "tripHours";
    public static final String COLUMN_voltage = "voltage";
    public static final String COLUMN_gpsDateTime = "gpsDateTime";
    public static final String COLUMN_latitude = "latitude";
    public static final String COLUMN_longitude = "longitude";
    public static final String COLUMN_gpsSpeed = "gpsSpeed";
    public static final String COLUMN_course = "course";
    public static final String COLUMN_numSats = "numSats";
    public static final String COLUMN_mslAlt = "mslAlt";
    public static final String COLUMN_dop = "dop";
    public static final String COLUMN_sequence = "sequence";
    public static final String COLUMN_firmwareVersion = "firmwareVersion";
    public static final String COLUMN_uploaded = "uploaded";

    private int id;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_STATUSTYPE + " TEXT,"
                    + COLUMN_ELDID + " TEXT,"
                    + COLUMN_vin + " DOUBLE,"
                    + COLUMN_rpm + " DOUBLE,"
                    + COLUMN_speed + " DOUBLE,"
                    + COLUMN_odometer + " DOUBLE,"
                    + COLUMN_tripDistance + " DOUBLE,"
                    + COLUMN_engineHours + " DOUBLE,"
                    + COLUMN_tripHours + " DOUBLE,"
                    + COLUMN_voltage + " DOUBLE,"
                    + COLUMN_gpsDateTime + " DATE,"
                    + COLUMN_latitude + " DOUBLE,"
                    + COLUMN_longitude + " DOUBLE,"
                    + COLUMN_gpsSpeed + " LONG,"
                    + COLUMN_course + " LONG,"
                    + COLUMN_numSats + " LONG,"
                    + COLUMN_mslAlt + " LONG,"
                    + COLUMN_dop + " DOUBLE,"
                    + COLUMN_sequence + " LONG,"
                    + COLUMN_firmwareVersion + " TEXT,"
                    + COLUMN_uploaded + " TEXT"
                    + ")";

    public DeviceModel(int id, String statusType, String eldId, String vin, double rpm, double speed, double odometer, double tripDistance, double engineHours, double tripHours, double voltage, String gpsDateTime, double latitude, double longitude, int gpsSpeed, int course, int numSats, int mslAlt, double dop, int sequence, String firmwareVersion, String uploaded) {
        this.id = id;
        this.statusType = statusType;
        this.eldId = eldId;
        this.vin = vin;
        this.rpm = rpm;
        this.speed = speed;
        this.odometer = odometer;
        this.tripDistance = tripDistance;
        this.engineHours = engineHours;
        this.tripHours = tripHours;
        this.voltage = voltage;
        this.gpsDateTime = gpsDateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gpsSpeed = gpsSpeed;
        this.course = course;
        this.numSats = numSats;
        this.mslAlt = mslAlt;
        this.dop = dop;
        this.sequence = sequence;
        this.firmwareVersion = firmwareVersion;
        this.uploaded = uploaded;
    }

    private String vin;
    private String statusType;
    private String eldId;
    private double rpm;
    private double speed;
    private double odometer;
    private double tripDistance;
    private double engineHours;
    private double tripHours;
    private double voltage;
    private String gpsDateTime;
    private double latitude;
    private double longitude;
    private int gpsSpeed;
    private int course;
    private int numSats;
    private int mslAlt;
    private double dop;
    private int sequence;
    private String firmwareVersion;
    private String uploaded;

    @Override
    public String toString() {
        return "{" +
                "\"externalId\":\"" + id + "\"" +
                ", \"vin\":\"" + vin + "\"" +
                ", \"statusType\":\"" + statusType + "\"" +
                ", \"eldId\":\"" + eldId + "\"" +
                ", \"rpm\":\"" + rpm + "\"" +
                ", \"speed\":\"" + speed + "\"" +
                ", \"odometer\":\"" + odometer + "\"" +
                ", \"tripDistance\":\"" + tripDistance + "\"" +
                ", \"engineHours\":\"" + engineHours + "\"" +
                ", \"tripHours\":\"" + tripHours + "\"" +
                ", \"voltage\":\"" + voltage + "\"" +
                ", \"gpsDateTime\":\"" + gpsDateTime + "\"" +
                ", \"latitude\":\"" + latitude + "\"" +
                ", \"longitude\":\"" + longitude + "\"" +
                ", \"gpsSpeed\":\"" + gpsSpeed + "\"" +
                ", \"course\":\"" + course + "\"" +
                ", \"numSats\":\"" + numSats + "\"" +
                ", \"mslAlt\":\"" + mslAlt + "\"" +
                ", \"dop\":\"" + dop + "\"" +
                ", \"sequence\":\"" + sequence + "\"" +
                ", \"firmwareVersion\":\"" + firmwareVersion +  "\"" +
                ", \"uploaded\":\"" + uploaded +  "\"" +
                "}";
    }
}
