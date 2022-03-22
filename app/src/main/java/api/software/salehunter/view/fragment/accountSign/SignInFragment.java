package api.software.salehunter.view.fragment.accountSign;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import java.util.regex.Pattern;

import api.software.salehunter.databinding.FragmentSignInBinding;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.R;
import api.software.salehunter.model.SignInModel;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding vb;

    public SignInFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentSignInBinding.inflate(inflater,container,false);
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
        if(((AccountSign)getActivity()).isBackButtonVisible()) {
            ((AccountSign) getActivity()).setTitle("Sign In");
            ((AccountSign) getActivity()).setBackButton(false);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        if(!((AccountSign)getActivity()).isGoogleServicesAvailable()) vb.signInSocialAuth.setVisibility(View.GONE);

        vb.signInEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.signInEmail.getEditText().getText().length()>0){

                    if(TextFieldValidator.isValidEmail(editable.toString())) vb.signInEmail.setError(null);
                    else vb.signInEmail.setError("Email not complete or not valid");

                }
                else vb.signInEmail.setError(null);

            }
        });

        vb.signInPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.signInPassword.getEditText().getText().length()>0){
                    if(TextFieldValidator.isValidPassword(editable.toString())) vb.signInPassword.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.signInPassword.setError("Password Minimum length is 8");
                    else vb.signInPassword.setError("Password must contain numbers, letters & symbols");
                }
                else vb.signInPassword.setError(null);

            }
        });

        vb.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validData = true;

                if(vb.signInPassword.getError()!=null || vb.signInPassword.getEditText().getText().length()==0){
                    vb.signInPassword.requestFocus();
                    vb.signInPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.signInEmail.getError()!=null || vb.signInEmail.getEditText().getText().length()==0){
                    vb.signInEmail.requestFocus();
                    vb.signInEmail.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(validData){
                    SignInModel signInModel = new SignInModel();
                    signInModel.setEmail(vb.signInEmail.getEditText().getText().toString());
                    signInModel.setPassword(vb.signInPassword.getEditText().getText().toString());
                    ((AccountSign)getActivity()).signIn(vb.signInRememberMe.isChecked(), signInModel);
                }

            }
        });


        vb.signInSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });

        vb.signInForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
            }
        });

        vb.signInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountSign)getActivity()).facebookAuth();
            }
        });

        vb.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountSign)getActivity()).googleAuth();
            }
        });

    }

}