package api.software.salehunter.accountSign;

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
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import api.software.salehunter.R;

public class ResetPasswordFragment extends Fragment {
    TextInputLayout password,confirmPassword;
    Button resetPassword;

    public ResetPasswordFragment() {
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
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AccountSign)getActivity()).title.setText("Reset Password");

        password = view.findViewById(R.id.reset_password_password);
        confirmPassword = view.findViewById(R.id.reset_password_password_confirm);
        resetPassword = view.findViewById(R.id.reset_password_button);

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

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean resetPassword = true;

                if(confirmPassword.getError()!=null || confirmPassword.getEditText().getText().length()==0){
                    confirmPassword.requestFocus();
                    confirmPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    resetPassword = false;
                }

                if(password.getError()!=null || password.getEditText().getText().length()==0){
                    password.requestFocus();
                    password.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    resetPassword = false;
                }

                if(resetPassword) resetPassword();

            }
        });

    }

    boolean validPassword(String password){
        Pattern emailRegex = Pattern.compile("(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}");
        return emailRegex.matcher(password).matches();
    }

    void resetPassword(){
        //request

        getActivity().getSupportFragmentManager().popBackStack();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                .replace(((AccountSign)getActivity()).frameLayout.getId(),new SignInFragment())
                .commit();
    }

}