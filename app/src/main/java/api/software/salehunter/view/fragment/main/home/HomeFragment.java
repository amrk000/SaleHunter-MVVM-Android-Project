package api.software.salehunter.view.fragment.main.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import api.software.salehunter.R;
import api.software.salehunter.databinding.FragmentHomeBinding;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.viewmodel.fragment.main.home.HomeViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding vb;
    private NavController navController;
    private HomeViewModel viewModel;

    private int index = 0;

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
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle("Home");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vb.homeBottomAppBarFab.setVisibility(View.INVISIBLE);
        switch (index){
            case 0:
                animateFab(vb.homeBarSearch, true);
                vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_search, getActivity().getTheme()));
                break;
            case 1:
                animateFab(vb.homeBarOnsale, true);
                vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_onsale, getActivity().getTheme()));
                break;
            case 2:
                animateFab(vb.homeBarFavourite, true);
                vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_favourite, getActivity().getTheme()));
                break;
            case 3:
                animateFab(vb.homeBarHistory, true);
                vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.menu_icon_history, getActivity().getTheme()));
                break;
        }

        navController = Navigation.findNavController(view.findViewById(R.id.home_framgmentContainer));
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        vb.homeBarSearch.setOnClickListener(button -> {
            index = 0;
            animateFab(button,false);
            navController.navigate(R.id.searchFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_search, getActivity().getTheme()));
        });

        vb.homeBarOnsale.setOnClickListener(button -> {
            index = 1;
            animateFab(button,false);
            navController.navigate(R.id.underConstructionFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_onsale, getActivity().getTheme()));
        });

        vb.homeBarFavourite.setOnClickListener(button -> {
            index = 2;
            animateFab(button,false);
            navController.navigate(R.id.favFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_favourite, getActivity().getTheme()));
        });

        vb.homeBarHistory.setOnClickListener(button -> {
            index = 3;
            animateFab(button,false);
            navController.navigate(R.id.historyFragment,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.menu_icon_history, getActivity().getTheme()));
        });

    }

    public void setFabSelector(int  i){

    }

    void animateFab(View view, boolean initial){
        view.post(()->{

            int duration = 400;

            AnimatorSet animationSet = new AnimatorSet();

            if(initial){
                duration = 0;
                new Handler(Looper.getMainLooper()).postDelayed(()->{
                    vb.homeBottomAppBarFab.setVisibility(View.VISIBLE);

                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleX",0.0f,1.0f);
                    animation1.setDuration(400);
                    animation1.setInterpolator(new DecelerateInterpolator());

                    ObjectAnimator animation2 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleY",0.0f,1.0f);
                    animation2.setDuration(400);
                    animation2.setInterpolator(new DecelerateInterpolator());

                    animationSet.playTogether(animation1,animation2);
                    animationSet.start();

                },duration);
            }

            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            float center = rect.exactCenterX();

            ObjectAnimator animation = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "X",center-(vb.homeBottomAppBarFab.getWidth()/2f));
            animation.setDuration(duration);
            animation.setInterpolator(new DecelerateInterpolator());

            ObjectAnimator animation2 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleX",1.0f,0.75f,1.0f);
            animation2.setDuration(duration);
            animation2.setInterpolator(new DecelerateInterpolator());

            ObjectAnimator animation3 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleY",1.0f,0.75f,1.0f);
            animation3.setDuration(duration);
            animation3.setInterpolator(new DecelerateInterpolator());

            animationSet.playTogether(animation,animation2,animation3);
            animationSet.start();

        });
    }

}