package api.software.salehunter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class SignInFragment extends Fragment {
    TextInputLayout email,password;
    CheckBox rememberMe;
    TextView forgotPassword, signUp;
    Button signIn;
    ImageButton facebook, google, twitter;
    ConstraintLayout socialAuthLayout;

    public SignInFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(((AccountSign)getActivity()).back.getVisibility() == View.VISIBLE) {
            ((AccountSign) getActivity()).title.setText("Sign In");
            ((AccountSign) getActivity()).back.setVisibility(View.GONE);
            ((AccountSign) getActivity()).back.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.back_button_out));
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.sign_in_email);
        password = view.findViewById(R.id.sign_in_password);
        rememberMe = view.findViewById(R.id.sign_in_rememberMe);
        forgotPassword = view.findViewById(R.id.sign_in_forgot_password);
        signUp = view.findViewById(R.id.sign_in_signUp);
        signIn = view.findViewById(R.id.sign_in_button);
        facebook = view.findViewById(R.id.sign_in_facebook);
        google = view.findViewById(R.id.sign_in_google);
        twitter = view.findViewById(R.id.sign_in_twitter);
        socialAuthLayout = view.findViewById(R.id.sign_in_social_auth);

        if(!((AccountSign)getActivity()).googleServices) socialAuthLayout.setVisibility(View.GONE);

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

            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean signIn = true;

                if(password.getError()!=null || password.getEditText().getText().length()==0){
                    password.requestFocus();
                    password.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    signIn = false;
                }

                if(email.getError()!=null || email.getEditText().getText().length()==0){
                    email.requestFocus();
                    email.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    signIn = false;
                }

                if(signIn) signIn(rememberMe.isChecked());

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_bottom,R.anim.slide_to_top,R.anim.slide_from_top,R.anim.slide_to_bottom)
                        .replace(((AccountSign)getActivity()).frameLayout.getId(),new SignUpFragment())
                        .addToBackStack("home")
                        .commit();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                        .replace(((AccountSign)getActivity()).frameLayout.getId(),new ForgotPasswordFragment())
                        .addToBackStack("home")
                        .commit();
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

    boolean validEmail(String email){
        Pattern emailRegex = Pattern.compile("(\\.*\\w+)+@([a-z]+|[A-Z]+)(\\.com|\\.sa)(\\.([a-z]+|[A-Z]+)+)?");
        return emailRegex.matcher(email).matches();
    }

    boolean validPassword(String password){
        Pattern emailRegex = Pattern.compile("(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}");
        return emailRegex.matcher(password).matches();
    }

    void signIn(boolean rememberMe){
        //Server Request
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(false);
        loadingDialog.show(getActivity().getSupportFragmentManager(),loadingDialog.getTag());
        new Handler().postDelayed(()->{
            loadingDialog.dismiss();

        getActivity().getSharedPreferences(MainActivity.APP_STATUS, MODE_PRIVATE)
                .edit()
                .putBoolean(MainActivity.SIGNED_IN,rememberMe)
                .apply();

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.REMEMBER_ME,rememberMe);

        startActivity(intent);
        getActivity().onBackPressed();

        },5000);
    }

}