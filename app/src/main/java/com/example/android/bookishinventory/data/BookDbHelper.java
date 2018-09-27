package com.example.android.bookishinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.bookishinventory.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "bookstore.db";
    public static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookEntry.TABLE_NAME
                + " (" + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 1, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.i("BookDbHelper", SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
