package amrk000.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.LocaleKt;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.Locale;

import amrk000.salehunter.R;
import amrk000.salehunter.adapter.AppIntroViewPagerAdapter;
import amrk000.salehunter.databinding.ActivityAppIntroBinding;
import amrk000.salehunter.util.SharedPrefManager;

public class AppIntro extends AppCompatActivity {
    private ActivityAppIntroBinding vb;

    private boolean end;
    private boolean rtl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        vb = ActivityAppIntroBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);

        AppIntroViewPagerAdapter adapter = new AppIntroViewPagerAdapter(this);
        vb.appIntroViewPager.setAdapter(adapter);
        vb.appIntroPageIndicator.setupWithViewPager(vb.appIntroViewPager);

        //RTL Support for ViewPager 1 (ViewPager 2 is Recommended)
        if(LocaleKt.getLayoutDirection(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL)
        {
            rtl = true;
            vb.appIntroViewPager.setCurrentItem(adapter.getCount()-1,false); //set last page as start (pages are inverted in adapter)
            vb.appIntroPageIndicator.setLayoutDirection(View.LAYOUT_DIRECTION_LTR); //force left to right for dots indicator
        }

        vb.appIntroSkip.setOnClickListener(button -> {
            endIntro();
        });

        vb.appIntroButton.setOnClickListener(button -> {
            int dir = 1;
            //RTL Support for ViewPager 1 (ViewPager 2 is Recommended)
            if(rtl) dir = -1;

            if(end) endIntro();
            else vb.appIntroViewPager.setCurrentItem(vb.appIntroViewPager.getCurrentItem()+dir,true);
        });

        vb.appIntroViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int lastPage = adapter.getCount()-1;
                //RTL Support for ViewPager 1 (ViewPager 2 is Recommended)
                if(rtl) lastPage = 0;

                if(position==lastPage){
                    end=true;
                    vb.appIntroButton.setText(getString(R.string.Start_Now));
                    vb.appIntroSkip.setVisibility(View.GONE);
                }
                else{
                    end=false;
                    vb.appIntroButton.setText(getString(R.string.next));
                    vb.appIntroSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(vb.appIntroViewPager, new Scroller(this) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy,750);
                }});

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void endIntro(){
        SharedPrefManager.get(this).setFirstLaunch(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}