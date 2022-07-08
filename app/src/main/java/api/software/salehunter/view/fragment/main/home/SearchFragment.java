package api.software.salehunter.view.fragment.main.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Locale;

import api.software.salehunter.R;
import api.software.salehunter.adapter.ProductsCardAdapter;
import api.software.salehunter.adapter.ProductsSearchResultsAdapter;
import api.software.salehunter.databinding.FragmentSearchBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.view.activity.Scanner;
import api.software.salehunter.viewmodel.fragment.main.home.SearchViewModel;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding vb;
    private NavController navController;
    private SearchViewModel viewModel;

    private ProductsCardAdapter adapter;

    private ActivityResultLauncher<Intent> voiceResultLauncher;
    private ActivityResultLauncher<Intent> barcodeResultLauncher;
    private ActivityResultLauncher<String> cameraPermission;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voiceResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Scanner.RESULT_OK) {
                            Intent data = result.getData();
                            searchRequest(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                        }
                    }
                });

        barcodeResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            searchRequest(data.getStringExtra("result"));
                        } else if(result.getResultCode() == Scanner.RESULT_TRY_AGAIN) barcodeResultLauncher.launch(new Intent(getContext(),Scanner.class));
                    }
                });

        cameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) barcodeResultLauncher.launch(new Intent(getContext(),Scanner.class));
                else Toast.makeText(getContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(vb==null) vb = FragmentSearchBinding.inflate(inflater,container,false);
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
        if(viewModel!=null) return;

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        vb.searchSearchbar.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_SEARCH) searchRequest(textView.getText().toString());
            return false;
        });

        vb.searchVoice.setOnClickListener(button -> {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Product Name ...");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag("EN").toLanguageTag());

            voiceResultLauncher.launch(intent);

        });

        vb.searchBarcodeScan.setOnClickListener(button -> {
            int checker = ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA);

            if(checker == PackageManager.PERMISSION_GRANTED) barcodeResultLauncher.launch(new Intent(getActivity(),Scanner.class));
            else cameraPermission.launch(Manifest.permission.CAMERA);

        });

        adapter = new ProductsCardAdapter(getContext(),vb.searchProductsRecyclerView);
        vb.searchProductsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        vb.searchProductsRecyclerView.setAdapter(adapter);
        adapter.setHideFavButton(true);

        adapter.setItemInteractionListener(new ProductsCardAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(long productId, String storeType) {
                Bundle bundle = new Bundle();
                bundle.putLong("productId",productId);
                navController.navigate(R.id.action_homeFragment_to_productPageFragment,bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                setFavourite(productId,favChecked);
            }
        });

        loadRecommendedProducts();

    }

    void searchRequest(String keyword){
        if(keyword.isEmpty()) return;

        Bundle bundle = new Bundle();
        bundle.putString("keyword",keyword);
        navController.navigate(R.id.action_homeFragment_to_searchResultsFragment,bundle);
    }

    void loadRecommendedProducts(){
        vb.searchLoadingRecommended.setVisibility(View.VISIBLE);

        viewModel.getRecommendedProducts().observe(getViewLifecycleOwner(),  response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    vb.searchLoadingRecommended.setVisibility(View.GONE);

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()) return;

                    ArrayList<ProductModel> products = response.body().getProducts();

                    adapter.addProducts(products);

                    viewModel.removeObserverRecommendedProducts(getViewLifecycleOwner());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    //Toast.makeText(getContext(), "Loading Recommendations Failed", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog("Server Error","Code: "+ response.code());
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

}