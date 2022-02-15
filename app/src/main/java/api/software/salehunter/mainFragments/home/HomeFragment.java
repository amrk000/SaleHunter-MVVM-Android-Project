package api.software.salehunter.mainFragments.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import api.software.salehunter.LoadingDialog;
import api.software.salehunter.R;
import api.software.salehunter.UnderConstructionFragment;

public class HomeFragment extends Fragment {
    FloatingActionButton fab;
    BottomAppBar bottomAppBar;
    Button search,onSale,favourite,notifs;
    FrameLayout frameLayout;
    FragmentManager fragmentManager;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //NavBar
        fab = view.findViewById(R.id.home_BottomAppBarFab);
        bottomAppBar = view.findViewById(R.id.home_BottomAppBar);
        search = view.findViewById(R.id.home_BarSearch);
        onSale = view.findViewById(R.id.home_BarOnsale);
        favourite = view.findViewById(R.id.home_BarFavourite);
        notifs = view.findViewById(R.id.home_BarNotifs);
        frameLayout = view.findViewById(R.id.home_frameLayout);

        fragmentManager = getActivity().getSupportFragmentManager();

        view.post(()->{
            animateFab(search);
            fab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_search));
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_search));
                switchFragment(new SearchFragment());
            }
        });

        onSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_onsale));
                switchFragment(new UnderConstructionFragment());
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_favourite));
                switchFragment(new UnderConstructionFragment());
            }
        });

        notifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.navbar_icon_notification));
                switchFragment(new UnderConstructionFragment());
            }
        });

        switchFragment(new SearchFragment()); //initial fragment

    }

    void animateFab(View view){
        new Handler(Looper.getMainLooper()).post(()->{
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        float center = rect.exactCenterX();

        AnimatorSet animationSet = new AnimatorSet();

        ObjectAnimator animation = ObjectAnimator.ofFloat(fab, "X",center-(fab.getWidth()/2f));
        animation.setDuration(350);
        animation.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(fab, "scaleX",1.0f,0.75f,1.0f);
        animation2.setDuration(350);
        animation2.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animation3 = ObjectAnimator.ofFloat(fab, "scaleY",1.0f,0.75f,1.0f);
        animation3.setDuration(350);
        animation3.setInterpolator(new DecelerateInterpolator());

        animationSet.playTogether(animation,animation2,animation3);
        animationSet.start();
        });
    }

    void switchFragment(Fragment fragment){
        new Handler().postDelayed(()-> {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out)
                    .replace(frameLayout.getId(), fragment)
                    .commit();
        },350);
    }

}