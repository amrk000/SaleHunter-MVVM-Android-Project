package api.software.salehunter.view.fragment.main;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import api.software.salehunter.R;
import api.software.salehunter.adapter.ProductsSearchResultsAdapter;
import api.software.salehunter.databinding.FragmentSearchResultsBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.model.SortAndFilterModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.view.fragment.dialogs.SortAndFilterDialog;
import api.software.salehunter.viewmodel.fragment.main.SearchResultsViewModel;

public class SearchResultsFragment extends Fragment{
    private FragmentSearchResultsBinding vb;
    private NavController navController;
    private SearchResultsViewModel viewModel;
    private BottomSheetBehavior<View> mapBottomSheet;
    private GoogleMap googleMap;

    private ProductsSearchResultsAdapter adapter;
    private boolean endOfOnlineProducts = false, endOfLocalProducts = false;

    private LocationManager locationManager;
    private String locationProvider;
    private LocationListener locationListener;
    private ActivityResultLauncher<IntentSenderRequest> locationDialogResultLauncher;
    private ActivityResultLauncher<String[]> locationPermission;
    private Marker userMark;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationDialogResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) registerLocationListener();
            }
        });

        locationPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if(result.get(Manifest.permission.ACCESS_FINE_LOCATION) && result.get(Manifest.permission.ACCESS_COARSE_LOCATION)) registerLocationListener();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(vb==null) vb = FragmentSearchResultsBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
        if(locationListener!=null) locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle("Results");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(viewModel!=null) return;

        ((MainActivity) getActivity()).setTitle("Search");
        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });
        viewModel = new ViewModelProvider(this).get(SearchResultsViewModel.class);
        if (getArguments() != null) viewModel.setKeyword(getArguments().getString("keyword"));
        vb.resultKeyword.setText(viewModel.getKeyword());
        vb.resultKeywordLoading.setText(viewModel.getKeyword());

        mapBottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.result_map_bottomSheet));

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.result_map)).getMapAsync(map ->{
            this.googleMap = map;

            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            vb.resultMapMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    else googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            });

            //Handling Camera Moving Down and BottomSheet Drag Down Touch Events
            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    mapBottomSheet.setDraggable(true);
                }
            });
            vb.mapTouchHandler.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                        mapBottomSheet.setDraggable(false);
                    return false;
                }
            });

        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationProvider = LocationManager.GPS_PROVIDER;
        setLocating(false);

        mapBottomSheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            int defaultTextColor = vb.resultProductMapTitle.getCurrentTextColor();

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        vb.resultProductMapTitle.animate().scaleX(1.5f).scaleY(1.5f).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();

                        ObjectAnimator expandColoring = ObjectAnimator.ofArgb(vb.resultProductMapTitle, "textColor", defaultTextColor, getResources().getColor(R.color.lightModeprimary, getActivity().getTheme()));
                        expandColoring.setDuration(500);
                        expandColoring.start();

                        if(googleMap!=null) registerLocationListener();
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        vb.resultProductMapTitle.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();

                        ObjectAnimator collapseColoring = ObjectAnimator.ofArgb(vb.resultProductMapTitle, "textColor", getResources().getColor(R.color.lightModeprimary, getActivity().getTheme()), defaultTextColor);
                        collapseColoring.setDuration(500);
                        collapseColoring.start();

                        if(locationListener!=null) locationManager.removeUpdates(locationListener);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        vb.resultBack.setOnClickListener(button -> {
            getActivity().onBackPressed();
        });

        vb.resultFilter.setOnClickListener(button -> {
            DialogsProvider.get(getActivity()).sortAndFilterDialog(viewModel.getSortAndFilterModel(), viewModel.getCategories(), viewModel.getBrands(), new SortAndFilterDialog.DialogResultListener() {
                @Override
                public void onApply(SortAndFilterModel sortAndFilterModel) {
                    viewModel.setSortAndFilterModel(sortAndFilterModel);
                    adapter.clearProducts();
                    loadResults();
                }
            });
        });

        adapter = new ProductsSearchResultsAdapter(getContext(), vb.resultRecyclerView);
        vb.resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        vb.resultRecyclerView.setAdapter(adapter);
        adapter.setLastItemReachedListener(new ProductsSearchResultsAdapter.LastItemReachedListener() {
            @Override
            public void onLastOnlineProductReached() {
                loadMoreOnlineProducts();
            }

            @Override
            public void onLastLocalProductReached() {
                loadMoreLocalProducts();
            }
        });

        adapter.setItemInteractionListener(new ProductsSearchResultsAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(long productId, String storeType) {
                Bundle bundle = new Bundle();
                bundle.putLong("productId",productId);

                navController.navigate(R.id.action_searchResultsFragment_to_productPageFragment,bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                setFavourite(productId,favChecked);
            }
        });

        loadResults();
    }

    void setSearching(boolean b){
        if(b == (vb.resultLoadingPage.getVisibility() == View.VISIBLE)) return;

        if(b) vb.resultLoadingPage.setVisibility(View.VISIBLE);
        else {
            vb.resultLoadingPage.setVisibility(View.GONE);
            vb.resultLoadingPage.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.lay_off));
        }
    }

    void setLocating(boolean b){
        if(b){
            vb.resultMapProgress.setVisibility(View.VISIBLE);
            vb.resultMapProgress.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fragment_in));
        }
        else {
            vb.resultMapProgress.setVisibility(View.GONE);
            vb.resultMapProgress.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fragment_out));
        }
    }

    //ERROR BACKEND CURSOR NAV BY LAST ID MAKES DUPLICATED DATA (Backend Problem)
    void loadResults(){
        setSearching(true);

        viewModel.loadResults().observe(getViewLifecycleOwner(),  response ->{
            setSearching(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    viewModel.removeObserverInitialLoadedProducts(getViewLifecycleOwner());

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()) {
                        adapter.showNoOnlineResultsFound();
                        adapter.showNoLocalResultsFound();
                        return;
                    }

                    ArrayList<ProductModel> products = response.body().getProducts();
                    ArrayList<ProductModel> onlineProducts = new ArrayList<>();
                    ArrayList<ProductModel> localProducts = new ArrayList<>();

                    for(ProductModel product : products){
                        if(product.getStoreType().equals(ProductModel.ONLINE_STORE)) onlineProducts.add(product);
                        else{
                            localProducts.add(product);
                            addProductOnMap(product.getStoreLatitude(), product.getStoreLongitude(), product.getStoreName());
                        }

                        viewModel.addCategory(product.getCategory());
                        viewModel.addBrand(product.getBrand());
                    }

                    if(onlineProducts.size()>0){
                        viewModel.setCursorLastOnlineItem(onlineProducts.get(onlineProducts.size()-1).getId());
                        adapter.addOnlineProducts(onlineProducts);
                    }
                    else adapter.showNoOnlineResultsFound();

                    if(localProducts.size()>0){
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            localProducts.sort(new Comparator<ProductModel>() {
//                                @Override
//                                public int compare(ProductModel productModel, ProductModel t1) {
//                                    return (int) (productModel.getId() - t1.getId());
//                                }
//                            });
//                        }
                        viewModel.setCursorLastLocalItem(localProducts.get(localProducts.size()-1).getId());
                        adapter.addLocalProducts(localProducts);
                    }
                    else adapter.showNoLocalResultsFound();

                    break;

                case BaseResponseModel.FAILED_INVALID_DATA:
                    DialogsProvider.get(getActivity()).messageDialog("Query Error", "Invalid Search Query Request\n"+response.toString());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog("Loading Failed", "Please check your connection");
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
            }
        });

    }

    //ERROR BACKEND CURSOR NAV BY LAST ID MAKES DUPLICATED DATA (Backend Problem)
    void loadMoreOnlineProducts(){
        if(endOfOnlineProducts) return;

        adapter.setOnlineProductsLoading(true);

        viewModel.loadMoreOnlineResults().observe(getViewLifecycleOwner(),  response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    adapter.setOnlineProductsLoading(false);
                    viewModel.removeObserverOnlineLoadedProducts(getViewLifecycleOwner());

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()){
                        endOfOnlineProducts = true;
                        return;
                    }

                    ArrayList<ProductModel> products = response.body().getProducts();

                    //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        products.sort(new Comparator<ProductModel>() {
//                            @Override
//                            public int compare(ProductModel productModel, ProductModel t1) {
//                                return (int) (productModel.getId() - t1.getId());
//                            }
//                        });
//                    }

                    adapter.addOnlineProducts(products);
                    viewModel.setCursorLastOnlineItem(products.get(products.size()-1).getId());

                    for(ProductModel product : products){
                        viewModel.addCategory(product.getCategory());
                        viewModel.addBrand(product.getBrand());
                    }

                    break;

                case BaseResponseModel.FAILED_INVALID_DATA:
                    DialogsProvider.get(getActivity()).messageDialog("Query Error", "Invalid Search Query Request\n"+response.toString());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: "+ response.code(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //ERROR BACKEND CURSOR NAV BY LAST ID MAKES DUPLICATED DATA (Backend Problem)
    void loadMoreLocalProducts(){
        if(endOfLocalProducts) return;

        adapter.setLocalProductsLoading(true);

        viewModel.loadMoreLocalResults().observe(getViewLifecycleOwner(),  response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    adapter.setLocalProductsLoading(false);
                    viewModel.removeObserverLocalLoadedProducts(getViewLifecycleOwner());

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()){
                        endOfLocalProducts = true;
                        return;
                    }

                    ArrayList<ProductModel> products = response.body().getProducts();

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        products.sort(new Comparator<ProductModel>() {
//                            @Override
//                            public int compare(ProductModel productModel, ProductModel t1) {
//                                return (int) (productModel.getId() - t1.getId());
//                            }
//                        });
//                    }

                    adapter.addLocalProducts(products);
                    viewModel.setCursorLastLocalItem(products.get(products.size()-1).getId());

                    for(ProductModel product : products){
                        viewModel.addCategory(product.getCategory());
                        viewModel.addBrand(product.getBrand());

                        addProductOnMap(product.getStoreLatitude(), product.getStoreLongitude(), product.getStoreName());
                    }

                    break;

                case BaseResponseModel.FAILED_INVALID_DATA:
                    DialogsProvider.get(getActivity()).messageDialog("Query Error", "Invalid Search Query Request\n"+response.toString());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: "+ response.code(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void setFavourite(long productId, boolean favourite){
        if(favourite){
            viewModel.addFavourite(productId).observe(getViewLifecycleOwner(), response ->{
                if (response.code() != BaseResponseModel.SUCCESSFUL_CREATION)
                    Toast.makeText(getContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();
            });
        }
        else {
            viewModel.removeFavourite(productId).observe(getViewLifecycleOwner(), response ->{
                if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED)
                    Toast.makeText(getContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();
            });
        }

    }

    private void addProductOnMap(double lat, double lng, String storeName) {
        try {
        LatLng productLocation = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(productLocation).title(storeName).snippet("Store").icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_store_mark)));
        } catch (Exception e){
            Toast.makeText(getContext(), "Map Marker Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserOnMap(Location location) {
        try {
        viewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));

        if(userMark == null) userMark = googleMap.addMarker(new MarkerOptions().position(viewModel.getUserLocation()).title(UserAccountManager.getUser(getContext()).getFullName()).snippet("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_user_mark)));
        else userMark.setPosition(viewModel.getUserLocation());

        userMark.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(viewModel.getUserLocation())
                .zoom(googleMap.getCameraPosition().zoom < 8 ? 8:googleMap.getCameraPosition().zoom)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);
        } catch (Exception e){
            Toast.makeText(getContext(), "Map Marker Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void focusOnMark(LatLng mark) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mark)
                .zoom(15)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    void registerLocationListener() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermission.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }

        if (locationManager.isProviderEnabled(locationProvider)) {

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation!=null) setUserOnMap(lastLocation);

            setLocating(true);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    setLocating(false);
                    setUserOnMap(location);

                }
            };

            //choosing which provider depending on accuracy
            if (locationManager.getProvider(LocationManager.GPS_PROVIDER).getAccuracy() < locationManager.getProvider(LocationManager.NETWORK_PROVIDER).getAccuracy())
                locationProvider = LocationManager.NETWORK_PROVIDER;
            else locationProvider = LocationManager.GPS_PROVIDER;

            locationManager.requestLocationUpdates(locationProvider, 5000, 0, locationListener);

        }
        else enableLocation();
    }

    public void enableLocation(){
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
                }
                catch (ApiException ex) {
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