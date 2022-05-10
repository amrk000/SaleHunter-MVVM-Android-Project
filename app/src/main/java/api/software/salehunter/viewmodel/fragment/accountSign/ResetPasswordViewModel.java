package api.software.salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ResetPasswordModel;
import retrofit2.Response;

public class ResetPasswordViewModel extends AndroidViewModel {
    private Repository repository;

    public ResetPasswordViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel>> resetPassword(String pin,String newPassword, String newPasswordConfirm){
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setPassword(newPassword);
        resetPasswordModel.setPasswordConfirm(newPasswordConfirm);

        return repository.resetPassword(pin,resetPasswordModel);
    }
}
