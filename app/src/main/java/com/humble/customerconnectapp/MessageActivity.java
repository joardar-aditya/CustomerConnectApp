package com.humble.customerconnectapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.humble.customerconnectapp.data.CustomerContract;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri Urirecieved;
    private String Name;
    private Long Phone;
    private PackageManager packageManager;
    private String url;
    private String smsNumber;
    boolean buying_cartrdiges;
    boolean cartridge_refilling;
    boolean cartridge_repair;
    boolean printer_servicing;
    String service = "";
    String current_date = " ";
    String visited = "";

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox)view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.buying:
                if (checked){
                    buying_cartrdiges = true;
                }
                else{
                    buying_cartrdiges = false ;
                }
                break;
            case R.id.cartridge_refilling:
                if (checked){
                    cartridge_refilling = true;
                }
                else{
                    cartridge_refilling = false;
                }
                break;
            case R.id.cartridge_repairing:
                if(checked) {
                    cartridge_repair = true;
                }
                else {
                    cartridge_repair = false;
                }
                break;
            case R.id.printer_repairing:
                if(checked) {
                    printer_servicing = true ;
                }
                else{
                    printer_servicing = false;
                }
                break;
            default:
                service = service+ " *our services,* ";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Intent intent = getIntent();
        Urirecieved = intent.getData();
        packageManager = this.getPackageManager();
        getLoaderManager().initLoader(1,null,this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] preferences = {CustomerContract.CustomerEntry._ID, CustomerContract.CustomerEntry.CUSTOMER_NAME, CustomerContract.CustomerEntry.PHONE, CustomerContract.CustomerEntry.LAST_VISITED, CustomerContract.CustomerEntry.VISITED_FOR};
        return new CursorLoader(getBaseContext(),Urirecieved,preferences,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){
            Name = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.CUSTOMER_NAME));
            Phone = cursor.getLong(cursor.getColumnIndex(CustomerContract.CustomerEntry.PHONE));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Name = null;
        Phone = null;

    }

    public void whatsappSend(View view) {
        if (Urirecieved != null) {
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            if (Phone == null) {
                Toast.makeText(getBaseContext(), "Please enter the phone number!", Toast.LENGTH_SHORT).show();
            } else {
                if (buying_cartrdiges) {
                    service = service + " *for buying cartridge(s)*, ";
                    visited = visited + "for buying cartridge, ";
                }
                if (cartridge_refilling) {
                    service = service + " *for cartridge refilling*, ";
                    visited = visited + "for cartridge_refilling, ";
                }
                if (printer_servicing) {
                    service = service + " *for printer servicing*, ";
                    visited = visited + "printer_servicing, ";
                }
                if (cartridge_repair) {
                    service = service + " *for cartridge repairing*, ";
                    visited = visited + "cartridge_repair, ";
                }

                String message = "Hi Mr./Mrs. " + "*" + Name + "*" + ", Thank you for visiting Shiva Print Services " + service + "\n We are glad to serve you as our customer! Do visit us again!";
                smsNumber = PhoneNumberUtils.formatNumber("91" + String.valueOf(Phone), "IN");
                try {
                    url = "https://api.whatsapp.com/send?phone=" + smsNumber + "&text=" + URLEncoder.encode(message, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Error", "Not Working Properly", e);
                }
                whatsappIntent.setData(Uri.parse(url));
                whatsappIntent.setPackage("com.whatsapp.w4b");
                if (whatsappIntent.resolveActivity(packageManager) != null) {
                    this.startActivity(whatsappIntent);
                } else {
                    Toast.makeText(this, "Message can't be send", Toast.LENGTH_LONG);
                }
                service = "";
                current_date = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault()).format(new Date());
                ContentValues values = new ContentValues();
                values.put(CustomerContract.CustomerEntry.LAST_VISITED,current_date);
                values.put(CustomerContract.CustomerEntry.VISITED_FOR,visited);
                int update = getContentResolver().update(Urirecieved,values,null,null);
                if(update != 0){
                    Toast.makeText(getBaseContext(),"Data Updated",Toast.LENGTH_SHORT).show();
                    visited = "";
                    current_date = "";
                }

            }

        }
        else {
            Toast.makeText(getBaseContext(),"Customer not found",Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
