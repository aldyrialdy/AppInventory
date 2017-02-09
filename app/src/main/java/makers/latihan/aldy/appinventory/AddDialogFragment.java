package makers.latihan.aldy.appinventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddDialogFragment extends DialogFragment {

    public static final int REQUEST_CODE = 0;
    EditText editProducts,editQty,editPrice;
    Button btnImage;
    String mImageURI;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View addView = inflater.inflate(R.layout.activity_add_dialog_fragment, null);

        editProducts = (EditText) addView.findViewById(R.id.name);
        editQty = (EditText) addView.findViewById(R.id.quantity);
        editPrice = (EditText) addView.findViewById(R.id.price);
        btnImage = (Button) addView.findViewById(R.id.btn_image);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),REQUEST_CODE);
            }
        });

        final Dialog d = builder.setView(addView).setPositiveButton("Add Product", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddDialogFragment.this.getDialog().cancel();
                    }
                }).create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button addProduct = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                addProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Boolean wantToCloseDialog = false;
                        String name = editProducts.getText().toString().trim();
                        String qty = editQty.getText().toString().trim();
                        String price = editPrice.getText().toString().trim();

                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(qty)) {
                            Toast.makeText(getActivity(), getString(R.string.product_info_not_empty), Toast.LENGTH_SHORT).show();
                        } else {
                            int quantity = Integer.parseInt(qty);
                            Float priceProd = Float.parseFloat(price);
                            insertProduct(name, quantity, priceProd);
                            wantToCloseDialog = true;
                        }

                        // after successfully inserting product, dismiss the dialog
                        if (wantToCloseDialog)
                            d.dismiss();
                    }
                });
            }
        });

        return super.onCreateDialog(savedInstanceState);
    }

    private void insertProduct(String name, int quantity, Float priceProd) {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCTS_NAME, name);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, priceProd);
        if (!"".equals(imagePath))
            values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imagePath);
        getActivity().getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }
}
