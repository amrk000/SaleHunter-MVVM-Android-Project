package api.software.salehunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String APP_STATUS = "0";
    public static final String APP_SETTINGS = "1";

    public static final String FIRST_LAUNCH = "firstLaunch";
    boolean firstLaunch;

    public static final String SIGNED_IN = "signedIn";
    public static final String REMEMBER_ME = "rememberMe";
    boolean signedIn;

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

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE).edit()
                        .putBoolean(MainActivity.FIRST_LAUNCH,true)
                        .apply();

                getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE)
                        .edit()
                        .putBoolean(MainActivity.SIGNED_IN,false)
                        .apply();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        });

    }

}