package com.example.shlok_mehta_project2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckOut extends AppCompatActivity {

    String userId;
    public long total;
    TextView name, email;
    ScrollView cartView;
    LinearLayout emptyCart;
    EditText sAddress, aptNum, country, zip, province, city, pNumber;
    Button payBtn, viewProds;
    ImageView logoutBtn;

    // Init Auth
    FirebaseAuth firebaseAuth;

    // Firebase:
    FirebaseFirestore fireStore;

    RecyclerView recyclerView;

    ArrayList<ProductData> recyclerDataCartArrayList;

    private RecyclerViewAdapterCart recyclerCartAdapter;

    public TextView totalTxt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        recyclerView = findViewById(R.id.cartRV);
        totalTxt = findViewById(R.id.totalTxt);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        cartView = findViewById(R.id.cartView);
        emptyCart = findViewById(R.id.emptyCart);
        sAddress = findViewById(R.id.sAddress);
        aptNum = findViewById(R.id.aptNum);
        country = findViewById(R.id.country);
        zip = findViewById(R.id.zip);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        pNumber = findViewById(R.id.pNumber);
        payBtn = findViewById(R.id.payBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        // created new array list..
        recyclerDataCartArrayList = new ArrayList<>();

        // Firebase ref
        fireStore = FirebaseFirestore.getInstance();

        // Get Instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Clear ArrayList
        ClearAll();

        //Get Data Method
        GetDataFromFirebase();

        // Get User Data
        getUserData();

        if (payBtn != null){
            payBtn = findViewById(R.id.payBtn);
            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sAddress.getText().toString().trim().isEmpty()){
                        sAddress.setError("Address is required");
                        return;
                    }

                    if (country.getText().toString().trim().isEmpty()){
                        country.setError("Country is required");
                        return;
                    }

                    if (zip.getText().toString().trim().isEmpty()){
                        zip.setError("Zip is required");
                        return;
                    }

                    if (province.getText().toString().trim().isEmpty()){
                        province.setError("Province is required");
                        return;
                    }

                    if (city.getText().toString().trim().isEmpty()){
                        city.setError("City is required");
                        return;
                    }

                    if (pNumber.getText().toString().trim().isEmpty()){
                        pNumber.setError("Phone Number is required");
                        return;
                    }

                    Intent myIntent = new Intent(CheckOut.this, PaymentActivity.class);
                    myIntent.putExtra("totalWithOutTax", total);
                    startActivity(myIntent);
                }
            });
        }

        Log.i(TAG, "onCreate: GET TOTAL  " + total);
    }

   private void GetDataFromFirebase(){
        userId = firebaseAuth.getCurrentUser().getUid();

        fireStore.collection("cart")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()){
                                emptyCart.setVisibility(View.VISIBLE);
                                cartView.setVisibility(View.GONE);
                                viewProds = findViewById(R.id.viewProds);
                                payBtn.setVisibility(View.GONE);

                                viewProds.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                                    }
                                });
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    total += (long) document.getData().get("price");
                                    ProductData productData = new ProductData();
                                    String price = String.valueOf(document.getData().get("price"));
                                    productData.setID(document.getId());
                                    productData.setImage((String) document.getData().get("image"));
                                    productData.setTitle((String) document.getData().get("title"));
                                    productData.setPrice(price);
                                    productData.setDescription((String) document.getData().get("description"));
                                    productData.setQuantity(Integer.parseInt(String.valueOf(document.getData().get("quantity"))));
                                    recyclerDataCartArrayList.add(productData);
                                    Log.d(TAG, "CART:"+ " =>  " + document);
                            }

                            recyclerCartAdapter = new RecyclerViewAdapterCart(recyclerDataCartArrayList, getApplicationContext());
                            recyclerView.setAdapter(recyclerCartAdapter);
                            recyclerCartAdapter.notifyDataSetChanged();
                            totalTxt.setText("$" + String.valueOf(total));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getUserData(){
        userId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference docRef = fireStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name.setText("Name: " + (String) document.getData().get("firstName") + " " + document.getData().get("LastName"));
                        email.setText("Email: " + (CharSequence) document.getData().get("email"));
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void ClearAll(){
        if (recyclerDataCartArrayList != null){
            recyclerDataCartArrayList.clear();

            if (recyclerCartAdapter != null){
                recyclerCartAdapter.notifyDataSetChanged();
            }
        }

        recyclerDataCartArrayList = new ArrayList<>();
    }
}