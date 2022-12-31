package com.example.shlok_mehta_project2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PaymentActivity extends AppCompatActivity {
    TextView subtotal, taxView, totalPay;
    Long totalWithOutTax;
    EditText cardNum, exDate, cvv;
    Button payBtn;
    ImageView checkOutBag;
    ImageView logoutBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        subtotal = findViewById(R.id.subtotal);
        taxView = findViewById(R.id.tax);
        totalPay = findViewById(R.id.totalPay);
        cardNum = findViewById(R.id.cardNum);
        exDate = findViewById(R.id.exDate);
        cvv = findViewById(R.id.cvv);
        payBtn = findViewById(R.id.payBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        checkOutBag = findViewById(R.id.checkOutBag);


        Intent intent = getIntent();

        totalWithOutTax = (Long) intent.getExtras().get("totalWithOutTax");

        Long tax = (totalWithOutTax * 13) / 100;

        Long totalWithTax = totalWithOutTax + tax;

        taxView.setText("$" + String.valueOf(tax));

        totalPay.setText("$" + String.valueOf(totalWithTax));

        subtotal.setText("$" + String.valueOf(intent.getExtras().get("totalWithOutTax")));

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardNum.getText().toString().trim().isEmpty()){
                    cardNum.setError("Card Number is required");
                    return;
                }

                if (exDate.getText().toString().trim().isEmpty()){
                    exDate.setError("Expiry Date is required");
                    return;
                }

                if (cvv.getText().toString().trim().isEmpty()){
                    cvv.setError("Province is required");
                    return;
                }

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                CollectionReference itemsRef = rootRef.collection("cart");
                Query query = itemsRef.whereEqualTo("userId", userId);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                itemsRef.document(document.getId()).delete();
                                // Create the object of AlertDialog Builder class
                                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);

                                // Set the message show for the Alert time
                                builder.setMessage("View More Products");

                                // Set Alert Title
                                builder.setTitle("Order Placed!");

                                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                builder.setCancelable(false);

                                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                                builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                                    // When the user click yes button then app will close
                                    startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                                });

                                // Create the Alert dialog
                                AlertDialog alertDialog = builder.create();
                                // Show the Alert Dialog box
                                alertDialog.show();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        checkOutBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CheckOut.class));
            }
        });
    }
}