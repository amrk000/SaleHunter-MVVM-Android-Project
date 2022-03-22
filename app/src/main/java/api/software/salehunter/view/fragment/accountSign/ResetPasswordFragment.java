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

import api.software.salehunter.databinding.FragmentResetPasswordBinding;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.R;
import api.software.salehunter.model.ResetPasswordModel;

public class ResetPasswordFragment extends Fragment {
    private FragmentResetPasswordBinding vb;

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

        ((AccountSign)getActivity()).setTitle("Reset Password");

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
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.PASSWORD_MIN,TextFieldValidator.NO_MAX_VALUE)) vb.resetPasswordPassword.setError("Password Minimum length is 8");
                    else vb.resetPasswordPassword.setError("Password must contain numbers, letters & symbols");
                }
                else vb.resetPasswordPassword.setError(null);

                if(vb.resetPasswordPasswordConfirm.getEditText().getText().length()>0){
                    if(!vb.resetPasswordPasswordConfirm.getEditText().getText().toString().equals(vb.resetPasswordPassword.getEditText().getText().toString())) vb.resetPasswordPasswordConfirm.setError("Not matching the password");
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
                    if(!vb.resetPasswordPasswordConfirm.getEditText().getText().toString().equals(vb.resetPasswordPassword.getEditText().getText().toString())) vb.resetPasswordPasswordConfirm.setError("Not matching the password");
                    else vb.resetPasswordPasswordConfirm.setError(null);
                }
                else vb.resetPasswordPasswordConfirm.setError(null);

            }
        });

        vb.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validData = true;

                if(vb.resetPasswordPasswordConfirm.getError()!=null || vb.resetPasswordPasswordConfirm.getEditText().getText().length()==0){
                    vb.resetPasswordPasswordConfirm.requestFocus();
                    vb.resetPasswordPasswordConfirm.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.resetPasswordPassword.getError()!=null || vb.resetPasswordPassword.getEditText().getText().length()==0){
                    vb.resetPasswordPassword.requestFocus();
                    vb.resetPasswordPassword.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(validData) {
                    String pin="";
                    if(getArguments()!=null) pin = getArguments().getString("pin");

                    ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
                    resetPasswordModel.setPassword(vb.resetPasswordPassword.getEditText().getText().toString());
                    resetPasswordModel.setPasswordConfirm(vb.resetPasswordPasswordConfirm.getEditText().getText().toString());

                    ((AccountSign)getActivity()).resetPassword(resetPasswordModel, pin);
                }

            }
        });

    }


}