package amrk000.salehunter.adapter;

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

import amrk000.salehunter.R;

public class CreateStoreViewPagerAdapter extends RecyclerView.Adapter<CreateStoreViewPagerAdapter.ViewHolder> {
    Context context;

    String[] titles;
    String[] desc;
    int[] images;

    public CreateStoreViewPagerAdapter(Context context){
        this.context=context;
        titles= new String[]{context.getString(R.string.store_intro_title_1), context.getString(R.string.store_intro_title_2), context.getString(R.string.store_intro_title_3), context.getString(R.string.store_intro_title_4)};
        desc= new String[]{context.getString(R.string.store_intro_description_1),
                context.getString(R.string.store_intro_description_2),
                context.getString(R.string.store_intro_description_3),
                context.getString(R.string.store_intro_description_4)};
        images= new int[]{R.drawable.create_store_intro1,
                R.drawable.create_store_intro2,
                R.drawable.create_store_intro3,
                R.drawable.create_store_intro4};
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