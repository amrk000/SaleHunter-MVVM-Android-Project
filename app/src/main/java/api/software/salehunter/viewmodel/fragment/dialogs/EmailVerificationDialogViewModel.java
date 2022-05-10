package api.software.salehunter.viewmodel.fragment.dialogs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.EmailVerificationModel;
import retrofit2.Response;

public class EmailVerificationDialogViewModel extends AndroidViewModel {
    private Repository repository;

    public EmailVerificationDialogViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel>> verifyToken(String token){
        return repository.verifyToken(token);
    }

    public LiveData<Response<BaseResponseModel>> sendEmailVerification(String email){
        EmailVerificationModel emailVerificationModel = new EmailVerificationModel();
        emailVerificationModel.setEmail(email);
        return repository.sendEmailVerification(emailVerificationModel);
    }
}
