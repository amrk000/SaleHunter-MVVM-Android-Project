package api.software.salehunter.data;

import api.software.salehunter.data.remote.RetrofitClient;
import api.software.salehunter.data.remote.RetrofitInterface;
import api.software.salehunter.model.ApiResponseModel;
import api.software.salehunter.model.ForgotPasswordModel;
import api.software.salehunter.model.ResetPasswordModel;
import api.software.salehunter.model.SignInModel;
import api.software.salehunter.model.SignUpModel;
import api.software.salehunter.model.SocialAuthModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountRepository {
    private Retrofit retrofit;

    public AccountRepository() {
        retrofit = RetrofitClient.getInstance();
    }

    public void signUp(SignUpModel signUpModel, Callback<ApiResponseModel> callback){
        retrofit.create(RetrofitInterface.class).signUp(signUpModel).enqueue(callback);
    }

    public void signIn(SignInModel signInModel, Callback<ApiResponseModel> callback){
        retrofit.create(RetrofitInterface.class).signIn(signInModel).enqueue(callback);
    }

    public void socialAuth(SocialAuthModel socialAuthModel, Callback<ApiResponseModel> callback){
        retrofit.create(RetrofitInterface.class).socialAuth(socialAuthModel).enqueue(callback);
    }

    public void forgotPassword(ForgotPasswordModel forgotPasswordModel, Callback<ApiResponseModel> callback){
        retrofit.create(RetrofitInterface.class).forgetPassword(forgotPasswordModel).enqueue(callback);
    }

    public void verifyToken(String token, Callback<ApiResponseModel> callback){
        retrofit.create(RetrofitInterface.class).verifyToken(token).enqueue(callback);
    }

    public void resetPassword(ResetPasswordModel resetPasswordModel,String token, Callback<ApiResponseModel> callback){
        retrofit.create(RetrofitInterface.class).resetPassword(resetPasswordModel, token).enqueue(callback);
    }

}
