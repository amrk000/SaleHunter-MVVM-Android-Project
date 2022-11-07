package amrk000.salehunter.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;

import amrk000.salehunter.model.UserModel;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private static SharedPreferences sharedPreferences;

    // User Data Keys
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String REMEMBER_ME = "rememberMe";
    private static final String SIGNED_IN = "signedIn";
    private static final String TOKEN = "token";
    private static final String USER = "user";

    // App Settings Keys
    private static final String THEME = "theme";
    private static final String LANGUAGE = "language";

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences("prefs",MODE_PRIVATE);
    }

    public static SharedPrefManager get(Context context){
        if(instance == null) instance = new SharedPrefManager(context);
        return instance;
    }

    //User Data Methods
    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH,true);
    }

    public void setFirstLaunch(boolean value){
        sharedPreferences.edit()
                .putBoolean(FIRST_LAUNCH,value)
                .apply();
    }

    public boolean isRememberMeChecked() {
        return sharedPreferences.getBoolean(REMEMBER_ME,true);
    }

    public void setRememberMe(boolean value){
        sharedPreferences.edit()
                .putBoolean(REMEMBER_ME,value)
                .apply();
    }

    public boolean isSignedIn() {
        return sharedPreferences.getBoolean(SIGNED_IN,false);
    }

    public void setSignedIn(boolean signedIn){
        sharedPreferences.edit()
                .putBoolean(SIGNED_IN,signedIn)
                .apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN,"");
    }

    public void setToken(String token){
        sharedPreferences.edit()
                .putString(TOKEN,token)
                .apply();
    }

    public UserModel getUser() {
        String userJson = sharedPreferences.getString(USER,"");
        if(userJson.isEmpty()) return new UserModel();
        return new Gson().fromJson(userJson, UserModel.class);
    }

    public void setUser(UserModel user){
        sharedPreferences.edit()
                .putString(USER,new Gson().toJson(user))
                .apply();
    }

    //App Settings Methods
    public int getTheme() {
        return sharedPreferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setTheme(int theme) {
        sharedPreferences.edit()
                .putInt(THEME,theme)
                .apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(LANGUAGE, "en");
    }

    public void setLanguage(String language) {
        sharedPreferences.edit()
                .putString(LANGUAGE,language)
                .apply();
    }

}
