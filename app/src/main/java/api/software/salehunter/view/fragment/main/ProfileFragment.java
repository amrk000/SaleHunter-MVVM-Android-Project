package api.software.salehunter.view.fragment.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.databinding.FragmentProfileBinding;
import api.software.salehunter.model.ResponseModel;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.model.UserResponseModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.ImageEncoder;
import api.software.salehunter.util.TextFieldValidator;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.view.fragment.dialogs.LoadingDialog;
import api.software.salehunter.view.fragment.dialogs.MessageDialog;
import api.software.salehunter.view.fragment.dialogs.EmailVerificationDialog;
import api.software.salehunter.view.fragment.dialogs.PasswordChangeDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding vb;
    UserModel user;
    Uri image;
    boolean picChanged, usernameChanged, emailChanged;

    ActivityResultLauncher<Intent> imagePicker;

    Repository  repository;
    String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    image = result.getData().getData();

                    Glide.with(getContext()).load(image)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .placeholder(R.drawable.profile_placeholder)
                            .circleCrop()
                            .into(vb.profilePic);

                    picChanged = true;
                    showSaveButton(true);
                }

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       vb = FragmentProfileBinding.inflate(inflater,container,false);
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
        token = UserAccountManager.getToken(getContext(),UserAccountManager.TOKEN_TYPE_BEARER);

        showSaveButton(false);

        user = UserAccountManager.getUser(getContext());
        renderProfileData();

        Glide.with(this).load(R.drawable.abstract_bg)
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(40)))
                .into(vb.profileCover);

        Glide.with(this).load(user.getImageLink())
                .centerCrop()
                .placeholder(R.drawable.profile_placeholder)
                .circleCrop()
                .into(vb.profilePic);


        vb.profileUsernameField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                vb.profileUsername.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.profileUsernameField.getEditText().getText().length()>0){
                    if(TextFieldValidator.isValidUsername(editable.toString())) vb.profileUsernameField.setError(null);
                    else if(TextFieldValidator.outLengthRange(editable.toString(),TextFieldValidator.USERNAME_MIN,TextFieldValidator.USERNAME_MAX)) vb.profileUsernameField.setError("Name is too short !");
                    else vb.profileUsernameField.setError("Not valid name");
                }
                else vb.profileUsernameField.setError(null);

                usernameChanged = !user.getFullName().equals(vb.profileUsernameField.getEditText().getText().toString());

                showSaveButton(usernameChanged || emailChanged || picChanged);
            }
        });

        vb.profileEmailField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(vb.profileEmailField.getEditText().getText().length()>0){

                    if(TextFieldValidator.isValidEmail(editable.toString())) vb.profileEmailField.setError(null);
                    else vb.profileEmailField.setError("Email not complete or not valid");

                }
                else vb.profileEmailField.setError(null);

                emailChanged = !user.getEmail().equals(vb.profileEmailField.getEditText().getText().toString());

                showSaveButton(usernameChanged || emailChanged || picChanged);
            }
        });

        vb.profileEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imagePicker.launch(intent);
            }
        });

        vb.profilePasswordField.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogsProvider.get(getActivity()).passwordChangeDialog();
            }
        });

        vb.profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validData = true;

                if(vb.profileEmailField.getError()!=null || vb.profileEmailField.getEditText().getText().length()==0){
                    vb.profileEmailField.requestFocus();
                    vb.profileEmailField.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }

                if(vb.profileUsernameField.getError()!=null || vb.profileUsernameField.getEditText().getText().length()==0){
                    vb.profileUsernameField.requestFocus();
                    vb.profileUsernameField.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.missing));
                    validData = false;
                }


                if(validData) {

                    if(picChanged) user.setEncodedImage(ImageEncoder.get().encode(getContext(),image));

                    if(usernameChanged) user.setFullName(vb.profileUsernameField.getEditText().getText().toString());

                    if(emailChanged){
                        DialogsProvider.get(getActivity()).emailVerificationDialog(
                                user.getEmail(),
                                "Email Change Security",
                                "Please enter the pin that we sent to your old email first before saving the new one",
                                new EmailVerificationDialog.DialogResultListener() {
                                    @Override
                                    public void onSuccess() {
                                        user.setEmail(vb.profileEmailField.getEditText().getText().toString());
                                        publishProfileData(user);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                }
                        );

                    }
                    else publishProfileData(user);

                }

            }
        });

    }

    void showSaveButton(boolean show){
        if(show){
            vb.profileSave.setVisibility(View.VISIBLE);
            vb.profileSave.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.lay_on));
        }
        else {
            vb.profileSave.setVisibility(View.GONE);
            vb.profileSave.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.lay_off));
        }
    }

    void renderProfileData(){
        vb.profileUsername.setText(user.getFullName());
        vb.profileUsernameField.getEditText().setText(user.getFullName());
        vb.profileEmailField.getEditText().setText(user.getEmail());
        vb.profilePasswordField.getEditText().setText("00000000");
    }

    void publishProfileData(UserModel userModel){
        DialogsProvider.get(getActivity()).setLoading(true);

        repository.updateUser(token,userModel, new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                DialogsProvider.get(getActivity()).setLoading(false);

                switch (response.code()){
                    case ResponseModel.SUCCESSFUL_OPERATION:
                        user = response.body().getUser();
                        renderProfileData();
                        showSaveButton(false);
                        ((MainActivity)getActivity()).updateMenuUserData(user);
                        UserAccountManager.updateUser(getContext(),user);
                        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        break;

                    case ResponseModel.FAILED_AUTH:
                        UserAccountManager.signOutForced(getActivity());

                        DialogsProvider.get(getActivity()).messageDialog("Session Expired", "Please sign in again");
                        break;

                    default:
                        DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                DialogsProvider.get(getActivity()).setLoading(false);

                DialogsProvider.get(getActivity()).messageDialog("Saving Failed", "Please check your connection");
            }
        });
    }


}