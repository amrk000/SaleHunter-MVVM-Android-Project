package api.software.salehunter.view.fragment.accountSign;

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

import api.software.salehunter.data.Repository;
import api.software.salehunter.databinding.FragmentSignUpBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.R;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.viewmodel.fragment.accountSign.SignUpViewModel;

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
            ((AccountSign) getActivity()).setTitle("Sign Up");
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
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.USERNAME_MIN,TextFieldValidator.USERNAME_MAX)) vb.signUpUsername.setError("Name is too short !");
                    else vb.signUpUsername.setError("Not valid name");
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
                    else vb.signUpEmail.setError("Email not complete or not valid");

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
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.signUpPassword.setError("Password Minimum length is 8");
                    else vb.signUpPassword.setError("Password must contain numbers, letters & symbols");
                }
                else vb.signUpPassword.setError(null);

                if(vb.signUpPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.signUpPasswordConfirm.getEditText().getText().toString().equals(editable.toString())) vb.signUpPasswordConfirm.setError("Not matching the password");
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
                    if(!editable.toString().equals(vb.signUpPassword.getEditText().getText().toString())) vb.signUpPasswordConfirm.setError("Not matching the password");
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
                    DialogsProvider.get(getActivity()).messageDialog("Welcome back !", "you already have an account with this email\nplease sign in");
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog("Sign Up Failed", "Please Check your connection !");
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
            }

        });

    }

}