package api.software.salehunter.viewmodel.fragment.main.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductsResponseModel;
import api.software.salehunter.util.UserAccountManager;
import retrofit2.Response;

public class FavViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Response<ProductsResponseModel>> products;
    private String token;

    public FavViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<ProductsResponseModel>> getFavoriteProducts(){
        products = repository.getFavoriteProducts(token);
        return products;
    }

    public void removeObserverOfProducts(LifecycleOwner lifecycleOwner){
        products.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(long productId){
        return repository.removeFavourite(token,productId);
    }
}
