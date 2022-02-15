package api.software.salehunter.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private static SharedPreferences sharedPreferences;

    // Keys
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String SIGNED_IN = "signedIn";

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences("settings",MODE_PRIVATE);
    }

    public static SharedPrefManager get(Context context){
        if(instance == null){
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH,true);
    }

    public void setFirstLaunch(boolean value){
        sharedPreferences.edit()
                .putBoolean(FIRST_LAUNCH,value)
                .apply();
    }

    public boolean isSignedIn() {
        return sharedPreferences.getBoolean(SIGNED_IN,false);
    }

    public void setSignedIn(boolean value){
        sharedPreferences.edit()
                .putBoolean(SIGNED_IN,value)
                .apply();
    }

}
