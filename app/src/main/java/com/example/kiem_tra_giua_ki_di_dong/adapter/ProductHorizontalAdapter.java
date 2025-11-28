package com.example.kiem_tra_giua_ki_di_dong.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.model.Product;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductHorizontalAdapter extends RecyclerView.Adapter<ProductHorizontalAdapter.ProductViewHolder> {

    private List<Product> products;
    private Context context;
    private OnProductClickListener onProductClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductHorizontalAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.onProductClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_horizontal, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvProductName;
        private TextView tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);

            itemView.setOnClickListener(v -> {
                if (onProductClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onProductClickListener.onProductClick(products.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());

            // Format price in Vietnamese currency
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvProductPrice.setText(formatter.format(product.getPrice()));

            // Load image using Glide
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_placeholder_product)
                        .error(R.drawable.ic_placeholder_product)
                        .into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.ic_placeholder_product);
            }
        }
    }
}