package api.software.salehunter.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import api.software.salehunter.R;

public class CreateStoreViewPagerAdapter extends RecyclerView.Adapter<CreateStoreViewPagerAdapter.ViewHolder> {
    Context context;

    String[] titles={"Do You Have A Store?","Create a Store Page","Reach More Customers","Even if you have website !"};

    String[] desc={"Get rid of creating website costs and efforts we help you go online easily",
            "get a page for your store and showcase your products totally free in few steps",
            "We will help customers find you easier so your store can grow & get more popularity",
            "Customers who find your products can go directly to your website and buy what they need quickly"};

    int[] images={R.drawable.create_store_intro1,
            R.drawable.create_store_intro2,
            R.drawable.create_store_intro3,
            R.drawable.create_store_intro4};

    public CreateStoreViewPagerAdapter(Context context){
        this.context=context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.viewpager_page_title);
            desc = (TextView) view.findViewById(R.id.viewpager_page_text);
            imageView = (ImageView) view.findViewById(R.id.viewpager_page_imageView);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(titles[position]);
        holder.desc.setText(desc[position]);
        holder.imageView.setImageDrawable(context.getDrawable(images[position]));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        holder.title.setVisibility(View.INVISIBLE);
        holder.desc.setVisibility(View.INVISIBLE);
        holder.imageView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(()->{
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_from_bottom));
            },250);
        new Handler().postDelayed(()->{
            holder.title.setVisibility(View.VISIBLE);
            holder.title.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_from_bottom));
            },500);
        new Handler().postDelayed(()->{
            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_from_bottom));
            },750);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }


}