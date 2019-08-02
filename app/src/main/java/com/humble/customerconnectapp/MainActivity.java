package com.humble.customerconnectapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.humble.customerconnectapp.data.CustomerContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Intent help_intent;
    TextView your_customer;
    CursorAdapter adapter = new CustomerAdapter(this,null);
    ListView listView;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();
    }

    public class CustomerAdapter extends CursorAdapter {

        Class<MainActivity> mainActivityClass = MainActivity.class;
        public CustomerAdapter(Context context, Cursor c ) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_layout,viewGroup,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView name = (TextView) view.findViewById(R.id.name_list_item);
            TextView phone = (TextView) view.findViewById(R.id.phone_input_list);
            Button button = (Button) view.findViewById(R.id.send_button);
            TextView last_visited = (TextView) view.findViewById(R.id.last_visited_list);
            TextView visited_for = (TextView) view.findViewById(R.id.visited_for);
            TextView email = (TextView) view.findViewById(R.id.email_list);
            TextView amount_due = (TextView) view.findViewById(R.id.amount_due_list);

            String Name = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.CUSTOMER_NAME));
            String Phone = String.valueOf(cursor.getLong(cursor.getColumnIndex(CustomerContract.CustomerEntry.PHONE)));
            String Email = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.EMAIL));
            String lastvisited = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.LAST_VISITED));
            String visited = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.VISITED_FOR));
            int amountdue = cursor.getInt(cursor.getColumnIndex(CustomerContract.CustomerEntry.AMOUNT_DUE_FOR_PAYMENT));

            final int ID = cursor.getInt(cursor.getColumnIndex(CustomerContract.CustomerEntry._ID));

            name.setText(Name);
            phone.setText(Phone);
            email.setText(Email);
            last_visited.setText("Last Visited : "+lastvisited);
            visited_for.setText("Last Visited for : "+visited);
            amount_due.setText("Amount Due in Rupees: Rs."+String.valueOf(amountdue));





        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] preferences = {CustomerContract.CustomerEntry._ID, CustomerContract.CustomerEntry.CUSTOMER_NAME, CustomerContract.CustomerEntry.PHONE, CustomerContract.CustomerEntry.EMAIL, CustomerContract.CustomerEntry.LAST_VISITED, CustomerContract.CustomerEntry.VISITED_FOR, CustomerContract.CustomerEntry.AMOUNT_DUE_FOR_PAYMENT};
        return new CursorLoader(getBaseContext(), CustomerContract.CustomerEntry.CONTENT_URI,preferences,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        listView.setAdapter(adapter);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        help_intent = new Intent(this,HelpActivity.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editActivity = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(editActivity);
            }
        });
        listView = (ListView) findViewById(R.id.list_view);
        View view = findViewById(R.id.empty_view);
        listView.setEmptyView(view);
        getLoaderManager().initLoader(0,null,this);
        final Intent intent = new Intent(this,EditorActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri currentUri = ContentUris.withAppendedId(CustomerContract.CustomerEntry.CONTENT_URI,l);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_help :
                startActivity(help_intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
