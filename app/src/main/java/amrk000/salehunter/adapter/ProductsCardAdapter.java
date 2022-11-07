package amrk000.salehunter.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.Locale;

import amrk000.salehunter.model.ProductModel;
import amrk000.salehunter.R;
import amrk000.salehunter.util.AppSettingsManager;

public class ProductsCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ProductModel> Data;
    private RecyclerView recyclerView;
    private Context context;

    private final ProductModel loadingCardObject = new ProductModel();
    private final ProductModel noResultCardObject = new ProductModel();

    private final int TYPE_DATA_VIEW_HOLDER = 0;
    private final int TYPE_LOADING_VIEW_HOLDER = 1;
    private final int TYPE_NO_RESULT_VIEW_HOLDER = 2;

    private LastItemReachedListener lastItemReachedListener;
    private ItemInteractionListener itemInteractionListener;

    private boolean noResultsFound = false;
    private boolean hideFavButton = false;

    public interface LastItemReachedListener {
        void onLastItemReached();
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

    public ProductsCardAdapter(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        this.Data = new ArrayList<>();
    }

    //item view inner class
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView brand, name, price, rate, sale;
        ImageView image, store;
        CheckBox favourite;
        ImageView rateIcon;

        public DataViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.product_card_brand);
            name = view.findViewById(R.id.product_card_Name);
            price = view.findViewById(R.id.product_card_price);
            rate = view.findViewById(R.id.product_card_rate);
            image = view.findViewById(R.id.product_card_image);
            store = view.findViewById(R.id.product_card_store);
            favourite = view.findViewById(R.id.product_card_favourite);
            rateIcon = view.findViewById(R.id.product_card_rate_icon);
            sale = view.findViewById(R.id.product_card_salePercent);
        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    public static class NoResultViewHolder extends RecyclerView.ViewHolder {
        public NoResultViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(Data.get(position)==loadingCardObject) return TYPE_LOADING_VIEW_HOLDER;
        else if(Data.get(position)==noResultCardObject) return TYPE_NO_RESULT_VIEW_HOLDER;
        else return TYPE_DATA_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == TYPE_LOADING_VIEW_HOLDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_loading_layout, parent, false);
            return new LoadingViewHolder(view);
        }
        else if(viewType == TYPE_NO_RESULT_VIEW_HOLDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_no_results_layout, parent, false);
            return new NoResultViewHolder(view);
        }

        //Default ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_layout, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if(getItemViewType(position) == TYPE_DATA_VIEW_HOLDER) {

            DataViewHolder holder = (DataViewHolder)  viewHolder;

            if(renderDataInLocalLanguage()) holder.name.setText(Data.get(position).getNameArabic());
            else holder.name.setText(Data.get(position).getName());
            holder.brand.setText(Data.get(position).getBrand());
            holder.price.setText(Data.get(position).getPrice()+ context.getString(R.string.currency));
            holder.rate.setText(String.valueOf(Data.get(position).getRate()));
            holder.favourite.setChecked(Data.get(position).isFavorite());
            holder.sale.setText(Data.get(position).getSalePercent()+context.getString(R.string.sale_percent));

            if(hideFavButton) holder.favourite.setVisibility(View.GONE);
            if(Data.get(position).getSalePercent() == 0) holder.sale.setVisibility(View.GONE);

            if(Data.get(position).getRate()==0){
                holder.rate.setVisibility(View.INVISIBLE);
                holder.rateIcon.setVisibility(View.INVISIBLE);
            }

            //Store
            //if(isDarkModeEnabled()) holder.store.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            if(Data.get(position).getStoreLogo()!=null)
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
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(noResultsFound) return;
        if(holder.getAdapterPosition() == Data.size()-1 && lastItemReachedListener!=null && !isLoading()) lastItemReachedListener.onLastItemReached();
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public void addProduct(ProductModel product){
        if(noResultsFound) return;

        recyclerView.post(()->{
            Data.add(product);
            notifyItemInserted(getItemCount());
        });
    }

    //NoPost for nested recyclerviews adapters
    public void addProductNoPost(ProductModel product){
        if(noResultsFound) return;

        Data.add(product);
        notifyItemInserted(getItemCount());
    }

    public void addProducts(ArrayList<ProductModel> products){
        if(noResultsFound) return;

        recyclerView.post(()->{
            Data.addAll(products);
            notifyItemRangeInserted(getItemCount(), products.size());
        });
    }

    //NoPost for nested recyclerviews adapters
    public void addProductsNoPost(ArrayList<ProductModel> products){
        if(noResultsFound) return;

        Data.addAll(products);
        notifyItemRangeInserted(getItemCount(), products.size());
    }

    public void clearProducts(){
        Data.clear();
        notifyDataSetChanged();
    }

    public boolean isLoading(){
        return Data.contains(loadingCardObject);
    }

    public void setLoading(boolean loading){
        recyclerView.post(()-> {
            if(isLoading() == loading) return;

            if (loading) {
                Data.add(loadingCardObject);
                notifyItemInserted(getItemCount());
            } else {
                Data.remove(loadingCardObject);
                notifyItemChanged(getItemCount());
            }
        });

    }

    //NoPost for nested recyclerviews adapters
    public void setLoadingNoPost(boolean loading){
        if(isLoading() == loading) return;

        if (loading) {
            Data.add(loadingCardObject);
            notifyItemInserted(getItemCount());
        } else {
            Data.remove(loadingCardObject);
            notifyItemChanged(getItemCount());
        }
    }

    public void showNoResultsFound(){
        if(Data.contains(noResultCardObject)) return;

        Data.add(noResultCardObject);
        notifyItemInserted(0);
        noResultsFound = true;
    }

    public void setHideFavButton(boolean hide){
        hideFavButton = hide;
    }

    public boolean isDarkModeEnabled() {
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }

    boolean renderDataInLocalLanguage(){
        switch (AppSettingsManager.getLanguageKey(context)){
            case AppSettingsManager.LANGUAGE_ENGLISH:
                return false;
            case AppSettingsManager.LANGUAGE_ARABIC:
                return true;
            default:
                String systemLanguage = Locale.getDefault().getLanguage();
                if(systemLanguage.equals(AppSettingsManager.LANGUAGE_ARABIC)) return true;
                else return false;
        }
    }

}
