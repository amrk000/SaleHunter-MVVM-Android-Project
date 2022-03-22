package api.software.salehunter.data.remote;

import api.software.salehunter.model.ChangePasswordModel;
import api.software.salehunter.model.ResponseModel;
import api.software.salehunter.model.EmailVerificationModel;
import api.software.salehunter.model.ResetPasswordModel;
import api.software.salehunter.model.SignInModel;
import api.software.salehunter.model.SignUpModel;
import api.software.salehunter.model.SocialAuthModel;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.model.UserResponseModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {

    //Account Sign in/up & password reset
    @Headers({"client: mobile"})
    @POST("users/auth/signup")
    Call<UserResponseModel> signUp(@Body SignUpModel signUpModel);

    @Headers({"client: mobile"})
    @POST("users/auth/signin")
    Call<UserResponseModel> signIn(@Body SignInModel signInModel);

    @Headers({"client: mobile"})
    @GET("users/auth/google")
    Call<UserResponseModel> googleAuth(@Header("Jwt") String token);

    @Headers({"client: mobile"})
    @GET("users/auth/facebook")
    Call<UserResponseModel> facebookAuth(@Header("Jwt") String token);

    @Headers({"client: mobile"})
    @POST("users/verifyEmail")
    Call<ResponseModel> sendEmailVerification(@Body EmailVerificationModel emailVerificationModel);

    @Headers({"client: mobile"})
    @GET("users/verifyEmailToken/{resetToken}")
    Call<ResponseModel> verifyToken(@Path("resetToken") String resetToken);

    @Headers({"client: mobile"})
    @PATCH("users/auth/resetPassword/{resetToken}")
    Call<ResponseModel> resetPassword(@Path("resetToken") String pin, @Body ResetPasswordModel resetPasswordModel);

    //User Data Calls
    @Headers({"client: mobile"})
    @GET("users")
    Call<UserResponseModel> getUser(@Header("Authorization") String token);

    @Headers({"client: mobile"})
    @PATCH("users")
    Call<UserResponseModel> updateUser(@Header("Authorization") String token, @Body UserModel userModel);

    @Headers({"client: mobile"})
    @PATCH("users/updatePassword")
    Call<ResponseModel> changePassword(@Header("Authorization") String token, @Body ChangePasswordModel changePasswordModel);

    //Other calls
}
