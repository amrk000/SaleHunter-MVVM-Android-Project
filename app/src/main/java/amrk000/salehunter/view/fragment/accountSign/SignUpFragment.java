package amrk000.salehunter.view.fragment.accountSign;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import amrk000.salehunter.data.Repository;
import amrk000.salehunter.databinding.FragmentSignUpBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.util.TextFieldValidator;
import amrk000.salehunter.util.UserAccountManager;
import amrk000.salehunter.view.activity.AccountSign;
import amrk000.salehunter.R;
import amrk000.salehunter.view.activity.MainActivity;
import amrk000.salehunter.viewmodel.fragment.accountSign.SignUpViewModel;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding vb;
    private SignUpViewModel viewModel;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentSignUpBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!((AccountSign) getActivity()).isBackButtonVisible()) {
            ((AccountSign) getActivity()).setTitle(getString(R.string.Sign_Up));
            ((AccountSign) getActivity()).setBackButton(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        vb.signUpUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length()>0){
                    if(TextFieldValidator.isValidName(editable.toString())) vb.signUpUsername.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.USERNAME_MIN,TextFieldValidator.USERNAME_MAX)) vb.signUpUsername.setError(getString(R.string.Name_is_too_short));
                    else vb.signUpUsername.setError(getString(R.string.Not_valid_name));
                }
                else vb.signUpUsername.setError(null);

            }
        });

        vb.signUpEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length()>0){

                    if(TextFieldValidator.isValidEmail(editable.toString())) vb.signUpEmail.setError(null);
                    else vb.signUpEmail.setError(getString(R.string.Email_not_complete_or_not_valid));

                }
                else vb.signUpEmail.setError(null);

            }
        });

        vb.signUpPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length()>0){
                    if(TextFieldValidator.isValidPassword(editable.toString())) vb.signUpPassword.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.signUpPassword.setError(getString(R.string.Password_Minimum_length_is) +  " "+ TextFieldValidator.PASSWORD_MIN);
                    else vb.signUpPassword.setError(getString(R.string.Password_must_contain));
                }
                else vb.signUpPassword.setError(null);

                if(vb.signUpPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.signUpPasswordConfirm.getEditText().getText().toString().equals(editable.toString())) vb.signUpPasswordConfirm.setError(getString(R.string.Not_matching_the_password));
                    else vb.signUpPasswordConfirm.setError(null);
                }
                else vb.signUpPasswordConfirm.setError(null);

            }
        });

        vb.signUpPasswordConfirm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length()>0){
                    if(!editable.toString().equals(vb.signUpPassword.getEditText().getText().toString())) vb.signUpPasswordConfirm.setError(getString(R.string.Not_matching_the_password));
                    else vb.signUpPasswordConfirm.setError(null);
                }
                else vb.signUpPasswordConfirm.setError(null);

            }
        });

        vb.signUpButton.setOnClickListener(button -> {
                if(isDataValid()) signUp();
        });
    }

    boolean isDataValid(){
        boolean validData = true;

        if(vb.signUpPasswordConfirm.getError()!=null || vb.signUpPasswordConfirm.getEditText().getText().length()==0){
            vb.signUpPasswordConfirm.requestFocus();
            vb.signUpPasswordConfirm.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        if(vb.signUpPassword.getError()!=null || vb.signUpPassword.getEditText().getText().length()==0){
            vb.signUpPassword.requestFocus();
            vb.signUpPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        if(vb.signUpEmail.getError()!=null || vb.signUpEmail.getEditText().getText().length()==0){
            vb.signUpEmail.requestFocus();
            vb.signUpEmail.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        if(vb.signUpUsername.getError()!=null || vb.signUpUsername.getEditText().getText().length()==0){
            vb.signUpUsername.requestFocus();
            vb.signUpUsername.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void signUp(){

        DialogsProvider.get(getActivity()).setLoading(true);
        viewModel.signUp(
                vb.signUpUsername.getEditText().getText().toString(),
                vb.signUpEmail.getEditText().getText().toString(),
                vb.signUpPassword.getEditText().getText().toString(),
                vb.signUpPasswordConfirm.getEditText().getText().toString(),
                "Empty"
        ).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_CREATION:
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra(MainActivity.JUST_SIGNED_IN,true);

                    UserAccountManager.signIn(getActivity(), intent, response.headers().get(Repository.AUTH_TOKEN_HEADER), response.body().getUser());
                    break;

                case BaseResponseModel.FAILED_DATA_CONFLICT:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Welcome_back), getString(R.string.you_already_have_an_account_with_this_email));
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