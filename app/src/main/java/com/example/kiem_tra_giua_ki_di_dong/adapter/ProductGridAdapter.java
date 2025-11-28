package com.example.kiem_tra_giua_ki_di_dong.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.model.Product;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;

    private List<Product> products = new ArrayList<>();
    private Context context;
    private boolean isLoading = false;
    private OnProductClickListener onProductClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductGridAdapter(Context context) {
        this.context = context;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.onProductClickListener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> newProducts) {
        int startPosition = this.products.size();
        this.products.addAll(newProducts);
        notifyItemRangeInserted(startPosition, newProducts.size());
    }

    public void showLoading() {
        if (!isLoading) {
            isLoading = true;
            notifyItemInserted(products.size());
        }
    }

    public void hideLoading() {
        if (isLoading) {
            isLoading = false;
            notifyItemRemoved(products.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == products.size() && isLoading) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return products.size() + (isLoading ? 1 : 0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            Product product = products.get(position);
            ((ProductViewHolder) holder).bind(product);
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvProductStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvName);
            tvProductPrice = itemView.findViewById(R.id.tvPrice);
//            tvProductStock = itemView.findViewById(R.id.tvStock);

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

            tvProductStock.setText("Còn lại: " + product.getStock());

            // Log image URL for debugging
            android.util.Log.d("ProductAdapter", "Image URL: " + product.getImageUrl());

            // Load image using Glide
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                String imageUrl = product.getImageUrl();

                // If relative URL, prepend base URL
                if (!imageUrl.startsWith("http")) {
                    imageUrl = "http://10.0.2.2:8080" + (imageUrl.startsWith("/") ? "" : "/") + imageUrl;
                }

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder_product)
                        .error(R.drawable.ic_placeholder_product)
                        .into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.ic_placeholder_product);
            }
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}