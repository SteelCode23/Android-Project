package com.elogstation.client.elogstationdrive.db;

public class DefaultEldModel {
    public static final String TABLE_NAME = "defaulteld";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ELDID = "eldId";

    private int id;
    private String eldId;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " LONG PRIMARY KEY,"
                    + COLUMN_ELDID + " TEXT"
                    + ")";

    public DefaultEldModel(){

    }

    public DefaultEldModel(int id, String eldId) {
        this.id = id;
        this.eldId = eldId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEldId() {
        return eldId;
    }

    public void setEldId(String eldId) {
        this.eldId = eldId;
    }
}
