package amrk000.salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import amrk000.salehunter.data.Repository;
import amrk000.salehunter.model.UserModel;
import amrk000.salehunter.model.UserResponseModel;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private Repository repository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

    public LiveData<Response<UserResponseModel>> updateUser(String token, UserModel userModel){
        return repository.updateUser(token, userModel);
    }
}
