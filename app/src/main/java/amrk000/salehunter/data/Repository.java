package amrk000.salehunter.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import amrk000.salehunter.data.remote.RetrofitClient;
import amrk000.salehunter.data.remote.RetrofitInterface;
import amrk000.salehunter.model.BarcodeMonsterResponseModel;
import amrk000.salehunter.model.ChangePasswordModel;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.CreateProductRequestModel;
import amrk000.salehunter.model.CreateStoreRequestModel;
import amrk000.salehunter.model.CreateStoreResponseModel;
import amrk000.salehunter.model.EmailVerificationModel;
import amrk000.salehunter.model.FacebookSocialAuthModel;
import amrk000.salehunter.model.ProductPageResponseModel;
import amrk000.salehunter.model.ProductRateModel;
import amrk000.salehunter.model.ProductsResponseModel;
import amrk000.salehunter.model.ResetPasswordModel;
import amrk000.salehunter.model.SignInModel;
import amrk000.salehunter.model.SignUpModel;
import amrk000.salehunter.model.GoogleSocialAuthModel;
import amrk000.salehunter.model.SocialAuthResponseModel;
import amrk000.salehunter.model.StorePageModel;
import amrk000.salehunter.model.UpcItemDbResponseModel;
import amrk000.salehunter.model.UserModel;
import amrk000.salehunter.model.UserResponseModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Repository {
    private Retrofit mainClient;
    private Retrofit upcItemDbClient;
    private Retrofit barcodeMonsterClient;

    //HEADERS
    public static final String AUTH_TOKEN_HEADER = "Authorization";

    public Repository() {
        mainClient = RetrofitClient.getMainInstance();
        upcItemDbClient = RetrofitClient.getUpcItemDbInstance();
        barcodeMonsterClient = RetrofitClient.getBarcodeMonsterInstance();
    }

    //Account Sign in/up & password reset
    public LiveData<Response<UserResponseModel>> signIn(SignInModel signInModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .signIn(signInModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<UserResponseModel>> signUp(SignUpModel signUpModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .signUp(signUpModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<SocialAuthResponseModel>> googleAuth(GoogleSocialAuthModel socialAuthModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .googleAuth(socialAuthModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<SocialAuthResponseModel>> facebookAuth(FacebookSocialAuthModel socialAuthModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .facebookAuth(socialAuthModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<BaseResponseModel>> sendEmailVerification(EmailVerificationModel emailVerificationModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .sendEmailVerification(emailVerificationModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> verifyToken(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .verifyToken(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> resetPassword(String pin, ResetPasswordModel resetPasswordModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .resetPassword(pin,resetPasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //User Data Calls
    public LiveData<Response<UserResponseModel>> getUser(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getUser(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<UserResponseModel>> updateUser(String token, UserModel userModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateUser(token, userModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> changePassword(String token, ChangePasswordModel changePasswordModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .changePassword(token, changePasswordModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductsResponseModel>> searchProducts(String token,String language, double userLat, double userLong, String keyword, String storeType, String sort, long minPrice, long maxPrice, String category, String brand, long cursorLastItemId, int limit){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .searchProducts(token,language,userLat,userLong,keyword,storeType,sort,minPrice,maxPrice,category,brand,cursorLastItemId,limit)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb(String barcode){
        return LiveDataReactiveStreams.fromPublisher(
                upcItemDbClient.create(RetrofitInterface.class)
                        .barcodeLookupUpcItemDb(barcode)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BarcodeMonsterResponseModel>> barcodeLookupBarcodeMonster(String barcode){
        return LiveDataReactiveStreams.fromPublisher(
                barcodeMonsterClient.create(RetrofitInterface.class)
                        .barcodeLookupBarcodeMonster(barcode)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductPageResponseModel>> getProduct(String token, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getProduct(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> addFavourite(String token, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .addFavourite(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(String token, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .removeFavourite(token, productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> rateProduct(String token, long productId, ProductRateModel productRateModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .rateProduct(token, productId, productRateModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<ProductsResponseModel>> getRecommendedProducts(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getRecommendedProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<ProductsResponseModel>> getProductsViewsHistory(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getProductsViewsHistory(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<ProductsResponseModel>> getFavoriteProducts(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getFavoriteProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<ProductsResponseModel>> getOnSaleProducts(String token){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getOnSaleProducts(token)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    //Store Calls
    public LiveData<Response<StorePageModel>> getStore(String token, long storeId, int page){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .getStore(token,storeId,page)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );

    }

    public LiveData<Response<CreateStoreResponseModel>> createStore(String token,CreateStoreRequestModel createStoreRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .createStore(token,createStoreRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> updateStore(String token, long storeId,CreateStoreRequestModel createStoreRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateStore(token, storeId,createStoreRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    //Dashboard Calls
    public LiveData<Response<BaseResponseModel>> createProduct(String token, long storeId,CreateProductRequestModel createProductRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .createProduct(token,storeId,createProductRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> updateProduct(String token, long storeId, long productId,CreateProductRequestModel createProductRequestModel){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .updateProduct(token,storeId,productId,createProductRequestModel)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<Response<BaseResponseModel>> deleteProduct(String token, long storeId, long productId){
        return LiveDataReactiveStreams.fromPublisher(
                mainClient.create(RetrofitInterface.class)
                        .deleteProduct(token,storeId,productId)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn( exception -> {
                            exception.printStackTrace();

                            if(exception.getClass() == HttpException.class)
                                return Response.error(((HttpException)exception).code(), ResponseBody.create(null,""));

                            return Response.error(BaseResponseModel.FAILED_REQUEST_FAILURE, ResponseBody.create(null,""));
                        })
                        .toFlowable(BackpressureStrategy.LATEST)
        );
    }
}
