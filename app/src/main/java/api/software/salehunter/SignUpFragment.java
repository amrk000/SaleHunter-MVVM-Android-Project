package api.software.salehunter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {
    TextInputLayout username,email,password,confirmPassword;
    Button signUp;
    ImageButton facebook, google, twitter;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AccountSign)getActivity()).title.setText("Sign Up");
        ((AccountSign)getActivity()).back.setVisibility(View.VISIBLE);
        ((AccountSign)getActivity()).back.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.back_button_in));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = view.findViewById(R.id.sign_up_username);
        email = view.findViewById(R.id.sign_up_email);
        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_password_confirm);
        signUp = view.findViewById(R.id.sign_up_button);
        facebook = view.findViewById(R.id.sign_up_facebook);
        google = view.findViewById(R.id.sign_up_google);
        twitter = view.findViewById(R.id.sign_up_twitter);

        username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(username.getEditText().getText().length()>0){
                    if(validUsername(username.getEditText().getText().toString())) username.setError(null);
                    else if(username.getEditText().getText().length()<2) username.setError("Name is too short !");
                    else username.setError("Not valid name");
                }
                else username.setError(null);

            }
        });

        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(email.getEditText().getText().length()>0){

                    if(validEmail(email.getEditText().getText().toString())) email.setError(null);
                    else email.setError("Email not complete or not valid");

                }
                else email.setError(null);

            }
        });

        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(password.getEditText().getText().length()>0){
                    if(validPassword(password.getEditText().getText().toString())) password.setError(null);
                    else if(password.getEditText().getText().length()<8) password.setError("Password Minimum length is 8");
                    else password.setError("Password must contain numbers, letters & symbols");
                }
                else password.setError(null);

                if(confirmPassword.getEditText().getText().length()>0){
                    if(!confirmPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())) confirmPassword.setError("Not matching the password");
                    else confirmPassword.setError(null);
                }
                else confirmPassword.setError(null);

            }
        });

        confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(confirmPassword.getEditText().getText().length()>0){
                    if(!confirmPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())) confirmPassword.setError("Not matching the password");
                    else confirmPassword.setError(null);
                }
                else confirmPassword.setError(null);

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean signUp = true;

                if(confirmPassword.getError()!=null || confirmPassword.getEditText().getText().length()==0){
                    confirmPassword.requestFocus();
                    confirmPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    signUp = false;
                }

                if(password.getError()!=null || password.getEditText().getText().length()==0){
                    password.requestFocus();
                    password.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    signUp = false;
                }

                if(email.getError()!=null || email.getEditText().getText().length()==0){
                    email.requestFocus();
                    email.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    signUp = false;
                }

                if(username.getError()!=null || username.getEditText().getText().length()==0){
                    username.requestFocus();
                    username.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    signUp = false;
                }

                if(signUp) signUp();

            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountSign)getActivity()).facebookAuth();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountSign)getActivity()).googleAuth();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountSign)getActivity()).twitterAuth();
            }
        });

    }


    boolean validUsername(String username){
        Pattern emailRegex = Pattern.compile("((\\w|\\s|\\.|-)+){2,}");
        return emailRegex.matcher(username).matches();
    }

    boolean validEmail(String email){
        Pattern emailRegex = Pattern.compile("(\\.*\\w+)+@([a-z]+|[A-Z]+)(\\.com|\\.sa)(\\.([a-z]+|[A-Z]+)+)?");
        return emailRegex.matcher(email).matches();
    }

    boolean validPassword(String password){
        Pattern emailRegex = Pattern.compile("(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}");
        return emailRegex.matcher(password).matches();
    }

    void signIn(){
        //Server Request

            getActivity().getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE)
                    .edit()
                    .putBoolean(MainActivity.SIGNED_IN,true)
                    .apply();

            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra(MainActivity.REMEMBER_ME,true);

            startActivity(intent);
            getActivity().onBackPressed();

    }

    void signUp(){

        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(false);
        loadingDialog.show(getActivity().getSupportFragmentManager(),loadingDialog.getTag());
        new Handler().postDelayed(()->{
            loadingDialog.dismiss();

            //Server Request

            signIn();

        },5000);

    }
}