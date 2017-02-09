package makers.latihan.aldy.appinventory;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aldyrialdy on 06/02/17.
 */
public class ProductContract {

    public static final String CONTENT_AUTHORITY = "makers.latihan.aldy.appinventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    private ProductContract() {

    }

    public static final class ProductEntry implements BaseColumns{

        /** The content URI to access the products data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single products.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        /** Name of database table for products */
        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCTS_NAME ="name";

        public final static String COLUMN_QUANTITY = "qty";

        public final static String COLUMN_PRICE = "price";

        public final static String COLUMN_IMAGE = "image";

    }
}
