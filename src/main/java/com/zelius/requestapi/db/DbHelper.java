package com.zelius.requestapi.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by RequestTwitterAPI on 16/03/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1; // Remember to update version when changing schema
    private static final String DATABASE_NAME = "TwitterAPI.db";
    private static final String CREATE_TABLE = "CREATE TABLE %s (%s);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS %s;";

    private String m_CreateQuery;
    private String m_DropQuery;

    public DbHelper(Context context, String tableName, String tableFields) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.m_CreateQuery = String.format(CREATE_TABLE, tableName, tableFields);
        this.m_DropQuery = String.format(DROP_TABLE, tableName);

        Log.i("DB-CREATE-QUERY", m_CreateQuery);
        Log.i("DB-DROP-QUERY", m_DropQuery);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(m_CreateQuery);
            Log.i("DB", "onCreate() called.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(m_DropQuery);
            onCreate(db);
            Log.i("DB", "onUpgrade() called.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}