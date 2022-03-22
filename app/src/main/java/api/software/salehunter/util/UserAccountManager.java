package api.software.salehunter.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import api.software.salehunter.R;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.view.fragment.dialogs.LoadingDialog;

public final class UserAccountManager {

    private static UserModel user;

    //Token Type Parameters
    public static final int TOKEN_TYPE_NORMAL = 0;
    public static final int TOKEN_TYPE_BEARER = 1;

    public static void signIn(Activity activity, String token, UserModel user){
        Context context = activity.getApplicationContext();
        //Save User Data
        SharedPrefManager.get(context).setSignedIn(true);
        SharedPrefManager.get(context).setToken(token);
        SharedPrefManager.get(context).setUser(user);
    }

    public static void signIn(Activity activity, Intent intent, String token, UserModel user){
        signIn(activity, token, user);
        //Launch Main Activity
        activity.startActivity(intent);
        activity.onBackPressed();
    }

    public static UserModel getUser(Context context){
        if(user == null) user = SharedPrefManager.get(context).getUser();
        return user;
    }

    public static void updateUser(Context context, UserModel userModel){
        user = userModel;
        SharedPrefManager.get(context).setUser(user);
    }

    public static String getToken(Context context, int type){
        String output ="";
        switch (type){
            case TOKEN_TYPE_NORMAL:
                output = SharedPrefManager.get(context).getToken();
                break;

            case TOKEN_TYPE_BEARER:
                output = "Bearer " + SharedPrefManager.get(context).getToken();
                break;
        }

        return output;
    }

    public static void signOut(Activity activity){
        DialogsProvider.get((AppCompatActivity) activity).setLoading(true);

        Context context = activity.getApplicationContext();

        //clear user account data
        SharedPrefManager.get(context).setSignedIn(false);
        SharedPrefManager.get(context).setToken("");
        SharedPrefManager.get(context).setUser(null);

        //signout from facebook
        if(AccessToken.getCurrentAccessToken()!=null) LoginManager.getInstance().logOut();

        //signout from google
        if(GoogleSignIn.getLastSignedInAccount(context) != null){

            GoogleSignIn.getClient(context, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            ).signOut();

        }

        DialogsProvider.get((AppCompatActivity) activity).setLoading(false);

        //Launch Account Sign Activity
        activity.startActivity(new Intent(context, AccountSign.class));
        activity.finish();
    }

    public static void signOutForced(Activity activity){
        signOut(activity);

        DialogsProvider.get((AppCompatActivity) activity).messageDialog("Session Expired","Please Sign In Again");
    }

}
