package api.software.salehunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;

public class AppIntro extends AppCompatActivity {
    Button next, skip;
    ViewPager viewPager;
    TabLayout indicator;

    boolean end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        next = findViewById(R.id.appIntro_button);
        skip = findViewById(R.id.appIntro_skip);
        viewPager = findViewById(R.id.appIntro_viewPager);
        indicator = findViewById(R.id.appIntro_pageIndicator);

        AppIntroViewPagerAdapter adapter = new AppIntroViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        indicator.setupWithViewPager(viewPager);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endIntro();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(end) endIntro();
            else viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==adapter.getCount()-1){
                    end=true;
                    next.setText("Start Now");
                    skip.setVisibility(View.GONE);
                }
                else{
                    end=false;
                    next.setText("Next");
                    skip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(viewPager, new Scroller(this) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy,750);
                }});

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void endIntro(){

        getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE).edit()
                .putBoolean(MainActivity.FIRST_LAUNCH,false)
                .apply();

        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}