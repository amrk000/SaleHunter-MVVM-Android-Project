package api.software.salehunter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import api.software.salehunter.model.ProductModel;
import api.software.salehunter.R;

public class HomeProductsAdapter extends RecyclerView.Adapter<HomeProductsAdapter.ViewHolder>{
    ArrayList<ProductModel> Data;
    Context context;

    public HomeProductsAdapter(Context context,ArrayList<ProductModel> Data){
        this.Data = Data;
        this.context=context;
    }

    //item view inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView brand, name, price, rate;
        ImageView image, store;
        CheckBox favourite;
        ImageView rateIcon;

        public ViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.product_card_brand);
            name = view.findViewById(R.id.product_card_Name);
            price = view.findViewById(R.id.product_card_price);
            rate = view.findViewById(R.id.product_card_rate);
            image = view.findViewById(R.id.product_card_image);
            store = view.findViewById(R.id.product_card_store);
            favourite = view.findViewById(R.id.product_card_favourite);
            rateIcon = view.findViewById(R.id.product_card_rate_icon);

        }

    }

    @NonNull
    @Override
    public HomeProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.brand.setText(Data.get(position).getBrand());
        holder.name.setText(Data.get(position).getName());
        holder.price.setText(Data.get(position).getPrice());
        holder.rate.setText(String.valueOf(Data.get(position).getRate()));
        holder.favourite.setChecked(Data.get(position).isFavourite());

        //store code
        switch (Data.get(position).getStore()){
            case "amazon":
                holder.store.setImageDrawable(context.getDrawable(R.drawable.amazon_logo_svg));
                break;

            case "jumia":
                holder.store.setImageDrawable(context.getDrawable(R.drawable.jumia_seeklogo_com_));
                break;
        }

        //image code

        Glide.with(context)
                .load(R.drawable.zz_havit_stereo_speakers_175)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(250))
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(30)))
                .into(holder.image);

        //rate code
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }
}
