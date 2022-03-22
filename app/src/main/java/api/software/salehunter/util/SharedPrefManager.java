package api.software.salehunter.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import api.software.salehunter.model.UserModel;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private static SharedPreferences sharedPreferences;

    // Keys
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String SIGNED_IN = "signedIn";
    private static final String TOKEN = "token";
    private static final String USER = "user";

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences("prefs",MODE_PRIVATE);
    }

    public static SharedPrefManager get(Context context){
        if(instance == null) instance = new SharedPrefManager(context);
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
        return new Gson().fromJson(userJson, UserModel.class);
    }

    public void setUser(UserModel user){
        sharedPreferences.edit()
                .putString(USER,new Gson().toJson(user))
                .apply();
    }

}
