package api.software.salehunter.accountSign;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import api.software.salehunter.R;
import api.software.salehunter.model.ForgotPasswordModel;

public class VerificationCodeFragment extends Fragment {
    EditText[] code;
    Button verify;
    TextView resend;

    public VerificationCodeFragment() {
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
        return inflater.inflate(R.layout.fragment_verification_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AccountSign)getActivity()).setTitle("Verification Code");

        verify = view.findViewById(R.id.verification_code_button);
        resend = view.findViewById(R.id.verification_code_resend);

        code = new EditText[6];
        code[0]=view.findViewById(R.id.verification_code_num);
        code[1]=view.findViewById(R.id.verification_code_num1);
        code[2]=view.findViewById(R.id.verification_code_num2);
        code[3]=view.findViewById(R.id.verification_code_num3);
        code[4]=view.findViewById(R.id.verification_code_num4);
        code[5]=view.findViewById(R.id.verification_code_num5);

        code[0].requestFocus();
        code[0].clearFocus();

        for(int i=0; i<code.length; i++){

            final int index = i;

            if(index < code.length-1){

                code[index].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!editable.toString().isEmpty()){
                            code[index+1].requestFocus();
                            code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.written_code,getActivity().getTheme()));
                        }
                        else code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.empty_code,getActivity().getTheme()));

                    }
                });

            }
            else{

                code[index].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!editable.toString().isEmpty()){
                            code[index].clearFocus();
                            code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.written_code,getActivity().getTheme()));
                        }
                        else code[index].setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.empty_code,getActivity().getTheme()));
                    }
                });

            }

            code[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        code[index].selectAll();
                        code[index].animate().scaleX(1.2f).scaleY(1.2f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                    } else code[index].animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();

                }
            });

        }

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendCode();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean verify=true;

                for(EditText block:code){
                    if(block.getText().toString().isEmpty()){
                        block.requestFocus();
                        block.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                        verify=false;
                        break;
                    }
                }

               if(verify) {

                   StringBuilder pin = new StringBuilder();
                   for(EditText digit : code) pin.append(digit.getText().toString());

                   ((AccountSign)getActivity()).verifyPin(pin.toString());
               }
            }
        });

    }

    void resendCode(){
        ForgotPasswordModel forgotPAsswordModel = new ForgotPasswordModel();
        forgotPAsswordModel.setEmail(((AccountSign)getActivity()).getEmail());

        ((AccountSign)getActivity()).resendPinToEmail(forgotPAsswordModel);

    }


}