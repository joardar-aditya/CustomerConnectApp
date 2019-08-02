package com.humble.customerconnectapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class CustomerProvider extends ContentProvider {

    CustomerdbHelper cust_db ;

    public static final String LOG_TAG = CustomerProvider.class.getSimpleName();

    private static final int  CUSTOMER = 27;
    private static final int CUTOMER_ID = 28;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(CustomerContract.CONTENT_AUTHORITY, CustomerContract.PATH, CUSTOMER);

        matcher.addURI(CustomerContract.CONTENT_AUTHORITY, "customer/#", CUTOMER_ID);
    }


    @Override
    public boolean onCreate() {
        cust_db = new CustomerdbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase database = cust_db.getReadableDatabase();
        Cursor cursor = null;
        int match = matcher.match(uri);
        switch (match) {
            case CUSTOMER :
                //query the entire database
                cursor = database.query(CustomerContract.CustomerEntry.TABLE_NAME,strings,s,strings1,null,null,null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CUTOMER_ID:
                s = CustomerContract.CustomerEntry._ID + "=?";
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(CustomerContract.CustomerEntry.TABLE_NAME,strings,s,strings1,null,null,null);
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = matcher.match(uri);
        switch (match){
            case CUSTOMER :
                return CustomerContract.CustomerEntry.CONTENT_TYPE;
            case CUTOMER_ID :
                return CustomerContract.CustomerEntry.CONTENT_ITEM_TYPE;
            default :
                throw  new IllegalArgumentException("Error returning the type of Uri");
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = matcher.match(uri);
        switch (match) {
            case CUSTOMER :
               return insertCustomer(uri,contentValues);
            default:
                throw new IllegalArgumentException("Problem inserting into database");

        }


    }

    private Uri insertCustomer(Uri uri,ContentValues contentValues){
        SQLiteDatabase db = cust_db.getWritableDatabase();
        String name = contentValues.getAsString(CustomerContract.CustomerEntry.CUSTOMER_NAME);
        long phone = contentValues.getAsLong(CustomerContract.CustomerEntry.PHONE);
        if(name == null){
            throw new IllegalArgumentException("Please enter the customer Name");
        }
        if(String.valueOf(phone) == null){
            throw new IllegalArgumentException("Please enter a phone number");
        }

        else {
          long id = db.insert(CustomerContract.CustomerEntry.TABLE_NAME,null,contentValues);
          getContext().getContentResolver().notifyChange(uri,null);
            return ContentUris.withAppendedId(uri,id);}

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db =  cust_db.getWritableDatabase();
        final int match = matcher.match(uri);
        switch (match) {
            case CUSTOMER :
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(CustomerContract.CustomerEntry.TABLE_NAME,s,strings);
            case CUTOMER_ID :
                s = CustomerContract.CustomerEntry._ID +"=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(CustomerContract.CustomerEntry.TABLE_NAME,s,strings);
             default:
                 throw new IllegalArgumentException("Error in deleting from the databse");
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = cust_db.getWritableDatabase();
        final int match = matcher.match(uri);
        switch (match) {
            case CUSTOMER :
                return updatePet(uri,contentValues,s,strings);
            case CUTOMER_ID :
                s = CustomerContract.CustomerEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,s,strings);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updatePet(Uri uri,ContentValues values,String s,String[] strings){
        if(values.size() == 0){
            return 0;}
        else {
            if (values.containsKey(CustomerContract.CustomerEntry.CUSTOMER_NAME)) {
                String name =values.getAsString(CustomerContract.CustomerEntry.CUSTOMER_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Please enter a name");

                }
            }
            if(values.containsKey(CustomerContract.CustomerEntry.PHONE)){
                long phone = values.getAsInteger(CustomerContract.CustomerEntry.PHONE);
                if(String.valueOf(phone)==null||phone<0){
                    throw new IllegalArgumentException("The Phone number cannot be negative");
                }
            }
            if(values.containsKey(CustomerContract.CustomerEntry.EMAIL)){
                String EMAIL = values.getAsString(CustomerContract.CustomerEntry.EMAIL);
                if(!EMAIL.contains("@")){
                    throw new IllegalArgumentException("Please enter a valid E-mail Id");
                }

            }

            SQLiteDatabase db = cust_db.getWritableDatabase();
            int num_rows = db.update(CustomerContract.CustomerEntry.TABLE_NAME,values,s,strings);
            getContext().getContentResolver().notifyChange(uri,null);
            return num_rows;
        }
    }
}
