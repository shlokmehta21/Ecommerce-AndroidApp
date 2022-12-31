package com.example.shlok_mehta_project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DEBUGDETAILACTIVITY";

    private int quantity = 1;
    public int price;
    TextView quantityTextView;
    TextView productPrice;
    Button increment;
    Button decrement;
    Button buyNow, loginBtn;
    ImageView checkOutBag;
    String userID;
    ImageView logoutBtn;
    // Auth to check if user is logged in or not
    FirebaseAuth firebaseAuth;

    // Firebase:
    FirebaseFirestore fireStore;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        quantityTextView = findViewById(R.id.quantity_text_view);
        increment = findViewById(R.id.increment_button);
        decrement = findViewById(R.id.decrement_button);
        buyNow = findViewById(R.id.buyBtn);
        loginBtn = findViewById(R.id.loginBtn);
        checkOutBag = findViewById(R.id.checkOut);
        logoutBtn = findViewById(R.id.logoutBtn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            logoutBtn.setImageResource(R.drawable.ic_baseline_login_24);
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
            });

            checkOutBag.setVisibility(View.GONE);
        } else {
            logoutBtn.setImageResource(R.drawable.ic_baseline_logout_24);
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            });
        }

        getIncomingIntent();

        // Firebase ref
        fireStore = FirebaseFirestore.getInstance();

        // Auth ref
        firebaseAuth = FirebaseAuth.getInstance();

        checkOutBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CheckOut.class));
            }
        });

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = quantity + 1;
                displayQuantity(quantity);
                Log.i(TAG, "onClick: INCREMENT AND DISPLAY AFTER " + quantity);
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity == 1)
                    return;
                quantity--;
                displayQuantity(quantity);
            }
        });


        if (firebaseAuth.getCurrentUser() == null){
            loginBtn.setVisibility(View.VISIBLE);
            buyNow.setVisibility(View.GONE);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userID = firebaseAuth.getCurrentUser().getUid();
                int newPrice = Integer.parseInt(getIntent().getStringExtra("PRODUCT_PRICE")) * quantity;
                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("userId", userID);
                cartItem.put("image", getIntent().getStringExtra("PRODUCT_IMG"));
                cartItem.put("title", getIntent().getStringExtra("PRODUCT_TITLE"));
                cartItem.put("price", newPrice);
                cartItem.put("description", getIntent().getStringExtra("PRODUCT_DESC"));
                cartItem.put("quantity", quantity);

                fireStore.collection("cart").add(cartItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Create the object of AlertDialog Builder class
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);

                        // Set the message show for the Alert time
                        builder.setMessage("Do want to checkout or buy more?");

                        // Set Alert Title
                        builder.setTitle("Item Added To The Cart!");

                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                        builder.setCancelable(false);

                        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                        builder.setPositiveButton("Check Out", (DialogInterface.OnClickListener) (dialog, which) -> {
                            // When the user click yes button then app will close
                            startActivity(new Intent(getApplicationContext(), CheckOut.class));
                        });

                        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                        builder.setNegativeButton("Buy More", (DialogInterface.OnClickListener) (dialog, which) -> {
                            // If user click no then dialog box is canceled.
                            startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                        });

                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();
                        // Show the Alert Dialog box
                        alertDialog.show();

                        Log.i(TAG, "onSuccess: " + "DATA ADDED INTO CART");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: " + "DID NOT ADDED INTO CART");
                    }
                });
            }
        });
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("PRODUCT_IMG") && getIntent().hasExtra("PRODUCT_TITLE") && getIntent().hasExtra("PRODUCT_PRICE")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");
            String image = getIntent().getStringExtra("PRODUCT_IMG");
            String product_title = getIntent().getStringExtra("PRODUCT_TITLE");
            String product_price = getIntent().getStringExtra("PRODUCT_PRICE");
            String product_description = getIntent().getStringExtra("PRODUCT_DESC");
            setDetail(product_title, image, product_price, product_description);
        }
    }

    private void setDetail(String product_title, String image, String price , String description){
        TextView name = findViewById(R.id.productTitle);
        name.setText(product_title);

        ImageView img = findViewById(R.id.productImg);
        Glide.with(this).load(image).into(img);
        TextView productPrice = findViewById(R.id.productPrice);
        int doublePrice = Integer.parseInt(price);
        productPrice.setText("$" + String.valueOf(doublePrice));

        TextView desc = findViewById(R.id.description);

        desc.setText(description);
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int number) {
        quantityTextView.setText(String.valueOf(number));
    }

//    private void incrementPrice(int price){
//        Log.i(TAG, "incrementPrice: " + productPrice.getText());
//
//        int newPrice = price + price;
//        productPrice.setText(String.valueOf(newPrice));
//    }

}