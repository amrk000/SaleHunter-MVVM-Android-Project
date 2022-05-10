package api.software.salehunter.viewmodel.fragment.dialogs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ChangePasswordModel;
import api.software.salehunter.model.UserResponseModel;
import retrofit2.Response;

public class PasswordChangeDialogViewModel extends AndroidViewModel {
    private Repository repository;

    public PasswordChangeDialogViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

    public LiveData<Response<BaseResponseModel>> changePassword(String token, String oldPassword, String newPassword, String newPasswordConfirm){
        ChangePasswordModel changePasswordModel = new ChangePasswordModel();
        changePasswordModel.setOldPassword(oldPassword);
        changePasswordModel.setNewPassword(newPassword);
        changePasswordModel.setNewPasswordConfirm(newPasswordConfirm);

        return repository.changePassword(token, changePasswordModel);
    }
}
