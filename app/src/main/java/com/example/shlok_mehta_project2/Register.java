package com.example.shlok_mehta_project2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText firstName, lastName, email, password;
    Button register, login;
    String userID;

    // Init Auth
    FirebaseAuth firebaseAuth;

    // Firebase:
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.LastName);
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);

        register = findViewById(R.id.registerBtn);
        login = findViewById(R.id.login);

        // Firebase ref
        fireStore = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        // Get Instance
        firebaseAuth = FirebaseAuth.getInstance();

        // If user is logged in then redirect to main screen
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), ProductActivity.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstNameCheck = firstName.getText().toString().trim();
                String lastNameCheck = lastName.getText().toString().trim();
                String emailCheck = email.getText().toString().trim();
                String passwordCheck = password.getText().toString().trim();

                if (TextUtils.isEmpty(firstNameCheck)){
                    email.setError("Firstname is Required");
                    return;
                }

                if (TextUtils.isEmpty(lastNameCheck)){
                    email.setError("Lastname is Required");
                    return;
                }

                if (TextUtils.isEmpty(emailCheck)){
                    email.setError("Email is Required");
                    return;
                }

                if (TextUtils.isEmpty(passwordCheck)){
                    password.setError("Password is required");
                    return;
                }

                if (password.length() < 6){
                    password.setError("Password should be 6 character or more");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(emailCheck, passwordCheck).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "User Registration Successful!",Toast.LENGTH_LONG).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("firstName", firstNameCheck);
                            user.put("LastName", lastNameCheck);
                            user.put("email", emailCheck);
                            user.put("type", "user");
//                            user.put("cart", Arrays.asList());

                            fireStore.collection("users").document(userID)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                            startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                        } else {
                            Toast.makeText(Register.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(Register.this, "ERROR: FAIL",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}