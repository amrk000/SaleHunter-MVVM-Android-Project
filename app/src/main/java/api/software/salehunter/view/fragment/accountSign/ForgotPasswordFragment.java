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

import api.software.salehunter.databinding.FragmentForgotPasswordBinding;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.R;
import api.software.salehunter.model.EmailVerificationModel;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding vb;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentForgotPasswordBinding.inflate(inflater,container,false);
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
        if(!((AccountSign)getActivity()).isBackButtonVisible()) {
            ((AccountSign) getActivity()).setTitle("Forgot Password");
            ((AccountSign) getActivity()).setBackButton(true);
        }

        vb.forgotPasswordEmail.requestFocus();
        vb.forgotPasswordEmail.clearFocus();

        vb.forgotPasswordEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.forgotPasswordEmail.getEditText().getText().length()>0){

                    if(TextFieldValidator.isValidEmail(editable.toString())) vb.forgotPasswordEmail.setError(null);
                    else vb.forgotPasswordEmail.setError("Email not complete or not valid");

                }
                else vb.forgotPasswordEmail.setError(null);

            }
        });

        vb.forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validData = true;

                if(vb.forgotPasswordEmail.getError()!=null || vb.forgotPasswordEmail.getEditText().getText().length()==0){
                    vb.forgotPasswordEmail.requestFocus();
                    vb.forgotPasswordEmail.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(validData) {
                    EmailVerificationModel forgotPAsswordModel = new EmailVerificationModel();
                    forgotPAsswordModel.setEmail(vb.forgotPasswordEmail.getEditText().getText().toString());

                    ((AccountSign)getActivity()).sendEmailVerification(forgotPAsswordModel);
                }

            }
        });
    }


}