package com.example.android.bookishinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookishinventory.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup viewGroup) {
        final View newView = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        Button saleButton = newView.findViewById(R.id.sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
                if (quantity == 0) {
                    return;
                } else {
                    quantity -= 1;
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, quantity);
                    long id = ((Long) newView.getTag());
                    Uri thisBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                    int rowUpdated = context.getContentResolver().update(thisBook, values, null, null);
        /**            if (rowUpdated > 0) {
                        Toast.makeText(this, getString(R.string.sale_toast), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.sale_error), Toast.LENGTH_LONG).show();
                    }**/

                }
            }
        });
        return newView;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        //get views
        TextView nameView = view.findViewById(R.id.name_text_view);
        TextView priceView = view.findViewById(R.id.price_text_view);
        TextView quantityView = view.findViewById(R.id.quantity_text_view);
        TextView supplierView = view.findViewById(R.id.supplier_text_view);
        TextView supplierPhoneView = view.findViewById(R.id.supplier_phone);

        //get columns
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

        // get values from the cursor
        String name = cursor.getString(nameColumnIndex);
        String price = Integer.toString(cursor.getInt(priceColumnIndex));
        String quantity = Integer.toString(cursor.getInt(quantityColumnIndex));
        String supplier = cursor.getString(supplierColumnIndex);
        String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

        price = "$ " + price;

        // set values to TextViews
        nameView.setText(name);
        priceView.setText(price);
        quantityView.setText(quantity);
        supplierView.setText(supplier);
        supplierPhoneView.setText(supplierPhone);

    }
}
