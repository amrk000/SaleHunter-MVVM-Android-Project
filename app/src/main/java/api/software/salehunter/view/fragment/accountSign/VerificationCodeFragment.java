package api.software.salehunter.view.fragment.accountSign;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import api.software.salehunter.databinding.FragmentVerificationCodeBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.R;
import api.software.salehunter.viewmodel.fragment.accountSign.VerificationCodeViewModel;

public class VerificationCodeFragment extends Fragment {
    private FragmentVerificationCodeBinding vb;
    private VerificationCodeViewModel viewModel;
    private NavController navController;
    private EditText[] code;

    public VerificationCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentVerificationCodeBinding.inflate(inflater,container,false);
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

        ((AccountSign)getActivity()).setTitle("Verification Code");

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(VerificationCodeViewModel.class);

        code = new EditText[6];
        code[0]=vb.verificationCodeNum;
        code[1]=vb.verificationCodeNum1;
        code[2]=vb.verificationCodeNum2;
        code[3]=vb.verificationCodeNum3;
        code[4]=vb.verificationCodeNum4;
        code[5]=vb.verificationCodeNum5;

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

            code[index].setOnFocusChangeListener((view1, b) -> {
                if(b){
                    code[index].selectAll();
                    code[index].animate().scaleX(1.2f).scaleY(1.2f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                } else code[index].animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();

            });

        }

        vb.verificationCodeResend.setOnClickListener(button -> {
            resendCode();
        });

        vb.verificationCodeButton.setOnClickListener(button -> {
            if(isDataValid()) verifyPin();
        });

    }

    boolean isDataValid(){
        boolean validData = true;

        for(EditText block:code){
            if(block.getText().toString().isEmpty()){
                block.requestFocus();
                block.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
                validData=false;
                break;
            }
        }

        return validData;
    }

    void verifyPin(){
        DialogsProvider.get(getActivity()).setLoading(true);

        StringBuilder pin = new StringBuilder();
        for(EditText digit : code) pin.append(digit.getText().toString());

        viewModel.verifyToken(pin.toString()).observe(getViewLifecycleOwner(), response ->{
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                //valid
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    Bundle bundle = new Bundle();
                    bundle.putString("pin",pin.toString());
                    navController.navigate(R.id.action_verificationCodeFragment_to_resetPasswordFragment, bundle);
                    break;

                //expired
                case BaseResponseModel.FAILED_INVALID_DATA:
                    DialogsProvider.get(getActivity()).messageDialog("Pin is expired", "Please resend pin code again");
                    break;

                //invalid
                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog("Pin is invalid", "Please check it and try again");
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog("Request Error", "Please Check your connection.");
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
            }

        });
    }

    void resendCode(){
        String email="";
        if(getArguments()!=null) email = getArguments().getString("email");

        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.resendEmailVerification(email).observe(getViewLifecycleOwner(), response ->{
            DialogsProvider.get(getActivity()).setLoading(false);
            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:

                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.verification_code_snackbar_colayout),"New PIN is on the way! check your inbox.",15000);
                    snackbar.getView().setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.snackbar,getActivity().getTheme()));
                    snackbar.setBackgroundTint(getResources().getColor(R.color.lightModesecondary,getActivity().getTheme()));
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.show();
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog("Email isn't registered", "you don't have an account\nplease sign up first");
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog("Request Error", "Please Check your connection.");
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());

            }
        });
    }


}