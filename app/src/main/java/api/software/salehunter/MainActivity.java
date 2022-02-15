package api.software.salehunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import api.software.salehunter.accountSign.AccountSign;
import api.software.salehunter.intro.AppIntro;
import api.software.salehunter.mainFragments.home.HomeFragment;
import api.software.salehunter.util.NetworkBroadcastReceiver;
import api.software.salehunter.util.SharedPrefManager;

public class MainActivity extends AppCompatActivity {
    boolean firstLaunch;
    boolean signedIn;
    boolean rememberMe;

    public static final String REMEMBER_ME = "rememberMe";

    View view,backView;
    ImageButton menuIcon;
    UnderlayNavigationDrawer underlayNavigationDrawer;
    RadioGroup menu;
    FrameLayout frameLayout;
    TextView fragmentTitle;
    FragmentManager fragmentManager;

    NetworkBroadcastReceiver networkBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.lay_on,R.anim.lay_off);

        firstLaunch = SharedPrefManager.get(this).isFirstLaunch();
        signedIn = SharedPrefManager.get(this).isSignedIn();
        rememberMe = getIntent().getBooleanExtra(REMEMBER_ME,true);

        if(firstLaunch){
            startActivity(new Intent(this, AppIntro.class));
            finish();
        }
        else if(!signedIn){
            startActivity(new Intent(this, AccountSign.class));
            finish();
        }

        view = findViewById(R.id.menu_frontView);
        backView = findViewById(R.id.menu_backView);
        menuIcon = findViewById(R.id.imageButton);
        menu = findViewById(R.id.menu);
        frameLayout = findViewById(R.id.main_FrameLayout);
        fragmentTitle = findViewById(R.id.current_fragment_title);

        underlayNavigationDrawer = new UnderlayNavigationDrawer(this,view,frameLayout,backView,menuIcon);
        fragmentManager = getSupportFragmentManager();

        networkBroadcastReceiver = new NetworkBroadcastReceiver(getSupportFragmentManager());
        registerReceiver(networkBroadcastReceiver , new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                fragmentTitle.setText(((RadioButton)findViewById(i)).getText().toString());

                switch (i){

                    case R.id.menu_home:
                        switchFragment(new HomeFragment());
                        break;

                    case R.id.menu_signout:
                        signOut();
                        break;

                    default:
                        switchFragment(new UnderConstructionFragment());
                        break;
                }

            }
        });


        getSupportFragmentManager().beginTransaction()
                .replace(frameLayout.getId(),new HomeFragment())
                .commit();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        underlayNavigationDrawer.detectTouch(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if(underlayNavigationDrawer.isOpened()) underlayNavigationDrawer.closeMenu();
        else if(menu.getCheckedRadioButtonId() != R.id.menu_home && getSupportFragmentManager().getBackStackEntryCount() == 0) menu.getChildAt(0).performClick();
        else super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!rememberMe) SharedPrefManager.get(this).setSignedIn(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!rememberMe) SharedPrefManager.get(this).setSignedIn(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkBroadcastReceiver);
    }

    void switchFragment(Fragment fragment){

        underlayNavigationDrawer.closeMenu();

        new Handler().postDelayed(()->{
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_in,0)
                    .replace(frameLayout.getId(),fragment)
                    .commit();
        },underlayNavigationDrawer.getAnimationDuration());
    }

    void signOut(){
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        //signout from facebook
        LoginManager.getInstance().logOut();

        //signout from google
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignIn.getClient(this, googleSignInOptions).signOut();

        SharedPrefManager.get(this).setSignedIn(false);
        
        startActivity(new Intent(getApplicationContext(), AccountSign.class));
        finish();

    }

}