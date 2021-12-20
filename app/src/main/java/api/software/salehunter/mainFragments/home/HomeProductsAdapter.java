package api.software.salehunter.mainFragments.home;

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

import api.software.salehunter.ProductModel;
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

        TextView brand, name, price;
        ImageView image, store;
        CheckBox favourite;
        ImageView[] rate = new ImageView[5];

        public ViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.product_card_brand);
            name = view.findViewById(R.id.product_card_Name);
            price = view.findViewById(R.id.product_card_price);
            image = view.findViewById(R.id.product_card_image);
            store = view.findViewById(R.id.product_card_store);
            favourite = view.findViewById(R.id.product_card_favourite);
            rate[0] = view.findViewById(R.id.product_card_star1);
            rate[1] = view.findViewById(R.id.product_card_star2);
            rate[2] = view.findViewById(R.id.product_card_star3);
            rate[3] = view.findViewById(R.id.product_card_star4);
            rate[4] = view.findViewById(R.id.product_card_star5);

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
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(20)))
                .into(holder.image);

        //rate code
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }
}
