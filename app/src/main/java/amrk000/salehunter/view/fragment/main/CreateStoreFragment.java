package amrk000.salehunter.view.fragment.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amrk000.salehunter.R;
import amrk000.salehunter.databinding.FragmentCreateStoreBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.StoreModel;
import amrk000.salehunter.model.UserModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.util.ImageEncoder;
import amrk000.salehunter.util.TextFieldValidator;
import amrk000.salehunter.util.UserAccountManager;
import amrk000.salehunter.view.activity.MainActivity;
import amrk000.salehunter.viewmodel.fragment.main.CreateStoreViewModel;

public class CreateStoreFragment extends Fragment {
    private FragmentCreateStoreBinding vb;
    private CreateStoreViewModel viewModel;
    private NavController navController;

    private Uri image;
    private ActivityResultLauncher<Intent> imagePicker;

    private LocationManager locationManager;
    private String locationProvider;
    private LocationListener locationListener;
    private ActivityResultLauncher<IntentSenderRequest> locationDialogResultLauncher;
    private ActivityResultLauncher<String[]> locationPermission;
    private boolean gpsLocationDetected = false;
    private Double latitude, longitude;

    private int action = 0;
    public static final String ACTION_KEY = "action";
    public static final int ACTION_CREATE_STORE = 0;
    public static final int ACTION_EDIT_STORE = 1;

    private StoreModel storeData;
    public static final String STORE_DATA_KEY = "storeData";

    public CreateStoreFragment() {
        // Required empty public constructor
    }

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
                            .into(vb.createStorePic);
                }

            }
        });

        locationDialogResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) getAddressByGPS();
            }
        });

        locationPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if(result.get(Manifest.permission.ACCESS_FINE_LOCATION) && result.get(Manifest.permission.ACCESS_COARSE_LOCATION)) getAddressByGPS();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vb = FragmentCreateStoreBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle(getString(R.string.Store));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null){
            action = getArguments().getInt(ACTION_KEY);
            if(action == ACTION_EDIT_STORE) storeData = new Gson().fromJson(getArguments().getString(STORE_DATA_KEY),StoreModel.class);
        }

        viewModel = new ViewModelProvider(this).get(CreateStoreViewModel.class);

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });

        vb.createStoreLoadingAddress.setVisibility(View.GONE);

        vb.createStoreHasWhatsapp.setEnabled(false);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationProvider = LocationManager.GPS_PROVIDER;

        vb.createStoreEditPic.setOnClickListener(button -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePicker.launch(intent);
        });

        vb.createStoreName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    if (TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.STORENAME_MIN, TextFieldValidator.STORENAME_MAX))
                        vb.createStoreName.setError(getString(R.string.Store_Name_length_should_be_between) + TextFieldValidator.STORENAME_MIN + " & " + TextFieldValidator.STORENAME_MAX);

                    else if (TextFieldValidator.isValidStoreName(editable.toString()))
                        vb.createStoreName.setError(null);
                    else vb.createStoreName.setError(getString(R.string.Not_valid_name));
                } else vb.createStoreName.setError(null);

            }
        });

        vb.createStoreCategory.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.CATEGORY_MIN, TextFieldValidator.CATEGORY_MAX))
                    vb.createStoreCategory.setError(getString(R.string.Category_length_should_be_between) + TextFieldValidator.CATEGORY_MIN + " & " + TextFieldValidator.CATEGORY_MAX);
                else vb.createStoreCategory.setError(null);

            }
        });

        vb.createStoreAddress.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.ADDRESS_MIN, TextFieldValidator.ADDRESS_MAX))
                    vb.createStoreAddress.setError(getString(R.string.Address_length_should_be_between) + TextFieldValidator.ADDRESS_MIN + " & " + TextFieldValidator.ADDRESS_MAX);
                else vb.createStoreAddress.setError(null);

            }
        });

        vb.createStoreDescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.DESCRIPTION_MIN, TextFieldValidator.DESCRIPTION_MAX))
                    vb.createStoreDescription.setError(getString(R.string.Description_length_should_be_between) + TextFieldValidator.DESCRIPTION_MIN + " & " + TextFieldValidator.DESCRIPTION_MAX);
                else vb.createStoreDescription.setError(null);

            }
        });

        vb.createStorePhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.PHONE_MIN, TextFieldValidator.PHONE_MAX)){
                    vb.createStorePhone.setError(getString(R.string.Phone_Number_digits_should_be) + TextFieldValidator.PHONE_MIN + getString(R.string.digit));
                    vb.createStoreHasWhatsapp.setEnabled(false);
                    vb.createStoreHasWhatsapp.setChecked(false);
                }
                else{
                    vb.createStorePhone.setError(null);
                    if(editable.toString().length() > 0) vb.createStoreHasWhatsapp.setEnabled(true);
                }

            }
        });

        vb.createStoreWebsite.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    if (TextFieldValidator.isValidStoreWebsite(editable.toString()))
                        vb.createStoreWebsite.setError(null);
                    else vb.createStoreWebsite.setError(getString(R.string.Not_valid_website_URL));
                } else vb.createStoreWebsite.setError(null);

            }
        });

        vb.createStoreFacebook.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    if (TextFieldValidator.isValidUserNameId(editable.toString()))
                        vb.createStoreFacebook.setError(null);
                    else vb.createStoreFacebook.setError(getString(R.string.Not_valid_facebook_username));
                } else vb.createStoreFacebook.setError(null);

            }
        });

        vb.createStoreInstagram.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    if (TextFieldValidator.isValidUserNameId(editable.toString()))
                        vb.createStoreInstagram.setError(null);
                    else vb.createStoreInstagram.setError(getString(R.string.Not_valid_instagram_username));
                } else vb.createStoreInstagram.setError(null);

            }
        });

        vb.createStoreAddress.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b & !gpsLocationDetected) {
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.GPS_Location_Required), getString(R.string.Please_use_gps_button_first_to_get_accurate_store_location_then_you_can_edit_address_if_needed));
                    vb.createStoreAddress.getEditText().clearFocus();
                }
            }
        });

        vb.createStoreFindLocation.setOnClickListener(button -> {
            getAddressByGPS();
        });

        if(action == ACTION_EDIT_STORE){
            vb.createStoreSubmitButton.setText(R.string.Update_Store);
            vb.createStoreSubmitButton.setOnClickListener(button -> {
                if (isDataValid()) updateStore();
            });

            renderStoreData();
        }
        else {
            vb.createStoreSubmitButton.setText(getString(R.string.Create_Store));
            vb.createStoreSubmitButton.setOnClickListener(button -> {
                if (isDataValid()) createStore();
            });
        }

    }

    boolean isDataValid() {
        boolean validData = true;

        if (vb.createStoreInstagram.getError() != null) {
            vb.createStoreInstagram.requestFocus();
            vb.createStoreInstagram.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createStoreFacebook.getError() != null) {
            vb.createStoreFacebook.requestFocus();
            vb.createStoreFacebook.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createStoreWebsite.getError() != null) {
            vb.createStoreWebsite.requestFocus();
            vb.createStoreWebsite.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createStorePhone.getError() != null || (vb.createStoreHasWhatsapp.isChecked() & vb.createStoreDescription.getEditText().getText().length() == 0)) {
            vb.createStorePhone.requestFocus();
            vb.createStorePhone.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createStoreDescription.getError() != null || vb.createStoreDescription.getEditText().getText().length() == 0) {
            vb.createStoreDescription.requestFocus();
            vb.createStoreDescription.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createStoreAddress.getError() != null || vb.createStoreAddress.getEditText().getText().length() == 0) {
            vb.createStoreAddress.requestFocus();
            vb.createStoreAddress.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createStoreCategory.getError() != null || vb.createStoreCategory.getEditText().getText().length() == 0) {
            vb.createStoreCategory.requestFocus();
            vb.createStoreCategory.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createStoreName.getError() != null || vb.createStoreName.getEditText().getText().length() == 0) {
            vb.createStoreName.requestFocus();
            vb.createStoreName.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void createStore() {

        DialogsProvider.get(getActivity()).setLoading(true);

        String encodedImage = null;
        if(image!=null) encodedImage = ImageEncoder.get().encode(getContext(),image);

        viewModel.createStore(
                encodedImage,
                vb.createStoreName.getEditText().getText().toString(),
                vb.createStoreCategory.getEditText().getText().toString(),
                vb.createStoreAddress.getEditText().getText().toString(),
                latitude,
                longitude,
                vb.createStoreDescription.getEditText().getText().toString(),
                vb.createStorePhone.getEditText().getText().toString(),
                vb.createStoreHasWhatsapp.isChecked(),
                vb.createStoreWebsite.getEditText().getText().toString(),
                vb.createStoreFacebook.getEditText().getText().toString(),
                vb.createStoreInstagram.getEditText().getText().toString()
        ).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_CREATION:
                    StoreModel store = response.body().getStore();
                    UserModel user = UserAccountManager.getUser(getContext());
                    user.setStoreId(store.getId());

                    UserAccountManager.updateUser(getContext(),user);
                    ((MainActivity)getActivity()).loadUserData(user);

                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId",store.getId());
                    navController.navigate(R.id.action_createStoreFragment2_to_storePageFragment,bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

            viewModel.removeObserverCreateStore(getViewLifecycleOwner());
        });

    }

    void updateStore(){
        DialogsProvider.get(getActivity()).setLoading(true);

        String encodedImage = null;
        if(image!=null) encodedImage = ImageEncoder.get().encode(getContext(),image);

        viewModel.updateStore(
                storeData.getId(),
                encodedImage,
                vb.createStoreName.getEditText().getText().toString(),
                vb.createStoreCategory.getEditText().getText().toString(),
                vb.createStoreAddress.getEditText().getText().toString(),
                latitude,
                longitude,
                vb.createStoreDescription.getEditText().getText().toString(),
                vb.createStorePhone.getEditText().getText().toString(),
                vb.createStoreHasWhatsapp.isChecked(),
                vb.createStoreWebsite.getEditText().getText().toString(),
                vb.createStoreFacebook.getEditText().getText().toString(),
                vb.createStoreInstagram.getEditText().getText().toString()
        ).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", storeData.getId());
                    navController.navigate(R.id.action_createStoreFragment2_to_dashboardFragment, bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

            viewModel.removeObserverUpdateStore(getViewLifecycleOwner());
        });
    }

    void renderStoreData(){
        Glide.with(this)
                .load(storeData.getLogo())
                .placeholder(R.drawable.store_placeholder)
                .circleCrop()
                .into(vb.createStorePic);

        vb.createStoreName.getEditText().setText(storeData.getName());
        vb.createStoreCategory.getEditText().setText(storeData.getStoreCategory());
        vb.createStoreAddress.getEditText().setText(storeData.getAddress());
        vb.createStoreDescription.getEditText().setText(storeData.getDescription());
        vb.createStorePhone.getEditText().setText(storeData.getPhone());
        vb.createStoreHasWhatsapp.setChecked(storeData.getWhatsapp()!=null);
        vb.createStoreWebsite.getEditText().setText(storeData.getWebsite());

        try {
            String facebookLink = storeData.getFacebook();
            String facebookUsername = new URL(facebookLink).getPath().replace("/","");
            vb.createStoreFacebook.getEditText().setText(facebookUsername);
        }
        catch (Exception e){
            Log.e("Exception",e.getMessage());
        }

        try {
        String instagramLink = storeData.getInstagram();
        String instagramUsername = new URL(instagramLink).getPath().replace("/","");
        vb.createStoreInstagram.getEditText().setText(instagramUsername);
        }
        catch (Exception e){
            Log.e("Exception",e.getMessage());
        }

        latitude = storeData.getLatitude();
        longitude = storeData.getLongitude();
    }

    void getAddressByGPS() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermission.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }

        if (locationManager.isProviderEnabled(locationProvider)) {

            vb.createStoreLoadingAddress.setVisibility(View.VISIBLE);
            vb.createStoreFindLocation.setEnabled(false);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    vb.createStoreLoadingAddress.setVisibility(View.GONE);
                    vb.createStoreFindLocation.setEnabled(true);

                    gpsLocationDetected = true;

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Geocoder geocoder = new Geocoder(getContext(), Locale.forLanguageTag("EN"));

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {

                        List<Address> addresses = null;

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (addresses != null) {
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String street = addresses.get(0).getThoroughfare();

                            if(street==null) street = "";

                            String address = city+", "+state+", "+street;

                            new Handler(Looper.getMainLooper()).post(() -> {
                                vb.createStoreAddress.getEditText().setText(address);
                            });
                        }

                    });

                    locationManager.removeUpdates(this);
                }
            };

            //choosing which provider depending on accuracy
            if (locationManager.getProvider(LocationManager.GPS_PROVIDER).getAccuracy() < locationManager.getProvider(LocationManager.NETWORK_PROVIDER).getAccuracy())
                locationProvider = LocationManager.NETWORK_PROVIDER;
            else locationProvider = LocationManager.GPS_PROVIDER;

            locationManager.requestLocationUpdates(locationProvider, 5000, 0, locationListener);

        } else enableLocation();
    }

    public void enableLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext()).checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //show dialog

                            ResolvableApiException resolvableApiException = (ResolvableApiException) ex;
                            locationDialogResultLauncher.launch(new IntentSenderRequest.Builder(resolvableApiException.getResolution()).build());

                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //showing dialog not allowed
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            break;
                    }
                }
            }
        });
    }

}