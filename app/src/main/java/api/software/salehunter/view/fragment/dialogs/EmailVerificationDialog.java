package api.software.salehunter.view.fragment.dialogs;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.databinding.FragmentEmailVerificationDialogBinding;
import api.software.salehunter.model.EmailVerificationModel;
import api.software.salehunter.model.ResponseModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.view.fragment.dialogs.LoadingDialog;
import api.software.salehunter.view.fragment.dialogs.MessageDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationDialog extends BottomSheetDialogFragment {
    private FragmentEmailVerificationDialogBinding vb;
    EditText[] code;
    String email, titleText, subTitleText;
    EmailVerificationModel emailVerificationModel;

    Repository repository;

    DialogResultListener dialogResultListener;

    public EmailVerificationDialog() {

    }

    public interface DialogResultListener{
        void onSuccess();
        void onCancel();
    }

    public void setDialogResultListener(DialogResultListener dialogResultListener){
        this.dialogResultListener = dialogResultListener;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String titleText, String subTitleText) {
       this.titleText = titleText;
       this.subTitleText = subTitleText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentEmailVerificationDialogBinding.inflate(inflater,container,false);
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

        repository = new Repository();

        vb.emailVerificationDialogTitle.setText(titleText);
        vb.emailVerificationDialogSubtitle.setText(subTitleText);

        emailVerificationModel = new EmailVerificationModel();
        emailVerificationModel.setEmail(email);
        sendEmailVerification(emailVerificationModel);

        code = new EditText[6];
        code[0]=vb.verificationDialogCodeNum;
        code[1]=vb.verificationDialogCodeNum1;
        code[2]=vb.verificationDialogCodeNum2;
        code[3]=vb.verificationDialogCodeNum3;
        code[4]=vb.verificationDialogCodeNum4;
        code[5]=vb.verificationDialogCodeNum5;

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

        vb.verificationDialogCodeResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendPinToEmail(emailVerificationModel);
            }
        });

        vb.verificationDialogCodeButton.setOnClickListener(new View.OnClickListener() {
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

                    verifyPin(pin.toString());
                }
            }
        });

    }

    public void sendEmailVerification(EmailVerificationModel emailVerificationModel){
        repository.sendEmailVerification(emailVerificationModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        break;

                    default:
                        dismiss();
                        DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                dismiss();
                DialogsProvider.get(getActivity()).messageDialog("Email Verification Request Failed", "Please Check your connection !");
            }
        });

    }

    public void resendPinToEmail(EmailVerificationModel emailVerificationModel){

        repository.sendEmailVerification(emailVerificationModel, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:

                        Snackbar snackbar = Snackbar.make(vb.verificationDialogCodeSnackbarColayout,"New PIN is on the way! check your inbox.",15000);
                        snackbar.getView().setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.snackbar,getActivity().getTheme()));
                        snackbar.setBackgroundTint(getResources().getColor(R.color.lightModesecondary,getActivity().getTheme()));
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                        snackbar.show();
                        break;

                    case ResponseModel.FAILED_NOT_FOUND:
                        DialogsProvider.get(getActivity()).messageDialog("Email isn't registered", "you don't have an account\nplease sign up first");
                        break;

                    default:
                        DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                DialogsProvider.get(getActivity()).setLoading(false);
                DialogsProvider.get(getActivity()).messageDialog("Request Failed", "Please Check your connection !");
            }
        });

    }

    public void verifyPin(String pinToken){
        DialogsProvider.get(getActivity()).setLoading(true);

        repository.verifyToken(pinToken, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                DialogsProvider.get(getActivity()).setLoading(false);

                switch (response.code()){
                    //valid
                    case 200:
                        if(dialogResultListener!=null) dialogResultListener.onSuccess();
                        dismiss();
                        break;

                    //expired
                    case 400:
                        DialogsProvider.get(getActivity()).messageDialog("Pin is expired", "Please resend pin code again");
                        break;

                    //invalid
                    case 404:
                        DialogsProvider.get(getActivity()).messageDialog("Pin is invalid", "Please check it and try again");
                        break;

                    default:
                        if(dialogResultListener!=null) dialogResultListener.onCancel();
                        DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                if(dialogResultListener!=null) dialogResultListener.onCancel();
                DialogsProvider.get(getActivity()).setLoading(false);
                DialogsProvider.get(getActivity()).messageDialog("Verification Failed", "Please Check your connection !");
            }
        });

    }


}