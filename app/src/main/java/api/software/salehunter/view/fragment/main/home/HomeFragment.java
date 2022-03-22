package api.software.salehunter.view.fragment.main.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import api.software.salehunter.R;
import api.software.salehunter.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding vb;
    private NavController navController;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentHomeBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view.findViewById(R.id.home_framgmentContainer));

        view.post(()->{
            animateFab(vb.homeBarSearch);
            vb.homeBottomAppBarFab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_search));
        });

        vb.homeBarSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                vb.homeBottomAppBarFab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_search));
                navController.navigate(R.id.searchFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            }
        });

        vb.homeBarOnsale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                vb.homeBottomAppBarFab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_onsale));
                navController.navigate(R.id.underConstructionFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            }
        });

        vb.homeBarFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                vb.homeBottomAppBarFab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_favourite));
                navController.navigate(R.id.underConstructionFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            }
        });

        vb.homeBarNotifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                vb.homeBottomAppBarFab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_notification));
                navController.navigate(R.id.underConstructionFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            }
        });

    }

    void animateFab(View view){
        new Handler(Looper.getMainLooper()).post(()->{
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        float center = rect.exactCenterX();

        AnimatorSet animationSet = new AnimatorSet();

        ObjectAnimator animation = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "X",center-(vb.homeBottomAppBarFab.getWidth()/2f));
        animation.setDuration(400);
        animation.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleX",1.0f,0.75f,1.0f);
        animation2.setDuration(400);
        animation2.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animation3 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleY",1.0f,0.75f,1.0f);
        animation3.setDuration(400);
        animation3.setInterpolator(new DecelerateInterpolator());

        animationSet.playTogether(animation,animation2,animation3);
        animationSet.start();

        });
    }

}