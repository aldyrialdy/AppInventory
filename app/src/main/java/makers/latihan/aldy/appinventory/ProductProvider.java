package makers.latihan.aldy.appinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by aldyrialdy on 06/02/17.
 */
public class ProductProvider extends ContentProvider{
    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();
    private ProductDbHelper mDbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCTS_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        Float price = values.getAsFloat(ProductContract.ProductEntry.COLUMN_PRICE);

        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                return database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
            case PRODUCTS_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCTS_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCTS_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRICE)) {
            Float price = values.getAsFloat(ProductContract.ProductEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        int rowsUpdated = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated == -1) {
            Log.e(LOG_TAG, "Failed to update row for " + uri);
            return 0;
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
