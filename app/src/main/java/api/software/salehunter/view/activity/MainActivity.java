package api.software.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.databinding.ActivityMainBinding;
import api.software.salehunter.model.ResponseModel;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.model.UserResponseModel;
import api.software.salehunter.util.NetworkBroadcastReceiver;
import api.software.salehunter.util.SharedPrefManager;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.view.UnderlayNavigationDrawer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityMainBinding vb;
    private UnderlayNavigationDrawer underlayNavigationDrawer;
    private NetworkBroadcastReceiver networkBroadcastReceiver;

    private Repository repository;

    public static final String REMEMBER_ME = "rememberMe";
    public static final String JUST_SIGNED_IN = "justSignedIn";

    private boolean rememberMe;
    private boolean firstLaunch;
    private boolean signedIn;
    private boolean justSignedIn;
    private String token;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        vb = ActivityMainBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on,R.anim.lay_off);

        firstLaunch = SharedPrefManager.get(this).isFirstLaunch();
        signedIn = SharedPrefManager.get(this).isSignedIn();
        token = UserAccountManager.getToken(this, UserAccountManager.TOKEN_TYPE_BEARER);
        user = UserAccountManager.getUser(this);

        rememberMe = getIntent().getBooleanExtra(REMEMBER_ME,true);
        justSignedIn = getIntent().getBooleanExtra(JUST_SIGNED_IN,false);

        if(firstLaunch){
            startActivity(new Intent(this, AppIntro.class));
            finish();
        }
        else if(!signedIn){
            startActivity(new Intent(this, AccountSign.class));
            finish();
        }
        else {

            repository = new Repository();

            navController = Navigation.findNavController(this, R.id.main_FragmentContainer);

            underlayNavigationDrawer = new UnderlayNavigationDrawer(this, vb.menuFrontView, findViewById(R.id.main_FragmentContainer), vb.menuBackView, vb.menuButton);

            networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
            registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            vb.menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    vb.currentFragmentTitle.setText(((RadioButton) findViewById(i)).getText().toString());

                    switch (i) {

                        case R.id.menu_home:
                            navigateToFragment(R.id.homeFragment);
                            break;

                        case R.id.menu_profile:
                            navigateToFragment(R.id.profileFragment);
                            break;

                        case R.id.menu_signout:
                            UserAccountManager.signOut(MainActivity.this);
                            break;

                        default:
                            navigateToFragment(R.id.underConstructionFragment2);
                            break;
                    }

                }
            });

            //load user name and pic in menu
            vb.menuUsername.setText(user.getFullName());
            Glide.with(this).load(user.getImageLink())
                    .centerCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .circleCrop()
                    .into(vb.menuProfilePic);

            if(!justSignedIn) refreshUserData();

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        underlayNavigationDrawer.detectTouch(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if(underlayNavigationDrawer.isOpened()) underlayNavigationDrawer.closeMenu();
        else if(vb.menu.getCheckedRadioButtonId() != R.id.menu_home && getSupportFragmentManager().getBackStackEntryCount() == 0) vb.menu.getChildAt(0).performClick();
        else super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!rememberMe) UserAccountManager.signOut(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!rememberMe) UserAccountManager.signIn(this,token,user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(networkBroadcastReceiver!=null) unregisterReceiver(networkBroadcastReceiver);
    }

    void navigateToFragment(int id){
        underlayNavigationDrawer.closeMenu();
        new Handler().postDelayed(()->{

            navController.navigate(id,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());

        },underlayNavigationDrawer.getAnimationDuration());
    }

    public void refreshUserData(){
        new Handler().postDelayed(()->{

        repository.getUser(token, new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        UserModel user = response.body().getUser();
                        UserAccountManager.updateUser(getApplicationContext(),user);
                        updateMenuUserData(user);
                        break;

                    case ResponseModel.FAILED_AUTH:
                        UserAccountManager.signOutForced(MainActivity.this);
                        break;
                }

            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) { t.printStackTrace();}
        });

    },1000);

    }

    public void updateMenuUserData(UserModel user){
        vb.menuUsername.setText(user.getFullName());
        Glide.with(this).load(user.getImageLink())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(250))
                .placeholder(R.drawable.profile_placeholder)
                .circleCrop()
                .into(vb.menuProfilePic);
    }

}