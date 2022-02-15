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
import api.software.salehunter.model.ForgotPasswordModel;

public class ForgotPasswordFragment extends Fragment {
    TextInputLayout email;
    Button sendCode;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!((AccountSign)getActivity()).isBackButtonVisible()) {
            ((AccountSign) getActivity()).setTitle("Forgot Password");
            ((AccountSign) getActivity()).setBackButton(true);
        }

        email = view.findViewById(R.id.forgot_password_email);
        sendCode = view.findViewById(R.id.forgot_password_button);

        email.requestFocus();
        email.clearFocus();

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

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean sendCode = true;

                if(email.getError()!=null || email.getEditText().getText().length()==0){
                    email.requestFocus();
                    email.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    sendCode = false;
                }

                if(sendCode) {
                    ForgotPasswordModel forgotPAsswordModel = new ForgotPasswordModel();
                    forgotPAsswordModel.setEmail(email.getEditText().getText().toString());

                    ((AccountSign)getActivity()).sendPinToEmail(forgotPAsswordModel);
                }

            }
        });
    }

    boolean validEmail(String email){
        Pattern emailRegex = Pattern.compile("(\\.*\\w+)+@([a-z]+|[A-Z]+)(\\.com|\\.sa)(\\.([a-z]+|[A-Z]+)+)?");
        return emailRegex.matcher(email).matches();
    }

}