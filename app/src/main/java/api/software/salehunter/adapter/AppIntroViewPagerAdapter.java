package api.software.salehunter.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import api.software.salehunter.R;

public class AppIntroViewPagerAdapter extends PagerAdapter {

    Activity context;


    String[] titles={"Find Products Easily","Cashier in Your Pocket","Hunt The Best Deals","Have a Store ?"};

    String[] texts={"Search for any product to get its price in market and location of nearest stores that selling it",
            "Scan any product barcode to get it's price and info instantly in few clicks using your phone",
            "Find best price and don't miss any discounts or price drops on your favourite products",
            "Get rid of creating website costs and efforts we help you reach more customers for free"};

    int[] images={R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3,
            R.drawable.intro4};

    public AppIntroViewPagerAdapter(Activity context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View item = context.getLayoutInflater().inflate(R.layout.app_intro_page,container,false);

        ImageView imageView = item.findViewById(R.id.intro_page_imageView);
        imageView.setImageDrawable(context.getDrawable(images[position]));

        TextView title = item.findViewById(R.id.intro_page_title);
        title.setText(titles[position]);

        TextView text = item.findViewById(R.id.intro_page_text);
        text.setText(texts[position]);

        container.addView(item);

        return item;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
