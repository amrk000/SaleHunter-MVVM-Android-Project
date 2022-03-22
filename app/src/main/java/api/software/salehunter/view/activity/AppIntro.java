package api.software.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Scroller;

import java.lang.reflect.Field;

import api.software.salehunter.R;
import api.software.salehunter.adapter.AppIntroViewPagerAdapter;
import api.software.salehunter.databinding.ActivityAppIntroBinding;
import api.software.salehunter.util.SharedPrefManager;

public class AppIntro extends AppCompatActivity {
    ActivityAppIntroBinding vb;
    boolean end;

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

        vb.appIntroSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endIntro();
            }
        });

        vb.appIntroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(end) endIntro();
            else vb.appIntroViewPager.setCurrentItem(vb.appIntroViewPager.getCurrentItem()+1,true);
            }
        });

        vb.appIntroViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==adapter.getCount()-1){
                    end=true;
                    vb.appIntroButton.setText("Start Now");
                    vb.appIntroSkip.setVisibility(View.GONE);
                }
                else{
                    end=false;
                    vb.appIntroButton.setText("Next");
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