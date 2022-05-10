package api.software.salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.SignUpModel;
import api.software.salehunter.model.UserResponseModel;
import retrofit2.Response;

public class SignUpViewModel extends AndroidViewModel {
    private Repository repository;

    public SignUpViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

    public LiveData<Response<UserResponseModel>> signUp(String name , String email, String password, String passwordConfirm, String image){
        SignUpModel signUpModel = new SignUpModel();
        signUpModel.setFullName(name);
        signUpModel.setEmail(email);
        signUpModel.setPassword(password);
        signUpModel.setPasswordConfirm(passwordConfirm);
        signUpModel.setProfileImage(image);

        return repository.signUp(signUpModel);
    }

}
