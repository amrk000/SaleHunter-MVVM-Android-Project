package amrk000.salehunter.view.fragment.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.google.android.material.tabs.TabLayoutMediator;

import amrk000.salehunter.R;
import amrk000.salehunter.adapter.CreateStoreViewPagerAdapter;
import amrk000.salehunter.databinding.FragmentCreateStoreIntroBinding;
import amrk000.salehunter.view.activity.MainActivity;

public class CreateStoreIntroFragment extends Fragment {
    private FragmentCreateStoreIntroBinding vb;
    private NavController navController;

    public CreateStoreIntroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentCreateStoreIntroBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });

        vb.createStoreIntroCreate.setVisibility(View.GONE);
        vb.createStoreIntroDown.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.scroll_down_icon));

        CreateStoreViewPagerAdapter adapter = new CreateStoreViewPagerAdapter(getContext());
        vb.createStoreIntroViewpager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(vb.createStoreIntroViewpagerPageIndicator, vb.createStoreIntroViewpager,(tab, position) ->{ });
        tabLayoutMediator.attach();

        vb.createStoreIntroViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position == adapter.getItemCount()-1 && (vb.createStoreIntroCreate.getVisibility() != View.VISIBLE)) {
                    vb.createStoreIntroDown.setVisibility(View.GONE);
                    vb.createStoreIntroDown.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.zoom_out));
                    new Handler().postDelayed(()->{
                    vb.createStoreIntroCreate.setVisibility(View.VISIBLE);
                    vb.createStoreIntroCreate.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in));
                    },500);
                }
                else if(vb.createStoreIntroDown.getVisibility() != View.VISIBLE) {
                    vb.createStoreIntroCreate.setVisibility(View.GONE);
                    vb.createStoreIntroCreate.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.zoom_out));

                    vb.createStoreIntroDown.setVisibility(View.VISIBLE);
                    vb.createStoreIntroDown.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in));
                    vb.createStoreIntroDown.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.scroll_down_icon));
                }
            }
        });

        vb.createStoreIntroDown.setOnClickListener( button ->{
            vb.createStoreIntroViewpager.setCurrentItem(vb.createStoreIntroViewpager.getCurrentItem()+1,true);
        });

        vb.createStoreIntroCreate.setOnClickListener( button ->{
            navController.navigate(R.id.action_createStoreFragment_to_createStoreFragment2);
        });

    }

}