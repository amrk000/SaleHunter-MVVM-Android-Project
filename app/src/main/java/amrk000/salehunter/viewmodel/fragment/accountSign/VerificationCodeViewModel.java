package amrk000.salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import amrk000.salehunter.data.Repository;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.EmailVerificationModel;
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
