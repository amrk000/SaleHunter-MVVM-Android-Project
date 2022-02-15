package api.software.salehunter.accountSign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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

import api.software.MessageDialog;
import api.software.salehunter.DisconnectedDialog;
import api.software.salehunter.LoadingDialog;
import api.software.salehunter.MainActivity;
import api.software.salehunter.data.AccountRepository;
import api.software.salehunter.databinding.ActivityAccountSignBinding;
import api.software.salehunter.model.ApiResponseModel;
import api.software.salehunter.model.ForgotPasswordModel;
import api.software.salehunter.model.ResetPasswordModel;
import api.software.salehunter.model.SignInModel;
import api.software.salehunter.model.SignUpModel;
import api.software.salehunter.model.SocialAuthModel;
import api.software.salehunter.util.NetworkBroadcastReceiver;
import api.software.salehunter.R;
import api.software.salehunter.util.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSign extends AppCompatActivity {
    private ActivityAccountSignBinding binding;
    private LoadingDialog loadingDialog;
    private MessageDialog messageDialog;

    NetworkBroadcastReceiver networkBroadcastReceiver;

    private CallbackManager facebookCallbackManager;
    private GoogleSignInClient googleSignInClient;
    boolean googleServices;

    private AccountRepository accountRepository;

    private String email, pin; //password Reset

    public static final int SIGN_IN_FRAGMENT = 0;
    public static final int SIGN_UP_FRAGMENT = 1;
    public static final int FORGOT_PASSWORD_FRAGMENT = 2;
    public static final int VERIFICATION_CODE_FRAGMENT = 3;
    public static final int RESET_PASSWORD_FRAGMENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        binding = ActivityAccountSignBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on,R.anim.null_anim);

        googleServices = googleServicesAvailable();

        AppEventsLogger.activateApp(getApplication());
        facebookCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_backend_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        accountRepository = new AccountRepository();
        loadingDialog = new LoadingDialog();
        messageDialog = new MessageDialog();

        binding.accountSignBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.accountSignFrameLayout.getId(),new SignInFragment())
                .commit();

        networkBroadcastReceiver = new NetworkBroadcastReceiver(getSupportFragmentManager());
        registerReceiver(networkBroadcastReceiver , new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                    String personName = account.getDisplayName();
                    String personFamilyName = account.getFamilyName();
                    String personEmail = account.getEmail();
                    String personId = account.getId();
                    Uri personPhoto = account.getPhotoUrl();

                    SocialAuthModel socialAuthModel = new SocialAuthModel();
                    socialAuthModel.setFullName(personName+" "+personFamilyName);
                    socialAuthModel.setEmail(personEmail);
                    socialAuthModel.setAccessToken(token);
                    socialAuthModel.setClientId(personId);

                    socialMediaToBackend(socialAuthModel);

                } catch (ApiException e) {
                    // The ApiException status code indicates the detailed failure reason.
                    // Please refer to the GoogleSignInStatusCodes class reference for more information.
                    Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                    loadingDialog.dismiss();
                    showMessageDialog("Authentication Error", "Google authentication has unknown error");
                }

            } else loadingDialog.dismiss();
        }

        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void googleAuth(){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        if(GoogleSignIn.getLastSignedInAccount(this) != null) googleSignInClient.signOut();

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public void facebookAuth(){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

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

                            socialMediaToBackend(socialAuthModel);

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
                loadingDialog.dismiss();
                showMessageDialog("Authentication Canceled", "Facebook authentication is canceled");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                loadingDialog.dismiss();
                showMessageDialog("Authentication Error", "Facebook authentication has unknown error");
            }
        });

    }

    public void socialMediaToBackend(SocialAuthModel socialAuthModel){

        accountRepository.socialAuth(socialAuthModel, new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                loadingDialog.dismiss();

                switch (response.code()){
                    case SocialAuthModel.STATUS_SUCCESSFUL_SIGN_IN:
                    case SocialAuthModel.STATUS_SUCCESSFUL_SIGN_UP:
                        SharedPrefManager.get(getApplicationContext()).setSignedIn(true);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        startActivity(intent);
                        onBackPressed();
                        break;

                    case SocialAuthModel.STATUS_FAILED_SIGN_IN:
                        errorHandle();
                        //showMessageDialog("Sign In Failed", "please try again later !");
                        break;

                    case SocialAuthModel.STATUS_FAILED_SIGN_UP:
                        errorHandle();
                        //showMessageDialog("Sign Up Failed", "please try again later !");
                        break;

                }

            }

            @Override
            public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                loadingDialog.dismiss();
                showMessageDialog("Sign In Failed", "Please Check your connection !");
            }
        });
    }

    //Login Error Handling
    public void errorHandle(){
        SharedPrefManager.get(getApplicationContext()).setSignedIn(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);
        onBackPressed();
    }

    public void signIn(boolean rememberMe, SignInModel signInModel){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        accountRepository.signIn(signInModel, new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                loadingDialog.dismiss();

                switch (response.code()){
                    case SignInModel.STATUS_SUCCESSFUL:
                        SharedPrefManager.get(getApplicationContext()).setSignedIn(true);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(MainActivity.REMEMBER_ME,rememberMe);

                        startActivity(intent);
                        onBackPressed();

                        break;

                    case SignInModel.STATUS_FAILED_NO_USER_FOUND:
                        showMessageDialog("Welcome !", "you don't have an account\nplease sign up first");
                        break;

                    case SignInModel.STATUS_WRONG_PASSWORD:
                        showMessageDialog("Wrong Password", "You can reset password from:\nForgot Passwrod ?");
                        break;

                }

            }

            @Override
            public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                loadingDialog.dismiss();
                showMessageDialog("Sign In Failed", "Please Check your connection !");
            }
        });

    }

    public void signUp(SignUpModel signUpModel){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        accountRepository.signUp(signUpModel, new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                loadingDialog.dismiss();

                switch (response.code()){
                    case SignUpModel.STATUS_SUCCESSFUL:
                        SharedPrefManager.get(getApplicationContext()).setSignedIn(true);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        onBackPressed();

                        break;

                    case SignUpModel.STATUS_FAILED:
                        showMessageDialog("Welcome back !", "you already have an account with this email\nplease sign in");
                        break;

                    default:
                        //Toast.makeText(getApplicationContext(), "code: "+ response.code()+"\n status: "+ response.body().getStatus()+ "\n message: "+ response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                loadingDialog.dismiss();
                showMessageDialog("Sign Up Failed", "Please Check your connection !");
            }
        });

    }

    public void sendPinToEmail(ForgotPasswordModel forgotPasswordModel){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        accountRepository.forgotPassword(forgotPasswordModel, new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                loadingDialog.dismiss();

                switch (response.code()){
                    case ForgotPasswordModel.STATUS_SUCCESSFUL:
                        changeFragment(AccountSign.VERIFICATION_CODE_FRAGMENT);
                        email = forgotPasswordModel.getEmail();
                        break;

                    case ForgotPasswordModel.STATUS_FAILED_NO_USER_FOUND:
                        showMessageDialog("Email isn't registered", "you don't have an account\nplease sign up first");
                        break;

                }

            }

            @Override
            public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                loadingDialog.dismiss();
                showMessageDialog("Request Failed", "Please Check your connection !");
            }
        });

    }

    public void resendPinToEmail(ForgotPasswordModel forgotPasswordModel){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        accountRepository.forgotPassword(forgotPasswordModel, new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                loadingDialog.dismiss();

                switch (response.code()){
                    case ForgotPasswordModel.STATUS_SUCCESSFUL:

                        Snackbar snackbar = Snackbar.make(findViewById(R.id.verification_code_snackbar_colayout),"New PIN is on the way! Please check your inbox.",15000);
                        snackbar.getView().setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.snackbar,getTheme()));
                        snackbar.setBackgroundTint(getResources().getColor(R.color.lightModesecondary,getTheme()));
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                        snackbar.show();

                        email = forgotPasswordModel.getEmail();
                        break;

                    case ForgotPasswordModel.STATUS_FAILED_NO_USER_FOUND:
                        showMessageDialog("Email isn't registered", "you don't have an account\nplease sign up first");
                        break;

                }

            }

            @Override
            public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                loadingDialog.dismiss();
                showMessageDialog("Request Failed", "Please Check your connection !");
            }
        });

    }

    public void verifyPin(String pinToken){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        accountRepository.verifyToken(pinToken, new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                loadingDialog.dismiss();

                switch (response.code()){
                    //valid
                    case 200:
                        changeFragment(AccountSign.RESET_PASSWORD_FRAGMENT);
                        pin = pinToken;
                        break;

                    //expired
                    case 400:
                        loadingDialog.dismiss();
                        showMessageDialog("Pin is expired", "Please resend pin code again");
                        break;

                    //invalid
                    case 404:
                        loadingDialog.dismiss();
                        showMessageDialog("Pin is invalid", "Please check it and try again");
                        break;
                }

            }

            @Override
            public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                loadingDialog.dismiss();
                showMessageDialog("Verification Failed", "Please Check your connection !");
            }
        });

    }

    public void resetPassword(ResetPasswordModel resetPasswordModel){
        loadingDialog.show(getSupportFragmentManager(),loadingDialog.getTag());

        accountRepository.resetPassword(resetPasswordModel,pin, new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(Call<ApiResponseModel> call, Response<ApiResponseModel> response) {
                loadingDialog.dismiss();

                switch (response.code()){
                    //valid
                    case ResetPasswordModel.STATUS_SUCCESSFUL:
                        changeFragment(AccountSign.SIGN_IN_FRAGMENT);
                        break;

                    //expired
                    case ResetPasswordModel.STATUS_FAILED_TOKEN_EXPIRED:
                        loadingDialog.dismiss();
                        changeFragment(AccountSign.SIGN_IN_FRAGMENT);
                        showMessageDialog("Pin is expired", "Please try password reset again");
                        break;

                    //invalid
                    case ResetPasswordModel.STATUS_FAILED_TOKEN_INVALID:
                        loadingDialog.dismiss();
                        changeFragment(AccountSign.SIGN_IN_FRAGMENT);
                        showMessageDialog("Pin is invalid", "Please try password reset again");
                        break;
                }

            }

            @Override
            public void onFailure(Call<ApiResponseModel> call, Throwable t) {
                loadingDialog.dismiss();
                showMessageDialog("Reset Failed", "Please Check your connection !");
            }
        });

    }

    boolean googleServicesAvailable(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        return status == ConnectionResult.SUCCESS;
    }

    public void showMessageDialog(String title, String subTitle){
        if(messageDialog.isVisible()) messageDialog.dismiss();
        messageDialog.setMessage(title, subTitle);
        messageDialog.show(getSupportFragmentManager(),messageDialog.getTag());
    }

    public void setTitle(String title) {binding.accountSignTitle.setText(title);}

    public boolean isBackButtonVisible(){return binding.accountSignBack.getVisibility() == View.VISIBLE;}

    public void setBackButton(boolean backButton){

        if(backButton){
            binding.accountSignBack.setVisibility(View.VISIBLE);
            binding.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_in));
        }
        else {
            binding.accountSignBack.setVisibility(View.GONE);
            binding.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_out));
        }

    }

    public void changeFragment(int fragment){
            switch (fragment){
                case SIGN_UP_FRAGMENT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_from_bottom,R.anim.slide_to_top,R.anim.slide_from_top,R.anim.slide_to_bottom)
                            .replace(binding.accountSignFrameLayout.getId(),new SignUpFragment())
                            .addToBackStack("home")
                            .commit();
                    break;

                case FORGOT_PASSWORD_FRAGMENT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                            .replace(binding.accountSignFrameLayout.getId(),new ForgotPasswordFragment())
                            .addToBackStack("home")
                            .commit();
                    break;

                case VERIFICATION_CODE_FRAGMENT:
                    getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                        .replace(binding.accountSignFrameLayout.getId(),new VerificationCodeFragment())
                        .addToBackStack("forgotPassword")
                        .commit();
                    break;

                case RESET_PASSWORD_FRAGMENT:
                    getSupportFragmentManager().popBackStack("forgotPassword", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                            .replace(binding.accountSignFrameLayout.getId(),new ResetPasswordFragment())
                            .commit();
                    break;

                default:
                    getSupportFragmentManager().popBackStack();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                            .replace(binding.accountSignFrameLayout.getId(),new SignInFragment())
                            .commit();
                    break;

            }
    }

    public String getEmail() {
        return email;
    }
}