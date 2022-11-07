package amrk000.salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import amrk000.salehunter.data.Repository;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.CreateProductRequestModel;
import amrk000.salehunter.util.UserAccountManager;
import retrofit2.Response;

public class CreateProductViewModel extends AndroidViewModel {
    private Repository repository;
    private String token;
    private LiveData<Response<BaseResponseModel>> createProductObserver;
    private LiveData<Response<BaseResponseModel>> updateProductObserver;
    private LiveData<Response<BaseResponseModel>> deleteProductObserver;

    public CreateProductViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<BaseResponseModel>> createProduct(long storeId, String name, String nameArabic, Double price, int salePercent, String category, String categoryArabic, String brand, String description, String descriptionArabic, ArrayList<String> images){
        CreateProductRequestModel createProductRequestModel = new CreateProductRequestModel();
        createProductRequestModel.setName(name);
        createProductRequestModel.setNameArabic(nameArabic);
        createProductRequestModel.setPrice(price);
        createProductRequestModel.setSale(salePercent);
        createProductRequestModel.setCategory(category);
        createProductRequestModel.setCategoryArabic(categoryArabic);
        createProductRequestModel.setBrand(brand);
        createProductRequestModel.setDescription(description);
        createProductRequestModel.setDescriptionArabic(descriptionArabic);
        createProductRequestModel.setImages(images);

        createProductObserver = repository.createProduct(token,storeId,createProductRequestModel);
        return createProductObserver;
    }

    public void removeObserverCreateProduct(LifecycleOwner lifecycleOwner){
        createProductObserver.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel>> updateProduct(long storeId, long productId, String name, String nameArabic, Double price, int salePercent, String description, String descriptionArabic){
        CreateProductRequestModel createProductRequestModel = new CreateProductRequestModel();
        createProductRequestModel.setName(name);
        createProductRequestModel.setNameArabic(nameArabic);
        createProductRequestModel.setPrice(price);
        createProductRequestModel.setSale(salePercent);
        createProductRequestModel.setDescription(description);
        createProductRequestModel.setDescriptionArabic(descriptionArabic);

        updateProductObserver = repository.updateProduct(token, storeId, productId, createProductRequestModel);
        return updateProductObserver;
    }

    public void removeObserverUpdateProduct(LifecycleOwner lifecycleOwner){
        updateProductObserver.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel>> deleteProduct(long storeId, long productId){
        deleteProductObserver = repository.deleteProduct(token, storeId, productId);
        return deleteProductObserver;
    }

    public void removeObserverDeleteProduct(LifecycleOwner lifecycleOwner){
        deleteProductObserver.removeObservers(lifecycleOwner);
    }

}
