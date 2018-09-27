package com.example.android.bookishinventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.bookishinventory.data.BookContract.BookEntry;
import com.example.android.bookishinventory.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity {

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQuantity;
    private EditText mProductSupplier;
    private EditText mSupplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductName = (EditText) findViewById(R.id.product_name);
        mProductPrice = (EditText) findViewById(R.id.product_price);
        mProductQuantity = (EditText) findViewById(R.id.product_quantity);
        mProductSupplier = (EditText) findViewById(R.id.product_supplier);
        mSupplierPhone = (EditText) findViewById(R.id.product_supplier_number);

    }

    public void insertProduct() {
        // create instance of the db helper
        BookDbHelper mDbHelper = new BookDbHelper(this);
        // get the data from the EditText objects
        String productName = mProductName.getText().toString().trim();
        int productPrice = Integer.parseInt(mProductPrice.getText().toString().trim());
        int productQuantity = Integer.parseInt(mProductQuantity.getText().toString().trim());
        String productSupplier = mProductSupplier.getText().toString().trim();
        String supplierPhone = mSupplierPhone.getText().toString().trim();
        // Create and fill the ContentValues Object with the data
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(BookEntry.COLUMN_PRICE, productPrice);
        values.put(BookEntry.COLUMN_QUANTITY, productQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, productSupplier);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
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
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertProduct();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
