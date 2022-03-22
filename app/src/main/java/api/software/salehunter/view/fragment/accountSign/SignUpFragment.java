package api.software.salehunter.view.fragment.accountSign;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import java.util.regex.Pattern;

import api.software.salehunter.databinding.FragmentSignUpBinding;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.R;
import api.software.salehunter.model.SignUpModel;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding vb;

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

        if(!((AccountSign)getActivity()).isGoogleServicesAvailable()) vb.signUpSocialAuth.setVisibility(View.GONE);

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
                    if(TextFieldValidator.isValidUsername(editable.toString())) vb.signUpUsername.setError(null);
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


        vb.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validData = true;

                if(vb.signUpPasswordConfirm.getError()!=null || vb.signUpPasswordConfirm.getEditText().getText().length()==0){
                    vb.signUpPasswordConfirm.requestFocus();
                    vb.signUpPasswordConfirm.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.signUpPassword.getError()!=null || vb.signUpPassword.getEditText().getText().length()==0){
                    vb.signUpPassword.requestFocus();
                    vb.signUpPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.signUpEmail.getError()!=null || vb.signUpEmail.getEditText().getText().length()==0){
                    vb.signUpEmail.requestFocus();
                    vb.signUpEmail.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.signUpUsername.getError()!=null || vb.signUpUsername.getEditText().getText().length()==0){
                    vb.signUpUsername.requestFocus();
                    vb.signUpUsername.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(validData){
                    SignUpModel signUpModel = new SignUpModel();
                    signUpModel.setFullName(vb.signUpUsername.getEditText().getText().toString());
                    signUpModel.setEmail(vb.signUpEmail.getEditText().getText().toString());
                    signUpModel.setPassword(vb.signUpPassword.getEditText().getText().toString());
                    signUpModel.setPasswordConfirm(vb.signUpPasswordConfirm.getEditText().getText().toString());
                    signUpModel.setProfileImage("test");

                    ((AccountSign)getActivity()).signUp(signUpModel);
                }

            }
        });

        vb.signUpFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountSign)getActivity()).facebookAuth();
            }
        });

        vb.signUpGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountSign)getActivity()).googleAuth();
            }
        });


    }

}