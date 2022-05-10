package api.software.salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.model.UserResponseModel;
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
