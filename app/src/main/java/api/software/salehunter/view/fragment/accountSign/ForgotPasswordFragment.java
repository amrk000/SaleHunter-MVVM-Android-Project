package api.software.salehunter.view.fragment.accountSign;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import api.software.salehunter.databinding.FragmentForgotPasswordBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.view.activity.AccountSign;
import api.software.salehunter.R;
import api.software.salehunter.viewmodel.fragment.accountSign.ForgotPasswordViewModel;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding vb;
    private ForgotPasswordViewModel viewModel;
    private NavController navController;

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

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

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

        vb.forgotPasswordButton.setOnClickListener(button -> {
            if(isDataValid()) sendEmailVerification();
        });
    }

    boolean isDataValid(){
        boolean validData = true;

        if(vb.forgotPasswordEmail.getError()!=null || vb.forgotPasswordEmail.getEditText().getText().length()==0){
            vb.forgotPasswordEmail.requestFocus();
            vb.forgotPasswordEmail.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void sendEmailVerification(){
        DialogsProvider.get(getActivity()).setLoading(true);
        viewModel.sendEmailVerification(vb.forgotPasswordEmail.getEditText().getText().toString())
                .observe(getViewLifecycleOwner(), response ->{
                    DialogsProvider.get(getActivity()).setLoading(false);

                    switch (response.code()){
                        case BaseResponseModel.SUCCESSFUL_OPERATION:
                            Bundle bundle = new Bundle();
                            bundle.putString("email", vb.forgotPasswordEmail.getEditText().getText().toString());
                            navController.navigate(R.id.action_forgotPasswordFragment_to_verificationCodeFragment,bundle);
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