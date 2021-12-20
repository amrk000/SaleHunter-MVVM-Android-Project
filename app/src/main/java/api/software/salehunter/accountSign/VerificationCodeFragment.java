package api.software.salehunter.accountSign;

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

        ((AccountSign)getActivity()).title.setText("Verification Code");

        verify = view.findViewById(R.id.verification_code_button);
        resend = view.findViewById(R.id.verification_code_resend);

        code = new EditText[4];
        code[0]=view.findViewById(R.id.verification_code_num);
        code[1]=view.findViewById(R.id.verification_code_num1);
        code[2]=view.findViewById(R.id.verification_code_num2);
        code[3]=view.findViewById(R.id.verification_code_num3);

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

               if(verify) verifyCode();
            }
        });

    }

    void resendCode(){
        //request

       Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.verification_code_snackbar_colayout),"Code is sent again. Check your inbox.",5000);
        snackbar.getView().setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.snackbar,getActivity().getTheme()));
        snackbar.setBackgroundTint(getResources().getColor(R.color.lightModesecondary,getActivity().getTheme()));
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        snackbar.show();
    }

    void verifyCode(){
        //request


        getActivity().getSupportFragmentManager().popBackStack("forgotPassword",FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right)
                .replace(((AccountSign)getActivity()).frameLayout.getId(),new ResetPasswordFragment())
                .commit();

    }
}