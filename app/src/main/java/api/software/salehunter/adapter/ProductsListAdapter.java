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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import api.software.salehunter.R;
import api.software.salehunter.model.ProductModel;

public class ProductsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<ProductModel> Data;
    private RecyclerView recyclerView;
    Context context;

    private final ProductModel loadingCardObject = new ProductModel();

    private final int TYPE_DATA_VIEW_HOLDER = 0;
    private final int TYPE_LOADING_VIEW_HOLDER = 1;

    private LastItemReachedListener lastItemReachedListener;
    private ItemInteractionListener itemInteractionListener;

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

    public ProductsListAdapter(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        this.Data = new ArrayList<>();
    }

    //item view inner class
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView brand, name, price, rate, date;
        ImageView image, store;
        CheckBox favourite;
        ImageView rateIcon;

        public DataViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.product_list_item_brand);
            name = view.findViewById(R.id.product_list_item_Name);
            price = view.findViewById(R.id.product_list_item_price);
            rate = view.findViewById(R.id.product_list_item_rate);
            image = view.findViewById(R.id.product_list_item_image);
            store = view.findViewById(R.id.product_list_item_store);
            favourite = view.findViewById(R.id.product_list_item_favourite);
            rateIcon = view.findViewById(R.id.product_list_item_rate_icon);
            date = view.findViewById(R.id.product_list_item_date);
        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(Data.get(position)==loadingCardObject) return TYPE_LOADING_VIEW_HOLDER;
        else return TYPE_DATA_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_LOADING_VIEW_HOLDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_loading_layout, parent, false);
            return new ProductsListAdapter.LoadingViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_layout, parent, false);
        return new ProductsListAdapter.DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_DATA_VIEW_HOLDER) {
            DataViewHolder holder = (DataViewHolder)  viewHolder;

            holder.brand.setText(Data.get(position).getBrand());
            holder.name.setText(Data.get(position).getName());
            holder.price.setText(Data.get(position).getPrice()+"LE");
            holder.rate.setText(String.valueOf(Data.get(position).getRate()));
            holder.favourite.setChecked(Data.get(position).isFavorite());
            holder.date.setText(dateTimeConvert(Data.get(position).getDate()));

            if(hideFavButton) holder.favourite.setVisibility(View.GONE);
            if(Data.get(position).getBrand().isEmpty()) holder.brand.setVisibility(View.GONE);
            if(Data.get(position).getRate()==-1){
                holder.rateIcon.setVisibility(View.GONE);
                holder.rate.setVisibility(View.GONE);
            }
            if(Data.get(position).getDate() == null || Data.get(position).getDate().isEmpty()) holder.date.setVisibility(View.GONE);

            //Store
            if(isDarkModeEnabled()) holder.store.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            if(Data.get(position).getStoreName().isEmpty()) holder.store.setVisibility(View.GONE);
            else {
                switch (Data.get(position).getStoreName().toLowerCase()) {
                    case "amazon":
                        holder.store.setImageDrawable(context.getDrawable(R.drawable.amazon_logo_svg));
                        break;

                    case "jumia":
                        holder.store.setImageDrawable(context.getDrawable(R.drawable.jumia_seeklogo_com_));
                        break;

                    default:
                        Glide.with(context)
                                .load(Data.get(position).getStoreLogo())
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade(250))
                                .into(holder.store);
                        break;
                }
            }

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
        if(holder.getAdapterPosition() == Data.size()-1 && lastItemReachedListener!=null && !isLoading()) lastItemReachedListener.onLastItemReached();
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    public ArrayList<ProductModel> getData() {
        return Data;
    }

    public void addProduct(ProductModel product){
        recyclerView.post(()->{
            Data.add(product);
            notifyItemInserted(getItemCount());
        });
    }

    public void removeProduct(ProductModel product){
        recyclerView.post(()->{
            int index = Data.indexOf(product);
            Data.remove(product);
            notifyItemRemoved(index);
        });
    }

    public void removeProductByIndex(int i){
        Data.remove(i);
        notifyItemRemoved(i);
    }

    public void removeProductById(long id){
        recyclerView.post(()->{
            for(int i=0; i<Data.size(); i++){
                if(Data.get(i).getId()==id){
                    Data.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        });
    }

    public void addProducts(ArrayList<ProductModel> products){
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

    public void setHideFavButton(boolean hide){
        hideFavButton = hide;
    }

    public boolean isDarkModeEnabled() {
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public String dateTimeConvert(String dateTime){
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String outputPattern = "dd/MM/yyyy";

        try {
            SimpleDateFormat input = new SimpleDateFormat(inputPattern);
            input.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date parsed = input.parse(dateTime);

            SimpleDateFormat destFormat = new SimpleDateFormat(outputPattern);
            destFormat.setTimeZone(TimeZone.getDefault());

            return destFormat.format(parsed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-";
    }
}
