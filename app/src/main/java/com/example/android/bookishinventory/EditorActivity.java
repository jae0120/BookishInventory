package com.example.android.bookishinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.bookishinventory.data.BookContract;
import com.example.android.bookishinventory.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // set up views
    private EditText mProductName;
    private EditText mProductPrice;
    private TextView mProductQuantity;
    private int mQuantity;
    private Button mDecrement;
    private Button mIncrement;
    private EditText mProductSupplier;
    private EditText mSupplierPhone;
    private Button mOrder;


    public String phone;

    // individual uri variable, which is passed in through the intent when a list item is clicked.
    public Uri bookUri;

    private static final int LOADER_ID = 2;

    private boolean mBookHasChanged = false;
    //establish listener to tell if the user has touched any of the edit text items
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // attach views to variables
        mProductName = findViewById(R.id.product_name);
        mProductPrice = findViewById(R.id.product_price);
        mProductQuantity = findViewById(R.id.product_quantity);
        mQuantity = Integer.parseInt(mProductQuantity.getText().toString());
        mProductSupplier = findViewById(R.id.product_supplier);
        mSupplierPhone = findViewById(R.id.product_supplier_number);
        mDecrement = findViewById(R.id.decrement_quant);
        mIncrement = findViewById(R.id.increment_quant);
        mOrder = findViewById(R.id.order_now);
        // get the uri from the MainActivity intent, if one is available
        Intent intent = getIntent();
        bookUri = intent.getData();

        // if there is a uri, we are in edit mode
        if (bookUri != null) {
            setTitle(getString(R.string.edit_title));
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
        // otherwise, we are in insert mode
        else {
            setTitle(getString(R.string.insert_title));
            // hide the delete option for insert mode
            invalidateOptionsMenu();
            // hide the order button while inserting
            mOrder.setVisibility(View.GONE);
        }

        // setup buttons
        mDecrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mQuantity == 0) {
                    return;
                } else {
                    mQuantity -= 1;
                    mProductQuantity.setText(Integer.toString(mQuantity));
                }
            }

        });
        mIncrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mQuantity += 1;
                mProductQuantity.setText(Integer.toString(mQuantity));
            }
        });

        // set touch listeners
        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mIncrement.setOnTouchListener(mTouchListener);
        mProductSupplier.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);


    }

    // method for saving a product.
    public void saveProduct() {

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



        // if uri is not null, we are in edit mode and need to call an update on the contentProvider
        // if there isn't a uri, we go to the else which inserts the data
        if (bookUri != null) {

            int rowsUpdated = getContentResolver().update(bookUri, values, null, null);
            if (rowsUpdated > 0) {
                Toast.makeText(this, R.string.update_toast, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_fail_toast, Toast.LENGTH_SHORT).show();
            }
        } else {
            // insert values into the table
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri != null) {
                Toast.makeText(this, R.string.insert_toast, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.insert_failed_toast, Toast.LENGTH_SHORT).show();
            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book(insert mode), hide the "Delete" menu item.
        if (bookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_entry);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                // if any fields are incomplete, ask user to complete them
                if (TextUtils.isEmpty(mProductName.getText()) || TextUtils.isEmpty(mProductPrice.getText())
                        || TextUtils.isEmpty(mProductSupplier.getText()) || TextUtils.isEmpty(mSupplierPhone.getText())) {
                    Toast.makeText(this, R.string.complete_all_fields, Toast.LENGTH_SHORT).show();
                    return true;
                }
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_delete_entry:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteEntry();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteEntry() {
        if (bookUri != null) {

            int rowsDeleted = getContentResolver().delete(bookUri, null, null);
            if (rowsDeleted > 0) {
                Toast.makeText(this, R.string.book_deleted_toast, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, bookUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //get columns
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
        final int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);

        // get values from the cursor
        if (cursor.moveToNext()) {
            phone = cursor.getString(supplierPhoneColumnIndex);
            mProductName.setText(cursor.getString(nameColumnIndex));
            mProductPrice.setText(Integer.toString(cursor.getInt(priceColumnIndex)));
            mQuantity = cursor.getInt(quantityColumnIndex);
            mProductQuantity.setText(Integer.toString(mQuantity));
            mProductSupplier.setText(cursor.getString(supplierColumnIndex));
            mSupplierPhone.setText(cursor.getString(supplierPhoneColumnIndex));

            // set the edit texts so the user cursor is at the end
            mProductName.setSelection(mProductName.getText().length());
            mProductPrice.setSelection(mProductPrice.getText().length());
            mProductSupplier.setSelection(mProductSupplier.getText().length());
            mSupplierPhone.setSelection(mSupplierPhone.getText().length());
        }
        // setup Order button
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = "+" + phone;
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(phoneIntent);


            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // reset edit text views to blank
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mProductSupplier.setText("");
        mSupplierPhone.setText("");
    }
}
