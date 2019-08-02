package com.humble.customerconnectapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.humble.customerconnectapp.data.CustomerContract;

import java.util.Date;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText name_edit_field;
    private EditText phone_edit_field;
    private EditText email_edit_field;
    private EditText amount_due;
    private Boolean customer_has_changed = false;
    private Uri currentUri;
    private Button button;
    private Date date;
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Customer");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteCustomer();
            }
        });
        builder.setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteCustomer(){
        int delete = getContentResolver().delete(currentUri,null,null);
        if(delete != 0){
            Toast.makeText(EditorActivity.this,"The Customer has been Removed",Toast.LENGTH_LONG).show();
            finish();}
        else{
            Toast.makeText(EditorActivity.this,"Error removing the Customer",Toast.LENGTH_LONG).show();
            finish();
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        currentUri = intent.getData();
        if (currentUri == null) {
            setTitle("Add new Customer");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Customer Details");
            getLoaderManager().initLoader(1, null, this);
        }
        name_edit_field = (EditText) findViewById(R.id.editText_name);
        phone_edit_field = (EditText) findViewById(R.id.editText_name1);
        email_edit_field = (EditText) findViewById(R.id.editText_name3);
        amount_due = (EditText) findViewById(R.id.editText_name4);
        button = (Button) findViewById(R.id.send_button);
        if(currentUri == null){
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUri != null){
                 Intent message = new Intent(EditorActivity.this,MessageActivity.class);
                 message.setData(currentUri);
                 startActivity(message);}
            }
        });
        name_edit_field.setOnTouchListener(mTouchListener);
        phone_edit_field.setOnTouchListener(mTouchListener);
        email_edit_field.setOnTouchListener(mTouchListener);
        amount_due.setOnTouchListener(mTouchListener);
    }
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            customer_has_changed = true;
            return false;
        }
    };

    private void showEditingDialogBox(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing");
        builder.setPositiveButton("DISCARD",discardButtonClickListener);
        builder.setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed(){
        if (!customer_has_changed) {
            super.onBackPressed();
            finish();
            return;
        }
        else{
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };
            showEditingDialogBox(discardButtonClickListener);
        }

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.editor_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_save:
                if(currentUri == null){
                    insertCustomer();
                }
                if(currentUri != null){
                    updateCustomer();
                }

                finish();
                return true;


            case R.id.action_delete:
                if(currentUri!=null){
                    showDeleteConfirmationDialog();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if(!customer_has_changed){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;}
                else{
                    DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    };
                    showEditingDialogBox(dialogOnClickListener);
                    return true;}


        }
        return super.onOptionsItemSelected(item);
    }

    private void insertCustomer() {
        //Get the data from the Edit Text Fields
        long phone = 0;
        int amount1 = 0;
        String name = name_edit_field.getText().toString().trim();
        String Email = email_edit_field.getText().toString().trim();
        if(!TextUtils.isEmpty(phone_edit_field.getText())){
          phone = Long.parseLong(phone_edit_field.getText().toString());}
        if(!TextUtils.isEmpty(amount_due.getText())){
        amount1 = Integer.parseInt(amount_due.getText().toString());}
        if (TextUtils.isEmpty(name) ||  phone == 0) {
            finish();
        } else {
            ContentValues values = new ContentValues();
            values.put(CustomerContract.CustomerEntry.CUSTOMER_NAME, name);
            values.put(CustomerContract.CustomerEntry.PHONE, phone);
            values.put(CustomerContract.CustomerEntry.AMOUNT_DUE_FOR_PAYMENT, amount1);
            values.put(CustomerContract.CustomerEntry.EMAIL, Email);
            values.put(CustomerContract.CustomerEntry.LAST_VISITED, "New Customer");
            values.put(CustomerContract.CustomerEntry.VISITED_FOR,"New Customer");

            Uri row_uri = getContentResolver().insert(CustomerContract.CustomerEntry.CONTENT_URI, values);

            if (row_uri != null) {
                Toast.makeText(this, "Customer Added Successfully", Toast.LENGTH_LONG).show();
            }
            if (row_uri == null) {
                Toast.makeText(this, "Customer couldn't be Added", Toast.LENGTH_LONG).show();
            }

        }
    }
    private void updateCustomer() {
        long phone = 0;
        int amount2 = 0;
        String new_name = name_edit_field.getText().toString().trim();
        String new_Email = email_edit_field.getText().toString().trim();
        if(!TextUtils.isEmpty(phone_edit_field.getText())){
        phone = Long.parseLong(phone_edit_field.getText().toString());}
        if(!TextUtils.isEmpty(amount_due.getText())){
         amount2 = Integer.valueOf(amount_due.getText().toString());}
        if (TextUtils.isEmpty(new_name) || phone == 0) {
            Toast.makeText(getBaseContext(), "Can't keep Name or Phone empty", Toast.LENGTH_LONG).show();
            finish();
        } else {
            ContentValues values = new ContentValues();
            values.put(CustomerContract.CustomerEntry.CUSTOMER_NAME, new_name);
            values.put(CustomerContract.CustomerEntry.EMAIL, new_Email);
            values.put(CustomerContract.CustomerEntry.PHONE, phone);
            values.put(CustomerContract.CustomerEntry.AMOUNT_DUE_FOR_PAYMENT, amount2);
            int update = getContentResolver().update(currentUri, values, null, null);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] preferences = {CustomerContract.CustomerEntry._ID, CustomerContract.CustomerEntry.CUSTOMER_NAME, CustomerContract.CustomerEntry.PHONE, CustomerContract.CustomerEntry.EMAIL, CustomerContract.CustomerEntry.LAST_VISITED, CustomerContract.CustomerEntry.VISITED_FOR, CustomerContract.CustomerEntry.AMOUNT_DUE_FOR_PAYMENT};
        return new CursorLoader(getBaseContext(),currentUri,preferences,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.CUSTOMER_NAME));
            long phone = cursor.getLong(cursor.getColumnIndex(CustomerContract.CustomerEntry.PHONE));
            String Email = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.EMAIL));
            int amount = cursor.getInt(cursor.getColumnIndex(CustomerContract.CustomerEntry.AMOUNT_DUE_FOR_PAYMENT));
            name_edit_field.setText(name);
            phone_edit_field.setText(String.valueOf(phone));
            email_edit_field.setText(Email);
            amount_due.setText(String.valueOf(amount));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name_edit_field.setText(null);
        phone_edit_field.setText(null);
        email_edit_field.setText(null);
        amount_due.setText(null);

    }




}
