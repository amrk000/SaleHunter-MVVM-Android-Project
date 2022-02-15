package api.software.salehunter.data.remote;

import api.software.salehunter.model.ApiResponseModel;
import api.software.salehunter.model.ForgotPasswordModel;
import api.software.salehunter.model.ResetPasswordModel;
import api.software.salehunter.model.SignInModel;
import api.software.salehunter.model.SignUpModel;
import api.software.salehunter.model.SocialAuthModel;
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
    @POST("auth/signup")
    Call<ApiResponseModel> signUp(@Body SignUpModel signUpModel);

    @Headers({"client: mobile"})
    @POST("auth/signin")
    Call<ApiResponseModel> signIn(@Body SignInModel signInModel);

    @Headers({"client: mobile"})
    @POST("auth/thirdparty")
    Call<ApiResponseModel> socialAuth(@Body SocialAuthModel socialAuthModel);

    @Headers({"client: mobile"})
    @POST("auth/forgetPassword")
    Call<ApiResponseModel> forgetPassword(@Body ForgotPasswordModel forgotPasswordModel);

    @Headers({"client: mobile"})
    @GET("auth/verifyResetToken/{resetToken}")
    Call<ApiResponseModel> verifyToken(@Path("resetToken") String resetToken);

    @Headers({"client: mobile"})
    @PATCH("auth/resetPassword/{resetToken}")
    Call<ApiResponseModel> resetPassword(@Body ResetPasswordModel resetPasswordModel, @Path("resetToken") String resetToken);

    //Other calls
}
