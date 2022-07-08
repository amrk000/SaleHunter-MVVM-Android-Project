package api.software.salehunter.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import api.software.salehunter.R;
import api.software.salehunter.model.ProductModel;

public class ProductsSearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ProductModel> Data;
    private RecyclerView recyclerView;
    private Context context;

    private ProductsCardAdapter onlineRecyclerAdapter;

    private final ProductModel loadingLayoutObject = new ProductModel();
    private final ProductModel onlineLabelLayoutObject = new ProductModel();
    private final ProductModel localLabelLayoutObject = new ProductModel();
    private final ProductModel horizontalProductsLayoutObject = new ProductModel();
    private final ProductModel noResultItemObject = new ProductModel();

    private final int TYPE_PRODUCT_VIEW_HOLDER = 0;
    private final int TYPE_LOADING_VIEW_HOLDER = 1;
    private final int TYPE_LABEL_VIEW_HOLDER = 2;
    private final int TYPE_HORIZONTAL_PRODUCTS_VIEW_HOLDER = 3;
    private final int TYPE_NO_RESULT_VIEW_HOLDER = 4;

    private LastItemReachedListener lastItemReachedListener;
    private ItemInteractionListener itemInteractionListener;

    private boolean noResultsFound = false;

    public interface LastItemReachedListener {
        void onLastOnlineProductReached();
        void onLastLocalProductReached();
    }

    public interface ItemInteractionListener {
        void onProductClicked(long productId, String storeType);
        void onProductAddedToFav(long productId, boolean favChecked);
    }

    public void setLastItemReachedListener(LastItemReachedListener lastItemReachedListener) {
        this.lastItemReachedListener = lastItemReachedListener;
    }

    public void setItemInteractionListener(ItemInteractionListener itemInteractionListener){
        this.itemInteractionListener = itemInteractionListener;
    }

    public ProductsSearchResultsAdapter(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        this.Data = new ArrayList<>();

        Data.add(onlineLabelLayoutObject);
        Data.add(horizontalProductsLayoutObject);
        Data.add(localLabelLayoutObject);
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView brand, name, price, rate, sale;
        ImageView image, store;
        CheckBox favourite;
        ImageView rateIcon;

        public ProductViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.product_list_item_brand);
            name = view.findViewById(R.id.product_list_item_Name);
            price = view.findViewById(R.id.product_list_item_price);
            rate = view.findViewById(R.id.product_list_item_rate);
            image = view.findViewById(R.id.product_list_item_image);
            store = view.findViewById(R.id.product_list_item_store);
            favourite = view.findViewById(R.id.product_list_item_favourite);
            rateIcon = view.findViewById(R.id.product_list_item_rate_icon);
            sale = view.findViewById(R.id.product_list_item_salePercent);
        }

    }

    public static class HorizontalProductsViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public HorizontalProductsViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.result_online_results_recyclerView);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    public static class LabelViewHolder extends RecyclerView.ViewHolder {

        TextView label;

        public LabelViewHolder(View view) {
            super(view);
            label = view.findViewById(R.id.product_list_label);
        }
    }

    public static class NoResultViewHolder extends RecyclerView.ViewHolder {
        public NoResultViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(Data.get(position) == loadingLayoutObject) return TYPE_LOADING_VIEW_HOLDER;
        else if(Data.get(position) == onlineLabelLayoutObject) return TYPE_LABEL_VIEW_HOLDER;
        else if(Data.get(position) == localLabelLayoutObject) return TYPE_LABEL_VIEW_HOLDER;
        else if(Data.get(position) == horizontalProductsLayoutObject) return TYPE_HORIZONTAL_PRODUCTS_VIEW_HOLDER;
        else if(Data.get(position) == noResultItemObject) return TYPE_NO_RESULT_VIEW_HOLDER;
        else return TYPE_PRODUCT_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == TYPE_LOADING_VIEW_HOLDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_loading_layout, parent, false);
            return new LoadingViewHolder(view);
        }
        else if(viewType == TYPE_LABEL_VIEW_HOLDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_label_layout, parent, false);
            return new LabelViewHolder(view);
        }
        else if(viewType == TYPE_HORIZONTAL_PRODUCTS_VIEW_HOLDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_horizontal_products_layout, parent, false);
            return new HorizontalProductsViewHolder(view);
        }
        else if(viewType == TYPE_NO_RESULT_VIEW_HOLDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_no_results_layout, parent, false);
            return new NoResultViewHolder(view);
        }

        //Default ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if(getItemViewType(position) == TYPE_PRODUCT_VIEW_HOLDER) {

            ProductViewHolder holder = (ProductViewHolder)  viewHolder;

            holder.brand.setText(Data.get(position).getBrand());
            holder.name.setText(Data.get(position).getName());
            holder.price.setText(Data.get(position).getPrice()+"LE");
            holder.rate.setText(String.valueOf(Data.get(position).getRate()));
            holder.favourite.setChecked(Data.get(position).isFavorite());
            holder.sale.setText(Data.get(position).getSalePercent()+"% SALE");

            if(Data.get(position).getSalePercent() == 0) holder.sale.setVisibility(View.GONE);

            if(Data.get(position).getRate()==0){
                holder.rate.setVisibility(View.INVISIBLE);
                holder.rateIcon.setVisibility(View.INVISIBLE);
            }

            //Store
            //if(isDarkModeEnabled()) holder.store.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            Glide.with(context)
                    .load(Data.get(position).getStoreLogo())
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(holder.store);

            //Image
            Glide.with(context)
                    .load(Data.get(position).getImage())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemInteractionListener!=null) itemInteractionListener.onProductClicked(Data.get(holder.getAdapterPosition()).getId(), Data.get(holder.getAdapterPosition()).getStoreType());
                }
            });

            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Data.get(holder.getAdapterPosition()).setFavorite(holder.favourite.isChecked());
                    if(itemInteractionListener!=null) itemInteractionListener.onProductAddedToFav(Data.get(holder.getAdapterPosition()).getId(),holder.favourite.isChecked());
                }
            });

        }
        else if(getItemViewType(position) == TYPE_HORIZONTAL_PRODUCTS_VIEW_HOLDER){

            HorizontalProductsViewHolder holder = (HorizontalProductsViewHolder)  viewHolder;

            if(onlineRecyclerAdapter==null) onlineRecyclerAdapter = new ProductsCardAdapter(context, holder.recyclerView);

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(onlineRecyclerAdapter);
            onlineRecyclerAdapter.setLastItemReachedListener(new ProductsCardAdapter.LastItemReachedListener() {
                @Override
                public void onLastItemReached() {
                    if(lastItemReachedListener != null) lastItemReachedListener.onLastOnlineProductReached();
                }
            });

            onlineRecyclerAdapter.setItemInteractionListener(new ProductsCardAdapter.ItemInteractionListener() {
                @Override
                public void onProductClicked(long productId, String storeType) {
                    itemInteractionListener.onProductClicked(productId, storeType);
                }

                @Override
                public void onProductAddedToFav(long productId,boolean favChecked) {
                    itemInteractionListener.onProductAddedToFav(productId,favChecked);
                }
            });

        }
        else if(getItemViewType(position) == TYPE_LABEL_VIEW_HOLDER){

            LabelViewHolder holder = (LabelViewHolder)  viewHolder;

            if(Data.get(position) == onlineLabelLayoutObject) holder.label.setText("Online Stores");
            else holder.label.setText("Local Stores");
        }

    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(noResultsFound) return;
        if(holder.getAdapterPosition() == getLocalProductsCount()-1 && lastItemReachedListener!=null && !isLocalProductsLoading()) lastItemReachedListener.onLastLocalProductReached();
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public int getLocalProductsCount(){
        return Data.size()-3;
    }

    public int getOnlineProductsCount(){
        return onlineRecyclerAdapter.getItemCount();
    }

    public void addLocalProduct(ProductModel product){
        if(noResultsFound) return;

        recyclerView.post(()->{
            Data.add(product);
            notifyItemInserted(getItemCount());
        });
    }

    public void addOnlineProduct(ProductModel product){
        recyclerView.post(()->{
            onlineRecyclerAdapter.addProductNoPost(product);
        });
    }


    public void addLocalProducts(ArrayList<ProductModel> products){
        if(noResultsFound) return;

        recyclerView.post(()-> {
            Data.addAll(products);
            notifyItemRangeInserted(getItemCount(), products.size());
        });
    }

    public void addOnlineProducts(ArrayList<ProductModel> products){
        recyclerView.post(()-> {
            onlineRecyclerAdapter.addProductsNoPost(products);
        });
    }

    public boolean isLocalProductsLoading(){
        return Data.contains(loadingLayoutObject);
    }

    public void setLocalProductsLoading(boolean loading){
        recyclerView.post(()-> {
            if(isLocalProductsLoading() == loading) return;
            if (loading) {
                Data.add(loadingLayoutObject);
                notifyItemInserted(getItemCount());
            } else {
                Data.remove(loadingLayoutObject);
                notifyItemChanged(getItemCount());
            }
        });

    }

    public boolean isOnlineProductsLoading(){
        return onlineRecyclerAdapter.isLoading();
    }

    public void setOnlineProductsLoading(boolean loading){
        recyclerView.post(()-> {
            onlineRecyclerAdapter.setLoadingNoPost(loading);
        });
    }

    public void showNoLocalResultsFound(){
        if(Data.contains(noResultItemObject)) return;

        Data.add(noResultItemObject);
        notifyItemInserted(0);
        noResultsFound = true;
    }

    public void showNoOnlineResultsFound(){
        recyclerView.post(()-> {
            onlineRecyclerAdapter.showNoResultsFound();
        });
    }

    public void clearProducts(){
        onlineRecyclerAdapter.clearProducts();

        int count =  getItemCount();
        Data.subList(3, count).clear();
        notifyItemRangeRemoved(3, count);
    }

    public boolean isDarkModeEnabled() {
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
