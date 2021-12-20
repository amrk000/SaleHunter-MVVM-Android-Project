package api.software.salehunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import api.software.salehunter.accountSign.AccountSign;
import api.software.salehunter.intro.AppIntro;
import api.software.salehunter.mainFragments.home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    public static final String APP_STATUS = "0";
    public static final String APP_SETTINGS = "1";

    public static final String FIRST_LAUNCH = "firstLaunch";
    boolean firstLaunch;

    public static final String SIGNED_IN = "signedIn";
    public static final String REMEMBER_ME = "rememberMe";
    boolean signedIn;

    View view,backView;
    ImageButton menuIcon;
    UnderlayNavigationDrawer underlayNavigationDrawer;
    RadioGroup menu;
    FrameLayout frameLayout;
    TextView fragmentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.lay_on,R.anim.lay_off);

        firstLaunch = getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE).getBoolean(MainActivity.FIRST_LAUNCH,true);
        signedIn = getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE).getBoolean(MainActivity.SIGNED_IN,false);

        if(firstLaunch){
            startActivity(new Intent(this, AppIntro.class));
            finish();
        }
        else if(!signedIn && getIntent().getBooleanExtra(REMEMBER_ME,true)){
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



    void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_in,R.anim.fragment_out)
                .replace(frameLayout.getId(),fragment)
                .commit();

        underlayNavigationDrawer.closeMenu();
    }

    void signOut(){

        getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE)
                .edit()
                .putBoolean(MainActivity.SIGNED_IN,false)
                .apply();

        startActivity(new Intent(getApplicationContext(), AccountSign.class));
        finish();

    }

}