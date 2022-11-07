package amrk000.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.FacebookSdk;

import java.util.Locale;

import amrk000.salehunter.BuildConfig;
import amrk000.salehunter.R;
import amrk000.salehunter.databinding.ActivityMainBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.UserModel;
import amrk000.salehunter.util.AppSettingsManager;
import amrk000.salehunter.util.NetworkBroadcastReceiver;
import amrk000.salehunter.util.SharedPrefManager;
import amrk000.salehunter.util.UserAccountManager;
import amrk000.salehunter.view.UnderlayNavigationDrawer;
import amrk000.salehunter.viewmodel.activity.MainActivityViewModel;



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
    protected void attachBaseContext(Context newBase) {
        String language;
        if(AppSettingsManager.isLanguageSystemDefault(newBase)) language = Locale.getDefault().getLanguage();
        else language = AppSettingsManager.getLanguageKey(newBase);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        newBase.getResources().getConfiguration().setLocale(locale);
        newBase.getResources().getConfiguration().setLayoutDirection(locale);

        super.attachBaseContext(newBase);
    }

    void changeLocale(){
        String language;
        if(AppSettingsManager.isLanguageSystemDefault(this)) language = Locale.getDefault().getLanguage();
        else language = AppSettingsManager.getLanguageKey(this);

        Locale locale = new Locale(language);
        Configuration config = getResources().getConfiguration();
        Locale.setDefault(locale);
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set App Settings
        switch (AppSettingsManager.getTheme(this)){
            case AppSettingsManager.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppSettingsManager.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) changeLocale();

        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);

        vb = ActivityMainBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on, R.anim.lay_off);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        navController = Navigation.findNavController(this, R.id.main_FragmentContainer);

        firstLaunch = SharedPrefManager.get(this).isFirstLaunch();
        rememberMe = SharedPrefManager.get(this).isRememberMeChecked();
        signedIn = true; //SharedPrefManager.get(this).isSignedIn();
        token = UserAccountManager.getToken(this, UserAccountManager.TOKEN_TYPE_BEARER);
        user = UserAccountManager.getUser(this);

        justSignedIn = getIntent().getBooleanExtra(JUST_SIGNED_IN, false);

        FacebookSdk.setClientToken(getApplication().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplication());

        if (firstLaunch) {
            startActivity(new Intent(this, AppIntro.class));
            finish();
        } else if (!signedIn) {
            startActivity(new Intent(this, AccountSign.class));
            finish();
        } else if (!(rememberMe || justSignedIn)) UserAccountManager.signOut(this, true);
        else {

            // Comment these two lines to skip sign in
            loadUserData(null); //From Local Storage
            if (!justSignedIn) syncUserData(); //From Server

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

                    case R.id.menu_dashboard:
                        underlayNavigationDrawer.closeMenu();
                        new Handler().postDelayed(() -> {
                            navController.popBackStack(R.id.dashboardFragment,true);

                            Bundle bundle = new Bundle();
                            bundle.putLong("storeId", user.getStoreId());
                            navController.navigate(R.id.dashboardFragment, bundle, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
                        }, underlayNavigationDrawer.getAnimationDuration());

                        break;

                    case R.id.menu_mystore:
                        if (user.hasStore()) {
                            underlayNavigationDrawer.closeMenu();
                            new Handler().postDelayed(() -> {
                                navController.popBackStack(R.id.storePageFragment,true);

                                Bundle bundle = new Bundle();
                                bundle.putLong("storeId", user.getStoreId());
                                navController.navigate(R.id.storePageFragment, bundle, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());

                            }, underlayNavigationDrawer.getAnimationDuration());
                        } else navigateToFragment(R.id.createStoreFragment);
                        break;

                    case R.id.menu_settings:
                        navigateToFragment(R.id.settingsFragment);
                        break;

                    case R.id.menu_about:
                        navigateToFragment(R.id.aboutFragment);
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

            //Sale Hunter links detection
            vb.appVersion.setText(BuildConfig.VERSION_NAME);
            Uri appLinkData = getIntent().getData();

            if(appLinkData!=null){
                String url = appLinkData.getPath();
                if(url.lastIndexOf("/") == url.length()-1) url = url.substring(0,url.lastIndexOf("/"));

                if(appLinkData.getPath().contains("pid=")){
                    String productId = url.substring(url.indexOf("=")+1);

                    Bundle bundle = new Bundle();
                    bundle.putLong("productId", Long.parseLong(productId));
                    navController.navigate(R.id.productPageFragment, bundle);
                }
                else if(appLinkData.getPath().contains("store-profile=")){
                    String storeId = url.substring(url.indexOf("=")+1);

                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", Long.parseLong(storeId));
                    navController.navigate(R.id.storePageFragment, bundle);
                }
                else if(appLinkData.getPath().equals("/profile")) vb.menuProfile.performClick();
                else if(appLinkData.getPath().equals("/about-us")) vb.menuAbout.performClick();
            }

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
        else if(vb.menu.getCheckedRadioButtonId() != R.id.menu_home && navController.getBackQueue().getSize() == 3) vb.menuHome.setChecked(true);
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
            navController.popBackStack(id,true);
            navController.navigate(id,null,new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());

        },underlayNavigationDrawer.getAnimationDuration());
    }

    public void syncUserData(){
            if(UserAccountManager.getUser(this).getSignedInWith() != UserModel.SIGNED_IN_WITH_EMAIL) return;

            viewModel.getUser(token).observe(this, response -> {
                switch (response.code()){
                    case BaseResponseModel.SUCCESSFUL_OPERATION:
                        UserModel user = response.body().getUser();
                        UserAccountManager.updateUser(getApplicationContext(),user);
                        loadUserData(user);
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

    public void loadUserData(UserModel userModel){
        if(userModel!=null) user = userModel;
        else user = UserAccountManager.getUser(this);

        vb.menuUsername.setText(user.getFullName());
        vb.menuAccountType.setText(user.getAccountType());
        Glide.with(this).load(user.getImageLink())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(50))
                .placeholder(R.drawable.profile_placeholder)
                .circleCrop()
                .into(vb.menuProfilePic);

        if(user.hasStore()) vb.menuDashboard.setVisibility(View.VISIBLE);
        else vb.menuDashboard.setVisibility(View.GONE);
    }

    public NavController getAppNavController(){ return navController;}

    public void setTitle(String title){
        vb.currentFragmentTitle.post(()->{
            vb.currentFragmentTitle.setText(title);
        });
    }

}