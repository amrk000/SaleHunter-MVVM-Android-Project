package api.software.salehunter.viewmodel.fragment.accountSign;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.model.FacebookSocialAuthModel;
import api.software.salehunter.model.SignInModel;
import api.software.salehunter.model.GoogleSocialAuthModel;
import api.software.salehunter.model.SocialAuthResponseModel;
import api.software.salehunter.model.UserResponseModel;
import retrofit2.Response;

public class SignInViewModel extends AndroidViewModel {
    private Repository repository;
    private CallbackManager facebookCallbackManager;
    private GoogleSignInClient googleSignInClient;

    private GoogleSocialAuthModel googleSocialAuthModel;
    private FacebookSocialAuthModel facebookSocialAuthModel;

    public SignInViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        AppEventsLogger.activateApp(getApplication());
        facebookCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(application.getString(R.string.google_backend_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(application, googleSignInOptions);
    }

    public LiveData<Response<UserResponseModel>> signIn(String email, String password){
        SignInModel signInModel = new SignInModel();
        signInModel.setEmail(email);
        signInModel.setPassword(password);

        return repository.signIn(signInModel);
    }

    public void googleAuth(Fragment fragment){
        if(GoogleSignIn.getLastSignedInAccount(getApplication()) != null) googleSignInClient.signOut();
        fragment.startActivityForResult(googleSignInClient.getSignInIntent(),1);
    }

    public MutableLiveData<Response<SocialAuthResponseModel>> googleAuthOnActivityResult(Intent data, Fragment fragment){

        MutableLiveData<Response<SocialAuthResponseModel>> backendResponse = new MutableLiveData<>();

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        if (task.isSuccessful()) {

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                googleSocialAuthModel = new GoogleSocialAuthModel();
                googleSocialAuthModel.setClientId(account.getId());
                googleSocialAuthModel.setEmail(account.getEmail());
                googleSocialAuthModel.setFullName(account.getGivenName()+" "+account.getFamilyName());
                googleSocialAuthModel.setImage(account.getPhotoUrl().toString().replace("s96-c", "s500-c"));

                repository.googleAuth(googleSocialAuthModel).observe(fragment.getViewLifecycleOwner(), backendResponse::setValue);

            } catch (ApiException e) { e.printStackTrace(); }

        }

        return backendResponse;
    }

    public MutableLiveData<Response<SocialAuthResponseModel>> facebookAuth(Fragment fragment){

        MutableLiveData<Response<SocialAuthResponseModel>> backendResponse = new MutableLiveData<>();

        if(AccessToken.getCurrentAccessToken()!=null) LoginManager.getInstance().logOut();

        LoginManager.getInstance().logInWithReadPermissions(fragment,facebookCallbackManager, Arrays.asList("email", "public_profile"));

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

                            facebookSocialAuthModel = new FacebookSocialAuthModel();
                            facebookSocialAuthModel.setClientId(id);
                            facebookSocialAuthModel.setEmail(email);
                            facebookSocialAuthModel.setFullName(first_name+" "+last_name);
                            facebookSocialAuthModel.setImage(picture);

                            repository.facebookAuth(facebookSocialAuthModel).observe(fragment.getViewLifecycleOwner(), backendResponse::setValue);

                        } catch (JSONException e) { e.printStackTrace();}

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,last_name,email,id,picture.type(large)");
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

        return backendResponse;
    }

    public boolean googleServicesNotAvailable(){
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplication()) != ConnectionResult.SUCCESS;
    }

    public GoogleSocialAuthModel getGoogleSocialAuthModel() {
        return googleSocialAuthModel;
    }

    public FacebookSocialAuthModel getFacebookSocialAuthModel() {
        return facebookSocialAuthModel;
    }
}
