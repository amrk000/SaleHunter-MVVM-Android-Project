package api.software.salehunter.mainFragments.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import api.software.salehunter.R;
import api.software.salehunter.UnderConstructionFragment;
import api.software.salehunter.UnderlayNavigationDrawer;

public class HomeFragment extends Fragment {
    FloatingActionButton fab;
    BottomAppBar bottomAppBar;
    Button search,onSale,favourite,notifs;
    FrameLayout frameLayout;

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

        //fab initial position adjustment
        fab.setVisibility(View.INVISIBLE);

        search.postDelayed(()->{
            animateFab(search);
            fab.setImageDrawable(getActivity().getDrawable(R.drawable.search_icon));
            fab.setVisibility(View.VISIBLE);
        },400);

        switchFragment(new SearchFragment()); //initial fragment

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.search_icon));
                switchFragment(new SearchFragment());
            }
        });

        onSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.on_sale_icon));
                switchFragment(new UnderConstructionFragment());
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.favourite_icon));
                switchFragment(new UnderConstructionFragment());
            }
        });

        notifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab(view);
                fab.setImageDrawable(getActivity().getDrawable(R.drawable.notification_icon));
                switchFragment(new UnderConstructionFragment());
            }
        });

    }

    void animateFab(View view){
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        float center = rect.exactCenterX();

        AnimatorSet animationSet = new AnimatorSet();

        ObjectAnimator animation = ObjectAnimator.ofFloat(fab, "X",center-(fab.getWidth()/2f));
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(fab, "scaleX",1.0f,0.75f,1.0f);
        animation2.setDuration(600);
        animation2.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animation3 = ObjectAnimator.ofFloat(fab, "scaleY",1.0f,0.75f,1.0f);
        animation3.setDuration(600);
        animation3.setInterpolator(new DecelerateInterpolator());

        animationSet.playTogether(animation,animation2,animation3);
        animationSet.start();

    }

    void switchFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_in,R.anim.fragment_out)
                .replace(frameLayout.getId(),fragment)
                .commit();

    }

}