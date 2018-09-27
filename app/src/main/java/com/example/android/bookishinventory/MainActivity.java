package com.example.android.bookishinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookishinventory.data.BookContract.BookEntry;
import com.example.android.bookishinventory.data.BookDbHelper;


public class MainActivity extends AppCompatActivity {

    BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new BookDbHelper(this);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // This makes sure the info is displayed properly after coming back to the app from another activity
        displayDatabaseInfo();
    }

    public void displayDatabaseInfo() {
        // get a readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // set the projection array
        String[] Projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };

        // create the cursor
        Cursor cursor = db.query(BookEntry.TABLE_NAME, Projection, null, null, null, null, null);
        TextView displayView = (TextView) findViewById(R.id.books);
        try {
            displayView.setText("The Products Table contains this many rows: " + cursor.getCount() + "\n\n");
            displayView.append(BookEntry._ID + " - " + BookEntry.COLUMN_PRODUCT_NAME + " - "
                    + BookEntry.COLUMN_PRICE + " - " + BookEntry.COLUMN_QUANTITY + " - "
                    + BookEntry.COLUMN_SUPPLIER_NAME + " - " + BookEntry.COLUMN_SUPPLIER_PHONE + "\n");

            // find the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // While loop to display all rows in the database that the cursor queried for
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                // display the values before exiting the while loop
                displayView.append("\n" + currentId + " - " + currentName
                        + " - " + currentPrice + " - " + currentQuantity
                        + " - " + currentSupplier + " - " + currentSupplierPhone);
            }
        } finally {
            cursor.close();
        }
    }

    private void insertProduct() {
        // create Dummy data into a ContentValues object
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Howl\'s Moving Castle");
        values.put(BookEntry.COLUMN_PRICE, 15);
        values.put(BookEntry.COLUMN_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Billy Bob Joel");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "1-800-867-5309");
        // get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // insert values into the table
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
        if (newRowId > 0) {
            Toast.makeText(this, "New Book number " + newRowId + " added.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No new rows added.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(BookEntry.TABLE_NAME, null, null);
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
