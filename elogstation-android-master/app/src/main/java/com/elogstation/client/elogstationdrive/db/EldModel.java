package com.elogstation.client.elogstationdrive.db;

public class EldModel {
    public static final String TABLE_NAME = "elds";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ELDID = "eldId";
    public static final String COLUMN_ELDNAME = "name";

    private int id;
    private String eldId;
    private String name;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " LONG PRIMARY KEY,"
                    + COLUMN_ELDID + " TEXT,"
                    + COLUMN_ELDNAME + " TEXT"
                    + ")";

    public EldModel(){

    }

    public EldModel(int id, String eldId, String name) {
        this.id = id;
        this.eldId = eldId;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}