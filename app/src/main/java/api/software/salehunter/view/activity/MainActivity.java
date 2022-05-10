package api.software.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.FacebookSdk;

import api.software.salehunter.BuildConfig;
import api.software.salehunter.R;
import api.software.salehunter.databinding.ActivityMainBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.util.NetworkBroadcastReceiver;
import api.software.salehunter.util.SharedPrefManager;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.view.UnderlayNavigationDrawer;
import api.software.salehunter.viewmodel.activity.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityMainBinding vb;
    private MainActivityViewModel viewModel;
    private UnderlayNavigationDrawer underlayNavigationDrawer;
    private NetworkBroadcastReceiver networkBroadcastReceiver;

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

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        navController = Navigation.findNavController(this, R.id.main_FragmentContainer);

        firstLaunch = SharedPrefManager.get(this).isFirstLaunch();
        rememberMe = SharedPrefManager.get(this).isRememberMeChecked();
        signedIn = SharedPrefManager.get(this).isSignedIn();
        token = UserAccountManager.getToken(this, UserAccountManager.TOKEN_TYPE_BEARER);
        user = UserAccountManager.getUser(this);

        justSignedIn = getIntent().getBooleanExtra(JUST_SIGNED_IN,false);

        FacebookSdk.setClientToken(getApplication().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplication());

        if(firstLaunch){
            startActivity(new Intent(this, AppIntro.class));
            finish();
        }
        else if(!signedIn){
            startActivity(new Intent(this, AccountSign.class));
            finish();
        }
        else if(!(rememberMe || justSignedIn)) UserAccountManager.signOut(this, true);
        else {

            //load user name and pic in menu
            setMenuUserData(user); //From Local Storage
            if(!justSignedIn) syncUserData(); //From Server

            //Side Menu
            underlayNavigationDrawer = new UnderlayNavigationDrawer(this, vb.menuFrontView, findViewById(R.id.main_FragmentContainer), vb.menuBackView, vb.menuButton);
            vb.menu.setOnCheckedChangeListener((radioGroup, i) -> {

                vb.currentFragmentTitle.setText(((RadioButton) findViewById(i)).getText().toString());

                switch (i) {

                    case R.id.menu_home:
                        navigateToFragment(R.id.homeFragment);
                        break;

                    case R.id.menu_profile:
                        navigateToFragment(R.id.profileFragment);
                        break;

                    case R.id.menu_signout:
                        UserAccountManager.signOut(MainActivity.this, false);
                        break;

                    default:
                        navigateToFragment(R.id.underConstructionFragment2);
                        break;
                }

            });

            //Network Checker
            networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
            registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        }

        vb.appVersion.setText(BuildConfig.VERSION_NAME);
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

    public void syncUserData(){
            if(UserAccountManager.getUser(this).getAccountType() != UserModel.ACCOUNT_TYPE_EMAIL) return;

            viewModel.getUser(token).observe(this, response -> {
                switch (response.code()){
                    case BaseResponseModel.SUCCESSFUL_OPERATION:
                        UserModel user = response.body().getUser();
                        UserAccountManager.updateUser(getApplicationContext(),user);
                        setMenuUserData(user);
                        break;

                    case BaseResponseModel.FAILED_AUTH:
                        UserAccountManager.signOut(MainActivity.this, true);
                        break;

                        case BaseResponseModel.FAILED_REQUEST_FAILURE:
                            //Toast.makeText(this, "Data Sync Failed !", Toast.LENGTH_SHORT).show();
                            break;
                }
            });
    }

    public void setMenuUserData(UserModel user){
        vb.menuUsername.setText(user.getFullName());
        Glide.with(this).load(user.getImageLink())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(50))
                .placeholder(R.drawable.profile_placeholder)
                .circleCrop()
                .into(vb.menuProfilePic);
    }

    public NavController getAppNavController(){ return navController;}

    public void setTitle(String title){
        vb.currentFragmentTitle.post(()->{
            vb.currentFragmentTitle.setText(title);
        });
    }

}