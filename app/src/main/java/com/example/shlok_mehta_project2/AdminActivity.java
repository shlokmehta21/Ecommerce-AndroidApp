package com.example.shlok_mehta_project2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminActivity extends AppCompatActivity {

    private String id, title, description, price;
    private Uri uri;
    Button addProduct;
    EditText name, priceProd, desc;
    ImageView upload,logoutBtn;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        addProduct = findViewById(R.id.addProduct);
        name = findViewById(R.id.prodName);
        priceProd = findViewById(R.id.prodPrice);
        desc = findViewById(R.id.prodDesc);
        upload = findViewById(R.id.uploadImg);
        logoutBtn = findViewById(R.id.logoutBtn);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = name.getText().toString().trim();
                price = priceProd.getText().toString().trim();
                description = desc.getText().toString().trim();

                if (TextUtils.isEmpty(title)){
                    name.setError("Title is Required");
                    return;
                }

                if (TextUtils.isEmpty(price)){
                    priceProd.setError("Price is Required");
                    return;
                }

                if (TextUtils.isEmpty(description)){
                    desc.setError("Description is Required");
                    return;
                }

                addProduct();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
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
    }

    private void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("products/"+id+".png");
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseFirestore.getInstance()
                                                .collection("products")
                                                .document(id)
                                                .update("image", uri.toString());
                                        Toast.makeText(AdminActivity.this, "DONE", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        name.setText("");
                        priceProd.setText("");
                        desc.setText("");
                        upload.setImageResource(R.drawable.ic_baseline_upload_file_24);
                    }
                });
    }

    private void addProduct() {
        id = UUID.randomUUID().toString();
        Map<String, Object> product = new HashMap<>();
        product.put("name", title);
        product.put("price", price);
        product.put("description", description);
        product.put("image", null);
        product.put("quantity", 1);

        FirebaseFirestore.getInstance()
                .collection("products")
                .document(id)
                .set(product);
        uploadImage();
        Toast.makeText(AdminActivity.this, "PRODUCT UPLOADED", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        upload.setImageURI(uri);
    }
}