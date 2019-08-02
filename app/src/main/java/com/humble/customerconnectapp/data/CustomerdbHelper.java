package com.humble.customerconnectapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomerdbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "customer.db";
    private static final int DATABASE_VERSION = 1;
    public CustomerdbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    String SQL_CREATE_TABLE = "CREATE TABLE "+CustomerContract.CustomerEntry.TABLE_NAME+" ("
            + CustomerContract.CustomerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CustomerContract.CustomerEntry.CUSTOMER_NAME + " TEXT NOT NULL,"
            + CustomerContract.CustomerEntry.PHONE + " INTEGER NOT NULL,"
            + CustomerContract.CustomerEntry.EMAIL + " TEXT,"
            + CustomerContract.CustomerEntry.LAST_VISITED + " TEXT,"
            + CustomerContract.CustomerEntry.VISITED_FOR + " TEXT,"
            + CustomerContract.CustomerEntry.AMOUNT_DUE_FOR_PAYMENT + " INTEGER);";
    String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + CustomerContract.CustomerEntry.TABLE_NAME;




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
