package amrk000.salehunter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import amrk000.salehunter.R;

public class ImagesSliderViewPagerAdapter extends RecyclerView.Adapter<ImagesSliderViewPagerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<String> imagesLinks;

    public ImagesSliderViewPagerAdapter(Context context){
        this.context  = context;
        this.imagesLinks = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.images_slider_image);
        }

    }

    @Override
    public ImagesSliderViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_slider_image, parent, false);
        return new ImagesSliderViewPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesSliderViewPagerAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(imagesLinks.get(position))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(holder.imageView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ImagesSliderViewPagerAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public int getItemCount() {
        return imagesLinks.size();
    }

    public void addImages(ArrayList<String> imagesLinks){
        this.imagesLinks.addAll(imagesLinks);
        notifyItemRangeInserted(getItemCount(),imagesLinks.size());
    }
}
