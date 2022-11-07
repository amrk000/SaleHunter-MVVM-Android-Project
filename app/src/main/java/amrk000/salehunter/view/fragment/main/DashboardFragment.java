package amrk000.salehunter.view.fragment.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import amrk000.salehunter.R;
import amrk000.salehunter.adapter.ProductsListAdapter;
import amrk000.salehunter.databinding.FragmentDashboardBinding;
import amrk000.salehunter.databinding.FragmentStorePageBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.ProductModel;
import amrk000.salehunter.model.StoreModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.view.activity.MainActivity;
import amrk000.salehunter.viewmodel.fragment.main.DashboardViewModel;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding vb;
    private DashboardViewModel viewModel;
    private NavController navController;

    private ProductsListAdapter adapter;
    private boolean endOfProducts = false;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(vb==null) vb = FragmentDashboardBinding.inflate(inflater,container,false);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.Dashboard));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(viewModel!=null) return;

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        if (getArguments() != null) viewModel.setStoreId(getArguments().getLong("storeId"));

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });

        adapter = new ProductsListAdapter(getContext(),vb.dashboardRecyclerView);
        vb.dashboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.dashboardRecyclerView.setAdapter(adapter);

        adapter.setItemInteractionListener(new ProductsListAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(ProductModel product) {
                Bundle bundle = new Bundle();
                bundle.putInt(CreateProductFragment.ACTION_KEY,CreateProductFragment.ACTION_EDIT_PRODUCT);
                bundle.putString(CreateProductFragment.PRODUCT_DATA_KEY,new Gson().toJson(product));
                bundle.putLong(CreateProductFragment.STORE_ID_KEY,viewModel.getStorePageModel().getStore().getId());
                navController.navigate(R.id.action_dashboardFragment_to_createProductFragment,bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                setFavourite(productId,favChecked);
            }
        });

        adapter.setLastItemReachedListener(new ProductsListAdapter.LastItemReachedListener() {
            @Override
            public void onLastItemReached() {
                loadMoreProducts();
            }
        });

        vb.dashboardAddProduct.setOnClickListener(button ->{
            Bundle bundle = new Bundle();
            bundle.putLong(CreateProductFragment.STORE_ID_KEY,viewModel.getStorePageModel().getStore().getId());
            navController.navigate(R.id.action_dashboardFragment_to_createProductFragment,bundle);
        });

        vb.dashboardEditStore.setOnClickListener(button ->{
            Bundle bundle = new Bundle();
            bundle.putInt(CreateStoreFragment.ACTION_KEY,CreateStoreFragment.ACTION_EDIT_STORE);
            bundle.putString(CreateStoreFragment.STORE_DATA_KEY,new Gson().toJson(viewModel.getStorePageModel().getStore()));
            navController.navigate(R.id.action_dashboardFragment_to_createStoreFragment2,bundle);
        });

        vb.dashboardStoreCard.setVisibility(View.INVISIBLE);

        loadStoreData();
    }

    void loadStoreData(){
        vb.dashboardLoadingPage.setVisibility(View.VISIBLE);

        viewModel.getStore().observe(getViewLifecycleOwner(), response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if(response.body()!=null){
                        viewModel.setStorePageModel(response.body());
                        renderStoreData();
                        renderInitialProducts();
                        vb.dashboardLoadingPage.setVisibility(View.GONE);

                        vb.dashboardStoreCard.setVisibility(View.VISIBLE);
                        vb.dashboardStoreCard.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in));
                        vb.dashboardRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_from_bottom));
                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Store_Not_Found), "Store Not Found in Server.");
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), getString(R.string.Loading_Failed), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: "+ response.code(), Toast.LENGTH_SHORT).show();
            }

            viewModel.removeObserverStoreData(getViewLifecycleOwner());
        });

    }

    void renderStoreData(){
        StoreModel storeModel = viewModel.getStorePageModel().getStore();

        vb.dashboardStoreName.setText(storeModel.getName());

            Glide.with(this)
                    .load(storeModel.getLogo())
                    .placeholder(R.drawable.store_placeholder)
                    .circleCrop()
                    .into(vb.dashboardLogo);

            vb.dashboardStoreCategory.setText(storeModel.getStoreCategory());

    }

    void renderInitialProducts(){
        ArrayList<ProductModel> products = viewModel.getStorePageModel().getProducts();
        if(products.size()==0){
            endOfProducts = true;
            vb.dashboardNoProducts.setVisibility(View.VISIBLE);
        }
        else adapter.addProducts(products);
    }

    void loadMoreProducts(){
        if(endOfProducts) return;

        adapter.setLoading(true);

        viewModel.getNextPage().observe(getViewLifecycleOwner(),  response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    adapter.setLoading(false);

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()){
                        endOfProducts = true;
                        return;
                    }

                    viewModel.removeObserverStoreData(getViewLifecycleOwner());
                    adapter.addProducts(response.body().getProducts());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), getString(R.string.Loading_Failed), Toast.LENGTH_SHORT).show();
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
}