package com.example.shlok_mehta_project2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button login, register;
    FirebaseAuth firebaseAuth;
    // Firebase:
    FirebaseFirestore fireStore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase ref
        fireStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailCheck = email.getText().toString().trim();
                String passwordCheck = password.getText().toString().trim();

                if (TextUtils.isEmpty(emailCheck)){
                    email.setError("Email is Required");
                    return;
                }

                if (TextUtils.isEmpty(passwordCheck)){
                    password.setError("Passowrd is required");
                    return;
                }

                if (password.length() < 3){
                    password.setError("Passowrd should be 3 chars or more");
                    return;
                }

                // Auth
                firebaseAuth.signInWithEmailAndPassword(emailCheck, passwordCheck).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "User Logged In",Toast.LENGTH_LONG).show();
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            String firebaseUser = firebaseAuth.getCurrentUser().getUid();

                            DocumentReference docRef = fireStore.collection("users").document(firebaseUser);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        String checkAdmin = "admin";
                                        if (document.exists()) {
                                            if (checkAdmin.equals(document.getData().get("type"))){
                                                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("type"));
                                            } else {
                                                startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                                            }
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

//                                Log.i(TAG, "onComplete: USER NORMAL " + firebaseUser);
//                                startActivity(new Intent(getApplicationContext(), ProductActivity.class));

                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(Login.this, "User auth faliled",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}