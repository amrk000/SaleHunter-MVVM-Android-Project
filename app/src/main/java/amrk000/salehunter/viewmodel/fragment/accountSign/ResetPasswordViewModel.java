package amrk000.salehunter.viewmodel.fragment.accountSign;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import amrk000.salehunter.data.Repository;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.ResetPasswordModel;
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
