package amrk000.salehunter.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.LocaleKt;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Locale;

import amrk000.salehunter.R;

public class AppIntroViewPagerAdapter extends PagerAdapter {

    Activity context;

    String[] titles;
    String[] texts;
    int[] images;
    
    public AppIntroViewPagerAdapter(Activity context){
        this.context=context;
        titles= new String[]{context.getString(R.string.intro_title_1), context.getString(R.string.intro_title_2), context.getString(R.string.intro_title_3), context.getString(R.string.intro_title_4)};
        texts= new String[]{context.getString(R.string.intro_description_1),
                context.getString(R.string.intro_description_2),
                context.getString(R.string.intro_description_3),
                context.getString(R.string.intro_description_4)};
        images= new int[]{R.drawable.intro1,
                R.drawable.intro2,
                R.drawable.intro3,
                R.drawable.intro4};
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
        View item = context.getLayoutInflater().inflate(R.layout.view_pager_page,container,false);

        int pos = position;

        //RTL Support for ViewPager 1 (ViewPager 2 is Recommended)
        if(LocaleKt.getLayoutDirection(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL) pos = titles.length - position - 1;

        ImageView imageView = item.findViewById(R.id.viewpager_page_imageView);
        imageView.setImageDrawable(context.getDrawable(images[pos]));

        TextView title = item.findViewById(R.id.viewpager_page_title);
        title.setText(titles[pos]);

        TextView text = item.findViewById(R.id.viewpager_page_text);
        text.setText(texts[pos]);

        container.addView(item);

        return item;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
