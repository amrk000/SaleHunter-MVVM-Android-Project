package api.software.salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.EmailVerificationModel;
import retrofit2.Response;

public class VerificationCodeViewModel extends AndroidViewModel {
    private Repository repository;

    public VerificationCodeViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel>> verifyToken(String token){
        return repository.verifyToken(token);
    }

    public LiveData<Response<BaseResponseModel>> resendEmailVerification(String email){
        EmailVerificationModel emailVerificationModel = new EmailVerificationModel();
        emailVerificationModel.setEmail(email);
        return repository.sendEmailVerification(emailVerificationModel);
    }
}
