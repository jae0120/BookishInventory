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
import android.widget.Toast;

import com.example.android.bookishinventory.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor cursor;
    Button saleButton;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.mContext = context;
        cursor = c;
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }




    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        // set up sale Button
        saleButton = view.findViewById(R.id.sale);

        // Define an onclicklistener
        class OnItemClickListener implements View.OnClickListener {
            private int position;

            public OnItemClickListener(int position) {
                super();
                this.position = position;
            }
            @Override
            public void onClick(View view) {

                int theWantedPosition = 0;
                // Loop through cursor to get the correct position that corresponds with our ID
                if (cursor.moveToFirst()) {
                    while (cursor.moveToNext()) {
                        if (cursor.getInt(cursor.getColumnIndex(BookEntry._ID)) == position) {
                            theWantedPosition = cursor.getPosition();
                            break;
                        }
                    }
                }
                // set cursor to the correct line
                cursor.moveToPosition(theWantedPosition);
                // change the quantity if there are more than zero books
                int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
                if (quantity > 0) {
                    quantity -= 1;
                    // create content values to use for updating the database
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, quantity);
                    // make sure to update the correct book entry
                    Uri thisBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, position);
                    int rowUpdated = mContext.getContentResolver().update(thisBook, values, null, null);
                    if (rowUpdated > 0) {
                     Toast.makeText(view.getContext(), R.string.sold, Toast.LENGTH_SHORT).show();
                     } else {
                     Toast.makeText(view.getContext(), R.string.sale_error, Toast.LENGTH_LONG).show();
                     }

                }
                // inform the user they've already sold out of the book and can't sell anymore
                else {
                    Toast.makeText(view.getContext(), R.string.sold_out, Toast.LENGTH_SHORT).show();
                }
            }
        };

        // get the id to pass into the clicklistener
        int id = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        // create instance of the listener and set it on the sale button
        OnItemClickListener mClickListener = new OnItemClickListener(id);
        saleButton.setOnClickListener(mClickListener);


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
