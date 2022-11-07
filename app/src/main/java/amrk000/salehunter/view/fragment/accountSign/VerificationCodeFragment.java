package amrk000.salehunter.view.fragment.accountSign;

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

import amrk000.salehunter.databinding.FragmentVerificationCodeBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.view.activity.AccountSign;
import amrk000.salehunter.R;
import amrk000.salehunter.viewmodel.fragment.accountSign.VerificationCodeViewModel;

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

        ((AccountSign)getActivity()).setTitle(getString(R.string.Verification_Code));

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
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Pin_is_expired), getString(R.string.Please_resend_pin_code_again));
                    break;

                //invalid
                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Pin_is_invalid), getString(R.string.Please_check_it_and_try_again));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.please_check_your_internet_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
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
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Email_isnt_registered), getString(R.string.you_dont_have_an_account));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.Please_Check_your_connection));
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());

            }
        });
    }


}