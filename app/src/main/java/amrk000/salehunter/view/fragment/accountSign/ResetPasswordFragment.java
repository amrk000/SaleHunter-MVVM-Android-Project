package amrk000.salehunter.view.fragment.accountSign;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import amrk000.salehunter.databinding.FragmentResetPasswordBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.util.TextFieldValidator;
import amrk000.salehunter.view.activity.AccountSign;
import amrk000.salehunter.R;
import amrk000.salehunter.viewmodel.fragment.accountSign.ResetPasswordViewModel;

public class ResetPasswordFragment extends Fragment {
    private FragmentResetPasswordBinding vb;
    private ResetPasswordViewModel viewModel;
    private NavController navController;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentResetPasswordBinding.inflate(inflater,container,false);
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

        ((AccountSign)getActivity()).setTitle(getString(R.string.Reset_Password));

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        vb.resetPasswordPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.resetPasswordPassword.getEditText().getText().length()>0){
                    if(TextFieldValidator.isValidPassword(editable.toString())) vb.resetPasswordPassword.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.resetPasswordPassword.setError(getString(R.string.Password_Minimum_length_is) + " "+ TextFieldValidator.PASSWORD_MIN);
                    else vb.resetPasswordPassword.setError(getString(R.string.Password_must_contain));
                }
                else vb.resetPasswordPassword.setError(null);

                if(vb.resetPasswordPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.resetPasswordPasswordConfirm.getEditText().getText().toString().equals(vb.resetPasswordPassword.getEditText().getText().toString())) vb.resetPasswordPasswordConfirm.setError(getString(R.string.Not_matching_the_password));
                    else vb.resetPasswordPasswordConfirm.setError(null);
                }
                else vb.resetPasswordPasswordConfirm.setError(null);

            }
        });

        vb.resetPasswordPasswordConfirm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.resetPasswordPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.resetPasswordPasswordConfirm.getEditText().getText().toString().equals(vb.resetPasswordPassword.getEditText().getText().toString())) vb.resetPasswordPasswordConfirm.setError(getString(R.string.Not_matching_the_password));
                    else vb.resetPasswordPasswordConfirm.setError(null);
                }
                else vb.resetPasswordPasswordConfirm.setError(null);

            }
        });

        vb.resetPasswordButton.setOnClickListener(button -> {
            if(isDataValid()) resetPassword();
        });

    }

    boolean isDataValid(){
        boolean validData = true;

        if(vb.resetPasswordPasswordConfirm.getError()!=null || vb.resetPasswordPasswordConfirm.getEditText().getText().length()==0){
            vb.resetPasswordPasswordConfirm.requestFocus();
            vb.resetPasswordPasswordConfirm.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        if(vb.resetPasswordPassword.getError()!=null || vb.resetPasswordPassword.getEditText().getText().length()==0){
            vb.resetPasswordPassword.requestFocus();
            vb.resetPasswordPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }
        return validData;
    }

    void resetPassword(){
        DialogsProvider.get(getActivity()).setLoading(true);

        String pin="";
        if(getArguments()!=null) pin = getArguments().getString("pin");

        viewModel.resetPassword(
                pin,
                vb.resetPasswordPassword.getEditText().getText().toString(),
                vb.resetPasswordPasswordConfirm.getEditText().getText().toString()
        ).observe(getViewLifecycleOwner(), response ->{
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                //valid
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    navController.navigate(R.id.action_resetPasswordFragment_to_signInFragment);
                    break;

                //expired
                case BaseResponseModel.FAILED_INVALID_DATA:
                    navController.navigate(R.id.action_resetPasswordFragment_to_signInFragment);
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Pin_is_expired), getString(R.string.Please_try_password_reset_again));
                    break;

                //invalid
                case BaseResponseModel.FAILED_NOT_FOUND:
                    navController.navigate(R.id.action_resetPasswordFragment_to_signInFragment);
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Pin_is_invalid), getString(R.string.Please_try_password_reset_again));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

        });

    }

}