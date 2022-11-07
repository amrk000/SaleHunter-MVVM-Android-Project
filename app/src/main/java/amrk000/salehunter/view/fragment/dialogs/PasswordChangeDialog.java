package amrk000.salehunter.view.fragment.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import amrk000.salehunter.R;
import amrk000.salehunter.databinding.FragmentPasswordChangeDialogBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.util.TextFieldValidator;
import amrk000.salehunter.util.UserAccountManager;
import amrk000.salehunter.viewmodel.fragment.dialogs.PasswordChangeDialogViewModel;


public class PasswordChangeDialog extends BottomSheetDialogFragment {
    private FragmentPasswordChangeDialogBinding vb;
    private PasswordChangeDialogViewModel viewModel;

    private String token;

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

        viewModel = new ViewModelProvider(this).get(PasswordChangeDialogViewModel.class);

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
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.passwordChangeDialogOldPassword.setError(getString(R.string.Password_Minimum_length_is) + " "+ TextFieldValidator.PASSWORD_MIN);
                    else vb.passwordChangeDialogOldPassword.setError(getString(R.string.Password_must_contain));
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
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.passwordChangeDialogOldPassword.setError(getString(R.string.Password_Minimum_length_is) + " "+ TextFieldValidator.PASSWORD_MIN);
                    else vb.passwordChangeDialogOldPassword.setError(getString(R.string.Password_must_contain));
                }
                else vb.passwordChangeDialogNewPassword.setError(null);

                if(vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().toString().equals(vb.passwordChangeDialogNewPassword.getEditText().getText().toString())) vb.passwordChangeDialogNewPasswordConfirm.setError(getString(R.string.Not_matching_the_password));
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
                    if(!vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().toString().equals(vb.passwordChangeDialogNewPassword.getEditText().getText().toString())) vb.passwordChangeDialogNewPasswordConfirm.setError(getString(R.string.Not_matching_the_password));
                    else vb.passwordChangeDialogNewPasswordConfirm.setError(null);
                }
                else vb.passwordChangeDialogNewPasswordConfirm.setError(null);

            }
        });

        vb.passwordChangeDialogButton.setOnClickListener(button -> {
            if(isDataValid()) changePassword();
        });

    }

    boolean isDataValid(){
        boolean validData = true;

        if(vb.passwordChangeDialogNewPasswordConfirm.getError()!=null || vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().length()==0){
            vb.passwordChangeDialogNewPasswordConfirm.requestFocus();
            vb.passwordChangeDialogNewPasswordConfirm.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        if(vb.passwordChangeDialogNewPassword.getError()!=null || vb.passwordChangeDialogNewPassword.getEditText().getText().length()==0){
            vb.passwordChangeDialogNewPassword.requestFocus();
            vb.passwordChangeDialogNewPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        if(vb.passwordChangeDialogOldPassword.getError()!=null || vb.passwordChangeDialogOldPassword.getEditText().getText().length()==0){
            vb.passwordChangeDialogOldPassword.requestFocus();
            vb.passwordChangeDialogOldPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void changePassword(){
        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.changePassword(
                token,
                vb.passwordChangeDialogOldPassword.getEditText().getText().toString(),
                vb.passwordChangeDialogNewPassword.getEditText().getText().toString(),
                vb.passwordChangeDialogNewPasswordConfirm.getEditText().getText().toString()
        ).observe(getViewLifecycleOwner(), response -> {

            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    dismiss();
                    Toast.makeText(getContext(), getString(R.string.Password_Updated), Toast.LENGTH_SHORT).show();
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    dismiss();
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Wrong_Old_Password), getString(R.string.Please_try_again));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    dismiss();
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
                    break;

            }

        });

    }

}