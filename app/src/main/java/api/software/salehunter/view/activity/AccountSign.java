package api.software.salehunter.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.databinding.ActivityAccountSignBinding;
import api.software.salehunter.model.ResponseModel;
import api.software.salehunter.model.EmailVerificationModel;
import api.software.salehunter.model.ResetPasswordModel;
import api.software.salehunter.model.SignInModel;
import api.software.salehunter.model.SignUpModel;
import api.software.salehunter.model.SocialAuthModel;
import api.software.salehunter.model.UserResponseModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.NetworkBroadcastReceiver;
import api.software.salehunter.util.SharedPrefManager;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.view.fragment.dialogs.LoadingDialog;
import api.software.salehunter.view.fragment.dialogs.MessageDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSign extends AppCompatActivity {
    private ActivityAccountSignBinding vb;
    private NavController navController;

    NetworkBroadcastReceiver networkBroadcastReceiver;

    private CallbackManager facebookCallbackManager;
    private GoogleSignInClient googleSignInClient;

    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        vb = ActivityAccountSignBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on,R.anim.null_anim);
        navController = Navigation.findNavController(this, R.id.account_sign_fragmentsContainer);

        AppEventsLogger.activateApp(getApplication());
        facebookCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_backend_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        repository = new Repository();

        vb.accountSignBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
        registerReceiver(networkBroadcastReceiver , new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (task.isSuccessful()) {

                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    String token = account.getIdToken();
                    googleAuthBackend(token);

                } catch (ApiException e) {
                    // The ApiException status code indicates the detailed failure reason.
                    // Please refer to the GoogleSignInStatusCodes class reference for more information.
                    Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                    DialogsProvider.get(this).setLoading(false);
                    DialogsProvider.get(AccountSign.this).messageDialog("Authentication Error", "Google authentication has unknown error");
                }

            } else DialogsProvider.get(this).setLoading(false);
        }

        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void googleAuth(){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        if(GoogleSignIn.getLastSignedInAccount(this) != null) googleSignInClient.signOut();

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public void facebookAuth(){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        if(AccessToken.getCurrentAccessToken()!=null) LoginManager.getInstance().logOut();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            String token = loginResult.getAccessToken().getToken();
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String picture = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            SocialAuthModel socialAuthModel = new SocialAuthModel();
                            socialAuthModel.setFullName(first_name+" "+last_name);
                            socialAuthModel.setEmail(email);
                            socialAuthModel.setAccessToken(token);
                            socialAuthModel.setClientId(id);

                            facebookAuthBackend(token);

                        } catch (JSONException e) { e.printStackTrace();}

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,last_name,email,id,picture");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Authentication Canceled", "Facebook authentication is canceled");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Authentication Error", "Facebook authentication has unknown error");
            }
        });

    }

    public void googleAuthBackend(String token){

        repository.googleAuth(token, new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                    case ResponseModel.SUCCESSFUL_CREATION:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(MainActivity.JUST_SIGNED_IN,true);

                        UserAccountManager.signIn(AccountSign.this, intent,response.headers().get("Authorization"), response.body().getUser());
                        break;

                    case ResponseModel.FAILED_INVALID_DATA:
                        DialogsProvider.get(AccountSign.this).messageDialog("Authentication Failed", "please try again later !");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                t.printStackTrace();
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Server Error", "Json Serialization Error");
                //DialogsProvider.get(AccountSign.this).messageDialog("Authentication Failed", "Please Check your connection !");
            }
        });
    }

    public void facebookAuthBackend(String token){

        repository.facebookAuth(token, new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                    case ResponseModel.SUCCESSFUL_CREATION:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(MainActivity.JUST_SIGNED_IN,true);

                        UserAccountManager.signIn(AccountSign.this, intent,response.headers().get("Authorization"), response.body().getUser());
                        break;

                    case ResponseModel.FAILED_INVALID_DATA:
                        DialogsProvider.get(AccountSign.this).messageDialog("Authentication Failed", "please try again later !");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());
                    }

            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Authentication Failed", "Please Check your connection !");
            }
        });
    }

    public void signIn(boolean rememberMe, SignInModel signInModel){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        repository.signIn(signInModel, new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(MainActivity.JUST_SIGNED_IN,true);
                        intent.putExtra(MainActivity.REMEMBER_ME,rememberMe);

                        UserAccountManager.signIn(AccountSign.this, intent,response.headers().get("Authorization"), response.body().getUser());
                        break;

                    case ResponseModel.FAILED_NOT_FOUND:
                        DialogsProvider.get(AccountSign.this).messageDialog("Welcome !", "you don't have an account\nplease sign up first");
                        break;

                    case ResponseModel.FAILED_AUTH:
                        DialogsProvider.get(AccountSign.this).messageDialog("Wrong Password", "You can reset password from:\nForgot Passwrod ?");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Sign In Failed", "Please Check your connection !");
            }
        });

    }

    public void signUp(SignUpModel signUpModel){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        repository.signUp(signUpModel, new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_CREATION:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(MainActivity.JUST_SIGNED_IN,true);

                        UserAccountManager.signIn(AccountSign.this, intent,response.headers().get("Authorization"), response.body().getUser());
                        break;

                    case ResponseModel.FAILED_INVALID_DATA:
                        DialogsProvider.get(AccountSign.this).messageDialog("Welcome back !", "you already have an account with this email\nplease sign in");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Sign Up Failed", "Please Check your connection !");
            }
        });

    }

    public void sendEmailVerification(EmailVerificationModel emailVerificationModel){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        repository.sendEmailVerification(emailVerificationModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        Bundle bundle = new Bundle();
                        bundle.putString("email", emailVerificationModel.getEmail());
                        navController.navigate(R.id.action_forgotPasswordFragment_to_verificationCodeFragment,bundle);
                        break;

                    case ResponseModel.FAILED_NOT_FOUND:
                        DialogsProvider.get(AccountSign.this).messageDialog("Email isn't registered", "you don't have an account\nplease sign up first");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());

                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Request Failed", "Please Check your connection !");
            }
        });

    }

    public void resendPinToEmail(EmailVerificationModel emailVerificationModel){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        repository.sendEmailVerification(emailVerificationModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:

                        Snackbar snackbar = Snackbar.make(findViewById(R.id.verification_code_snackbar_colayout),"New PIN is on the way! check your inbox.",15000);
                        snackbar.getView().setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.snackbar,getTheme()));
                        snackbar.setBackgroundTint(getResources().getColor(R.color.lightModesecondary,getTheme()));
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                        snackbar.show();
                        break;

                    case ResponseModel.FAILED_NOT_FOUND:
                        DialogsProvider.get(AccountSign.this).messageDialog("Email isn't registered", "you don't have an account\nplease sign up first");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());

                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Request Failed", "Please Check your connection !");
            }
        });

    }

    public void verifyPin(String pinToken){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        repository.verifyToken(pinToken, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    //valid
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        Bundle bundle = new Bundle();
                        bundle.putString("pin",pinToken);
                        navController.navigate(R.id.action_verificationCodeFragment_to_resetPasswordFragment, bundle);
                        break;

                    //expired
                    case ResponseModel.FAILED_INVALID_DATA:
                        DialogsProvider.get(AccountSign.this).setLoading(false);
                        DialogsProvider.get(AccountSign.this).messageDialog("Pin is expired", "Please resend pin code again");
                        break;

                    //invalid
                    case ResponseModel.FAILED_NOT_FOUND:
                        DialogsProvider.get(AccountSign.this).setLoading(false);
                        DialogsProvider.get(AccountSign.this).messageDialog("Pin is invalid", "Please check it and try again");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Verification Failed", "Please Check your connection !");
            }
        });

    }

    public void resetPassword(ResetPasswordModel resetPasswordModel, String pin){
        DialogsProvider.get(AccountSign.this).setLoading(true);

        repository.resetPassword(pin, resetPasswordModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DialogsProvider.get(AccountSign.this).setLoading(false);

                switch (response.code()){
                    //valid
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        navController.navigate(R.id.action_resetPasswordFragment_to_signInFragment);
                        break;

                    //expired
                    case ResponseModel.FAILED_INVALID_DATA:
                        navController.navigate(R.id.action_resetPasswordFragment_to_signInFragment);
                        DialogsProvider.get(AccountSign.this).messageDialog("Pin is expired", "Please try password reset again");
                        break;

                    //invalid
                    case ResponseModel.FAILED_NOT_FOUND:
                        navController.navigate(R.id.action_resetPasswordFragment_to_signInFragment);
                        DialogsProvider.get(AccountSign.this).messageDialog("Pin is invalid", "Please try password reset again");
                        break;

                    default:
                        DialogsProvider.get(AccountSign.this).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                DialogsProvider.get(AccountSign.this).setLoading(false);
                DialogsProvider.get(AccountSign.this).messageDialog("Reset Failed", "Please Check your connection !");
            }
        });

    }

    public boolean isGoogleServicesAvailable(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        return status == ConnectionResult.SUCCESS;
    }

    public void setTitle(String title) {
        vb.accountSignTitle.setText(title);}

    public boolean isBackButtonVisible(){return vb.accountSignBack.getVisibility() == View.VISIBLE;}

    public void setBackButton(boolean backButton){

        if(backButton){
            vb.accountSignBack.setVisibility(View.VISIBLE);
            vb.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_in));
        }
        else {
            vb.accountSignBack.setVisibility(View.GONE);
            vb.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_out));
        }

    }

}