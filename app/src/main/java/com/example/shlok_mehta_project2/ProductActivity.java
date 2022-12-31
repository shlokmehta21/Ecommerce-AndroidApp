package com.example.shlok_mehta_project2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {
    ImageView logoutBtn;
    RecyclerView recyclerView;
    ImageView checkOutBag;

    // Firebase:
    FirebaseFirestore fireStore;

    ArrayList<ProductData> recyclerDataArrayList;

    private RecyclerViewAdapter recyclerAdapter;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        recyclerView = findViewById(R.id.productsRV);
        logoutBtn = findViewById(R.id.logoutBtn);
        checkOutBag = findViewById(R.id.checkOutBag);

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

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        // Firebase ref
        fireStore = FirebaseFirestore.getInstance();

        // created new array list..
        recyclerDataArrayList = new ArrayList<>();

        // Clear ArrayList
        ClearAll();

        //Get Data Method
        GetDataFromFirebase();

        checkOutBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CheckOut.class));
            }
        });
    }

    public void GetDataFromFirebase(){
        fireStore.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ProductData productData = new ProductData();
                            productData.setID(document.getId());
                            productData.setImage((String) document.getData().get("image"));
                            productData.setTitle((String) document.getData().get("name"));
                            productData.setPrice((String) document.getData().get("price"));
                            productData.setDescription((String) document.getData().get("description"));
                            recyclerDataArrayList.add(productData);
                            Log.d(TAG,document.getId() + " => " + document.getId());
                        }

                        recyclerAdapter = new RecyclerViewAdapter(recyclerDataArrayList, getApplicationContext());
                        recyclerView.setAdapter(recyclerAdapter);
                        recyclerAdapter.notifyDataSetChanged();

                        // Remove Progress Bar once the data is loaded
                        ProgressBar progressBar;
                        progressBar = findViewById(R.id.progress_loader);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void ClearAll(){
        if (recyclerDataArrayList != null){
            recyclerDataArrayList.clear();

            if (recyclerAdapter != null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }

        recyclerDataArrayList = new ArrayList<>();
    }
}

