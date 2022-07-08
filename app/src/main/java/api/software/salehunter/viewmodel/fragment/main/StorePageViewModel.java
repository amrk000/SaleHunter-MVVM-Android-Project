package api.software.salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductPageModel;
import api.software.salehunter.model.ProductPageResponseModel;
import api.software.salehunter.model.ProductRateModel;
import api.software.salehunter.model.StorePageModel;
import api.software.salehunter.util.UserAccountManager;
import retrofit2.Response;

public class StorePageViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Response<StorePageModel>> storeData;

    private long storeId;
    private int page = 1;
    private String token;
    private StorePageModel storePageModel;

    public StorePageViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<StorePageModel>> getStore(){
        storeData = repository.getStore(token,storeId,1);
        return storeData;
    }

    public LiveData<Response<StorePageModel>> getNextPage(){
        page++;
        storeData = repository.getStore(token,storeId,page);
        return storeData;
    }

    public void removeObserverStoreData(LifecycleOwner lifecycleOwner){
        storeData.removeObservers(lifecycleOwner);
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public long getStoreId() {
        return storeId;
    }

    public StorePageModel getStorePageModel() {
        return storePageModel;
    }

    public void setStorePageModel(StorePageModel storePageModel) {
        this.storePageModel = storePageModel;
    }

    public LiveData<Response<BaseResponseModel>> addFavourite(long productId){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(long productId){
        return repository.removeFavourite(token,productId);
    }
}
