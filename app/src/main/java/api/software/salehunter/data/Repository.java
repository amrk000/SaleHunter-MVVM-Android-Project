package api.software.salehunter.data;

import api.software.salehunter.data.remote.RetrofitClient;
import api.software.salehunter.data.remote.RetrofitInterface;
import api.software.salehunter.model.ChangePasswordModel;
import api.software.salehunter.model.ResponseModel;
import api.software.salehunter.model.EmailVerificationModel;
import api.software.salehunter.model.ResetPasswordModel;
import api.software.salehunter.model.SignInModel;
import api.software.salehunter.model.SignUpModel;
import api.software.salehunter.model.SocialAuthModel;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.model.UserResponseModel;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Repository {
    private Retrofit retrofit;

    public Repository() {
        retrofit = RetrofitClient.getInstance();
    }

    //Account Sign in/up & password reset
    public void signUp(SignUpModel signUpModel, Callback<UserResponseModel> callback){
        retrofit.create(RetrofitInterface.class).signUp(signUpModel).enqueue(callback);
    }

    public void signIn(SignInModel signInModel, Callback<UserResponseModel> callback){
        retrofit.create(RetrofitInterface.class).signIn(signInModel).enqueue(callback);
    }

    public void googleAuth(String token, Callback<UserResponseModel> callback){
        retrofit.create(RetrofitInterface.class).googleAuth(token).enqueue(callback);
    }

    public void facebookAuth(String token, Callback<UserResponseModel> callback){
        retrofit.create(RetrofitInterface.class).facebookAuth(token).enqueue(callback);
    }

    public void sendEmailVerification(EmailVerificationModel emailVerificationModel, Callback<ResponseModel> callback){
        retrofit.create(RetrofitInterface.class).sendEmailVerification(emailVerificationModel).enqueue(callback);
    }

    public void verifyToken(String token, Callback<ResponseModel> callback){
        retrofit.create(RetrofitInterface.class).verifyToken(token).enqueue(callback);
    }

    public void resetPassword(String pin, ResetPasswordModel resetPasswordModel, Callback<ResponseModel> callback){
        retrofit.create(RetrofitInterface.class).resetPassword(pin, resetPasswordModel).enqueue(callback);
    }

    //User Data Calls
    public void getUser(String token, Callback<UserResponseModel> callback){
        retrofit.create(RetrofitInterface.class).getUser(token).enqueue(callback);
    }

    public void updateUser(String token, UserModel userModel, Callback<UserResponseModel> callback){
        retrofit.create(RetrofitInterface.class).updateUser(token, userModel).enqueue(callback);
    }

    public void changePassword(String token, ChangePasswordModel changePasswordModel, Callback<ResponseModel> callback){
        retrofit.create(RetrofitInterface.class).changePassword(token, changePasswordModel).enqueue(callback);
    }
}
