package com.example.android.bookishinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookishinventory.data.BookContract.BookEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView mListView;
    BookCursorAdapter mAdapter;
    final static int LOADER_ID = 1;
    final static String[] PROJECTION = new String[] {
            BookEntry._ID,
            BookEntry.COLUMN_PRODUCT_NAME,
            BookEntry.COLUMN_PRICE,
            BookEntry.COLUMN_QUANTITY,
            BookEntry.COLUMN_SUPPLIER_NAME,
            BookEntry.COLUMN_SUPPLIER_PHONE
    };
    Button saleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_title));

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);

        mListView = findViewById(R.id.list_view);
        View empty = findViewById(R.id.empty);
        mListView.setEmptyView(empty);
        mAdapter = new BookCursorAdapter(this, null);
        mListView.setAdapter(mAdapter);

        saleButton = findViewById(R.id.sale);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                final Uri thisBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                intent.setData(thisBook);
                startActivity(intent);
            }
        });




    }



    private void insertProduct() {
        // create Dummy data into a ContentValues object
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, getString(R.string.sample_title));
        values.put(BookEntry.COLUMN_PRICE, 15);
        values.put(BookEntry.COLUMN_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, getString(R.string.sample_supplier));
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.sample_phoone));
        // insert values into the table
        Uri uri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(this, R.string.dummy_added, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.dummy_not_added, Toast.LENGTH_SHORT).show();
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, BookEntry.CONTENT_URI,
                PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
