package api.software.salehunter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class AccountSign extends AppCompatActivity {
    TextView title;
    ImageButton back;
    FrameLayout frameLayout;

    CallbackManager facebookCallbackManager;

    GoogleSignInClient googleSignInClient;

    boolean googleServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        setContentView(R.layout.activity_account_sign);

        overridePendingTransition(R.anim.lay_on,R.anim.null_anim);

        googleServices = googleServicesAvailable();

        //FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        facebookCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //check last sign in
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        title = findViewById(R.id.account_sign_title);
        back = findViewById(R.id.account_sign_back);
        frameLayout = findViewById(R.id.account_sign_frameLayout);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(),new SignInFragment())
                .commit();

        registerReceiver( new NetworkBroadcastReceiver(getSupportFragmentManager(), frameLayout), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String personName = account.getDisplayName();
                    String personGivenName = account.getGivenName();
                    String personFamilyName = account.getFamilyName();
                    String personEmail = account.getEmail();
                    String personId = account.getId();
                    Uri personPhoto = account.getPhotoUrl();

                    Toast.makeText(getApplicationContext(), "Name: "+personName+" "+personFamilyName+"\n Email: "+personEmail+"\n ID: "+personId+"\n pic:"+personPhoto.getPath(), Toast.LENGTH_LONG).show();
                    hello();
                }


            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            }

        }

        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void facebookAuth(){
        //signout users
        LoginManager.getInstance().logOut();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String picture = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            Toast.makeText(getApplicationContext(), "Name: "+first_name+" "+last_name+"\n Email: "+email+"\n ID: "+id+"\n pic:"+picture, Toast.LENGTH_LONG).show();
                            hello();

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

            }

            @Override
            public void onError(@NonNull FacebookException e) {

            }
        });

    }

    void googleAuth(){
        //logout
        googleSignInClient.signOut();

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    void twitterAuth(){
        Toast.makeText(getApplicationContext(), "Soon ...", Toast.LENGTH_SHORT).show();
    }

    void hello(){
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(false);
        loadingDialog.show(getSupportFragmentManager(), loadingDialog.getTag());

        new Handler().postDelayed(()-> {
                    loadingDialog.dismiss();
                    getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE)
                            .edit()
                            .putBoolean(MainActivity.SIGNED_IN, true)
                            .apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(MainActivity.REMEMBER_ME, true);

                    startActivity(intent);
                    onBackPressed();
                    },2000);

    }

    boolean googleServicesAvailable(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        return status == ConnectionResult.SUCCESS;
    }

}