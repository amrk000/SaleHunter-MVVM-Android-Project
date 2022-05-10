package api.software.salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.EmailVerificationModel;
import api.software.salehunter.model.BaseResponseModel;
import retrofit2.Response;

public class ForgotPasswordViewModel extends AndroidViewModel {
    private Repository repository;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel>> sendEmailVerification(String email){
        EmailVerificationModel emailVerificationModel = new EmailVerificationModel();
        emailVerificationModel.setEmail(email);
        return repository.sendEmailVerification(emailVerificationModel);
    }

}
