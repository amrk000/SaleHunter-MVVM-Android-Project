package api.software.salehunter.viewmodel.fragment.main;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductPageModel;
import api.software.salehunter.model.ProductPageResponseModel;
import api.software.salehunter.model.ProductRateModel;
import api.software.salehunter.util.UserAccountManager;
import retrofit2.Response;

public class ProductPageViewModel extends AndroidViewModel {
    private Repository repository;

    private long productId;
    private String token;
    private ProductPageModel productPageModel;

    public ProductPageViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<ProductPageResponseModel>> getProduct(){
        return repository.getProduct(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> addFavourite(){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(){
        return repository.removeFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> rateProduct(int rating){
        ProductRateModel productRateModel = new ProductRateModel();
        productRateModel.setRating(rating);
        return repository.rateProduct(token,productId,productRateModel);
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public ProductPageModel getProductPageModel() {
        return productPageModel;
    }

    public void setProductPageModel(ProductPageModel productPageModel) {
        this.productPageModel = productPageModel;
    }
}
