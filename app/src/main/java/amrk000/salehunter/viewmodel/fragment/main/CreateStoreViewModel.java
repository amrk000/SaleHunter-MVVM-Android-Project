package amrk000.salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import amrk000.salehunter.data.Repository;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.CreateStoreRequestModel;
import amrk000.salehunter.model.CreateStoreResponseModel;
import amrk000.salehunter.util.UserAccountManager;
import retrofit2.Response;

public class CreateStoreViewModel extends AndroidViewModel {
    private Repository repository;
    private String token;
    private LiveData<Response<CreateStoreResponseModel>> createStoreObserver;
    private LiveData<Response<BaseResponseModel>> updateStoreObserver;

    public CreateStoreViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<CreateStoreResponseModel>> createStore(String logoBase64, String name , String category, String address, Double latitude, Double longitude, String description, String phone, boolean hasWhatsapp, String websiteLink, String facebookUsername, String InstagramUsername){
        CreateStoreRequestModel createStoreRequestModel = new CreateStoreRequestModel();
        createStoreRequestModel.setLogoBase64(logoBase64);
        createStoreRequestModel.setName(name);
        createStoreRequestModel.setCategory(category);
        createStoreRequestModel.setAddress(address);
        createStoreRequestModel.setLatitude(latitude);
        createStoreRequestModel.setLongitude(longitude);
        createStoreRequestModel.setDescription(description);
        if(phone.length()!=0) createStoreRequestModel.setPhone(phone);
        if(hasWhatsapp) createStoreRequestModel.setWhatsappPhoneNumber(phone);
        if(websiteLink.length()!=0) createStoreRequestModel.setWebsiteLink(websiteLink);
        if(facebookUsername.length()!=0) createStoreRequestModel.setFacebookLink("https://www.facebook.com/"+facebookUsername+"/");
        if(InstagramUsername.length()!=0) createStoreRequestModel.setInstagramLink("https://www.instagram.com/"+InstagramUsername+"/");

        createStoreObserver = repository.createStore(token,createStoreRequestModel);
        return createStoreObserver;
    }

    public void removeObserverCreateStore(LifecycleOwner lifecycleOwner){
        createStoreObserver.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel>> updateStore(long storeId,String logoBase64, String name , String category, String address, Double latitude, Double longitude, String description, String phone, boolean hasWhatsapp, String websiteLink, String facebookUsername, String InstagramUsername){
        CreateStoreRequestModel createStoreRequestModel = new CreateStoreRequestModel();
        createStoreRequestModel.setLogoBase64(logoBase64);
        createStoreRequestModel.setName(name);
        createStoreRequestModel.setCategory(category);
        createStoreRequestModel.setAddress(address);
        createStoreRequestModel.setLatitude(latitude);
        createStoreRequestModel.setLongitude(longitude);
        createStoreRequestModel.setDescription(description);
        if(phone.length()!=0) createStoreRequestModel.setPhone(phone);
        if(hasWhatsapp) createStoreRequestModel.setWhatsappPhoneNumber(phone);
        if(websiteLink.length()!=0) createStoreRequestModel.setWebsiteLink(websiteLink);
        if(facebookUsername.length()!=0) createStoreRequestModel.setFacebookLink("https://www.facebook.com/"+facebookUsername+"/");
        if(InstagramUsername.length()!=0) createStoreRequestModel.setInstagramLink("https://www.instagram.com/"+InstagramUsername+"/");

        updateStoreObserver = repository.updateStore(token,storeId,createStoreRequestModel);
        return updateStoreObserver;
    }

    public void removeObserverUpdateStore(LifecycleOwner lifecycleOwner){
        updateStoreObserver.removeObservers(lifecycleOwner);
    }

}
