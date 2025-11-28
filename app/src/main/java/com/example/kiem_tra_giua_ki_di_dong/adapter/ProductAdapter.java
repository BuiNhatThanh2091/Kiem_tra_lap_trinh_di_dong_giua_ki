//<!--Dao Tuan Duy - 23162011-->
package com.example.kiem_tra_giua_ki_di_dong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kiem_tra_giua_ki_di_dong.R;
import com.example.kiem_tra_giua_ki_di_dong.model.Product;

import java.text.DecimalFormat;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private List<Product> productList;
    private boolean isLoadingMore = false;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getItemViewType(int position) {
        // Nếu đang load và là item cuối cùng, hiện loading
        if (isLoadingMore && position == productList.size()) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return productList.size() + (isLoadingMore ? 1 : 0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            Product product = productList.get(position);
            ((ProductViewHolder) holder).bind(product);
        }
        // LoadingViewHolder không cần bind
    }

    // Method để set loading state
    public void setLoadingMore(boolean loading) {
        boolean wasLoading = isLoadingMore;
        isLoadingMore = loading;

        if (wasLoading != loading) {
            if (loading) {
                notifyItemInserted(productList.size());
            } else {
                notifyItemRemoved(productList.size());
            }
        }
    }

    // Product ViewHolder
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvRating, tvDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(Product product) {
            tvName.setText(product.getName());

            // Format giá VND
            DecimalFormat formatter = new DecimalFormat("#,###");
            tvPrice.setText(formatter.format(product.getPrice()) + "đ");

//            tvRating.setText(String.valueOf(product.getRating()));
            tvDescription.setText(product.getDescription());

            // Load image using Glide
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .into(ivProduct);

            itemView.setOnClickListener(v -> {
                // Handle click - navigate to product detail
            });
        }
    }

    // Loading ViewHolder
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}