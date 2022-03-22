package api.software.salehunter.view.fragment.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.databinding.FragmentPasswordChangeDialogBinding;
import api.software.salehunter.model.ChangePasswordModel;
import api.software.salehunter.model.ResponseModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.util.UserAccountManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PasswordChangeDialog extends BottomSheetDialogFragment {
    private FragmentPasswordChangeDialogBinding vb;

    Repository repository;
    String token;

    public PasswordChangeDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentPasswordChangeDialogBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new Repository();
        token = UserAccountManager.getToken(getContext(),UserAccountManager.TOKEN_TYPE_BEARER);

        vb.passwordChangeDialogOldPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.passwordChangeDialogOldPassword.getEditText().getText().length()>0){
                    if(TextFieldValidator.isValidPassword(editable.toString())) vb.passwordChangeDialogOldPassword.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.passwordChangeDialogOldPassword.setError("Password Minimum length is 8");
                    else vb.passwordChangeDialogOldPassword.setError("Password must contain numbers, letters & symbols");
                }
                else vb.passwordChangeDialogOldPassword.setError(null);

            }
        });

        vb.passwordChangeDialogNewPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.passwordChangeDialogNewPassword.getEditText().getText().length()>0){
                    if(TextFieldValidator.isValidPassword(editable.toString())) vb.passwordChangeDialogNewPassword.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.passwordChangeDialogNewPassword.setError("Password Minimum length is 8");
                    else vb.passwordChangeDialogNewPassword.setError("Password must contain numbers, letters & symbols");
                }
                else vb.passwordChangeDialogNewPassword.setError(null);

                if(vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().toString().equals(vb.passwordChangeDialogNewPassword.getEditText().getText().toString())) vb.passwordChangeDialogNewPasswordConfirm.setError("Not matching the password");
                    else vb.passwordChangeDialogNewPasswordConfirm.setError(null);
                }
                else vb.passwordChangeDialogNewPasswordConfirm.setError(null);

            }
        });

        vb.passwordChangeDialogNewPasswordConfirm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().toString().equals(vb.passwordChangeDialogNewPassword.getEditText().getText().toString())) vb.passwordChangeDialogNewPasswordConfirm.setError("Not matching the password");
                    else vb.passwordChangeDialogNewPasswordConfirm.setError(null);
                }
                else vb.passwordChangeDialogNewPasswordConfirm.setError(null);

            }
        });

        vb.passwordChangeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validData = true;

                if(vb.passwordChangeDialogNewPasswordConfirm.getError()!=null || vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().length()==0){
                    vb.passwordChangeDialogNewPasswordConfirm.requestFocus();
                    vb.passwordChangeDialogNewPasswordConfirm.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.passwordChangeDialogNewPassword.getError()!=null || vb.passwordChangeDialogNewPassword.getEditText().getText().length()==0){
                    vb.passwordChangeDialogNewPassword.requestFocus();
                    vb.passwordChangeDialogNewPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.passwordChangeDialogOldPassword.getError()!=null || vb.passwordChangeDialogOldPassword.getEditText().getText().length()==0){
                    vb.passwordChangeDialogOldPassword.requestFocus();
                    vb.passwordChangeDialogOldPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(validData){

                    ChangePasswordModel changePasswordModel = new ChangePasswordModel();
                    changePasswordModel.setOldPassword(vb.passwordChangeDialogOldPassword.getEditText().getText().toString());
                    changePasswordModel.setNewPassword(vb.passwordChangeDialogNewPassword.getEditText().getText().toString());
                    changePasswordModel.setNewPasswordConfirm(vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().toString());

                    changePassword(changePasswordModel);

                }

            }
        });

    }


    void changePassword(ChangePasswordModel changePasswordModel){
        DialogsProvider.get(getActivity()).setLoading(true);

        repository.changePassword(token, changePasswordModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DialogsProvider.get(getActivity()).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        dismiss();
                        Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                        break;

                    case ResponseModel.FAILED_AUTH:
                        dismiss();
                        UserAccountManager.signOutForced(getActivity());

                        DialogsProvider.get(getActivity()).messageDialog("Invalid Token", "Please sign in again");
                        break;

                    default:
                        dismiss();

                        DialogsProvider.get(getActivity()).messageDialog("Wrong old Password", "Please try again");
                        break;

                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                DialogsProvider.get(getActivity()).setLoading(false);

                DialogsProvider.get(getActivity()).messageDialog("Password Change Failed", "Please check your connection");
            }
        });

    }

}