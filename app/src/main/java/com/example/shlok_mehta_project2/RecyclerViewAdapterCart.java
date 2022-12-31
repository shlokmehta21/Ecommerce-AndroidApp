package com.example.shlok_mehta_project2;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapterCart extends RecyclerView.Adapter<RecyclerViewAdapterCart.RecyclerViewHolder> {

    private ArrayList<ProductData> productDataArrayList;
    private final Context mContext;

    public RecyclerViewAdapterCart(ArrayList<ProductData> recyclerDataArrayList, Context mcontext) {
        this.productDataArrayList = recyclerDataArrayList;
        this.mContext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productTitle.setText(productDataArrayList.get(position).getTitle());
        //ImageView: Glide Library
        Glide.with(mContext).load(productDataArrayList.get(position).getImage()).into(holder.productImg);
        holder.price.setText("$" + productDataArrayList.get(position).getPrice());
        holder.quantity.setText(String.valueOf(productDataArrayList.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return productDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView productTitle;
        private ImageView productImg;
        private TextView price;
        private TextView quantity;
        private CardView parentLayout;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.productTitle);
            productImg = itemView.findViewById(R.id.productImg);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.qty);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}