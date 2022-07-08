package api.software.salehunter.view.fragment.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.AppBarLayout;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import api.software.salehunter.R;
import api.software.salehunter.adapter.ProductsCardAdapter;
import api.software.salehunter.databinding.FragmentProductPageBinding;
import api.software.salehunter.databinding.FragmentStorePageBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.model.ProductPageModel;
import api.software.salehunter.model.StoreModel;
import api.software.salehunter.model.StorePageModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.viewmodel.fragment.main.ProductPageViewModel;
import api.software.salehunter.viewmodel.fragment.main.StorePageViewModel;

public class StorePageFragment extends Fragment {
    private FragmentStorePageBinding vb;
    private StorePageViewModel viewModel;
    private NavController navController;

    private ProductsCardAdapter adapter;
    private boolean endOfProducts = false;

    private int toolbarOffset = -1000;

    public StorePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(vb==null) vb = FragmentStorePageBinding.inflate(inflater,container,false);
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
        ((MainActivity) getActivity()).setTitle("Store");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(viewModel!=null) return;

        viewModel = new ViewModelProvider(this).get(StorePageViewModel.class);
        if (getArguments() != null) viewModel.setStoreId(getArguments().getLong("storeId"));

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });

        vb.storePageAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

              if(verticalOffset > toolbarOffset){
                  vb.storePageExpandedLabelToolbar.animate().alpha(1.0f).setDuration(150).withStartAction(()->{
                      vb.storePageExpandedLabelToolbar.setVisibility(View.VISIBLE);
                  }).start();

              }
              else if(verticalOffset < toolbarOffset){
                  vb.storePageExpandedLabelToolbar.animate().alpha(0).setDuration(150).withEndAction(()->{
                      vb.storePageExpandedLabelToolbar.setVisibility(View.GONE);
                  }).start();
              }
            }
        });

        adapter = new ProductsCardAdapter(getContext(),vb.storePageRecyclerView);
        vb.storePageRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        vb.storePageRecyclerView.setAdapter(adapter);

        adapter.setItemInteractionListener(new ProductsCardAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(long productId, String storeType) {
                Bundle bundle = new Bundle();
                bundle.putLong("productId",productId);
                navController.navigate(R.id.action_storePageFragment_to_productPageFragment,bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                setFavourite(productId,favChecked);
            }
        });

        adapter.setLastItemReachedListener(new ProductsCardAdapter.LastItemReachedListener() {
            @Override
            public void onLastItemReached() {
                loadMoreProducts();
            }
        });

        loadStoreData();
    }

    void loadStoreData(){
        vb.storePageLoadingPage.setVisibility(View.VISIBLE);

        viewModel.getStore().observe(getViewLifecycleOwner(), response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if(response.body()!=null){
                        viewModel.setStorePageModel(response.body());
                        renderStoreData();
                        renderInitialProducts();
                        vb.storePageLoadingPage.setVisibility(View.GONE);

                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog("Store Not Found !", "Store Not Found in Server.");
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Server Error | Code: "+ response.code(), Toast.LENGTH_SHORT).show();
            }

            viewModel.removeObserverStoreData(getViewLifecycleOwner());
        });

    }

    void renderStoreData(){
        StoreModel storeModel = viewModel.getStorePageModel().getStore();

        vb.storePageStoreName.setText(storeModel.getName());
        vb.storePageStoreNameToolbar.setText(storeModel.getName());

        if(storeModel.getType().equals("online")){

            Glide.with(this)
                    .load(storeModel.getLogo())
                    .fitCenter()
                    .placeholder(R.drawable.store_placeholder)
                    .into(vb.storePageLogo);

            Glide.with(this)
                    .load(storeModel.getLogo())
                    .fitCenter()
                    .placeholder(R.drawable.store_placeholder)
                    .into(vb.storePageLogoToolbar);

            vb.storePageStoreCategory.setText("Online E-commerce");
            vb.storePageStoreCategoryToolbar.setText("Online E-commerce");

            vb.storePageStoreLocation.setVisibility(View.GONE);
            vb.storePageStorePhone.setVisibility(View.GONE);

            toolbarOffset = -500;
        }
        else{

            Glide.with(this)
                    .load(storeModel.getLogo())
                    .placeholder(R.drawable.store_placeholder)
                    .circleCrop()
                    .into(vb.storePageLogo);

            Glide.with(this)
                    .load(storeModel.getLogo())
                    .placeholder(R.drawable.store_placeholder)
                    .circleCrop()
                    .into(vb.storePageLogoToolbar);

            vb.storePageStoreCategory.setText(storeModel.getStoreCategory());
            vb.storePageStoreCategoryToolbar.setText(storeModel.getStoreCategory());

            if(storeModel.getAddress() == null) vb.storePageStoreWebsite.setVisibility(View.GONE);
            else{
                vb.storePageStoreLocation.setText(storeModel.getAddress());
                vb.storePageStoreLocation.setOnClickListener(button ->{
                    Toast.makeText(getContext(), vb.storePageStoreLocation.getText(), Toast.LENGTH_LONG).show();
                });
            }

            if(storeModel.getPhone() == null) vb.storePageStoreWebsite.setVisibility(View.GONE);
            else{
                vb.storePageStorePhone.setText(storeModel.getPhone());
                vb.storePageStorePhone.setOnClickListener(button ->{
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", vb.storePageStorePhone.getText().toString(), null));
                    startActivity(intent);
                });
            }

        }

        vb.storePageStoreDescription.setText(storeModel.getDescription());

        if(storeModel.getWebsite() == null) vb.storePageStoreWebsite.setVisibility(View.GONE);
        else{
            vb.storePageStoreWebsite.setOnClickListener(button ->{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeModel.getWebsite()));
                startActivity(intent);
            });
        }

        if(storeModel.getWhatsapp() == null) vb.storePageStoreWhatsapp.setVisibility(View.GONE);
        else{
            vb.storePageStoreWhatsapp.setOnClickListener(button ->{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+20"+storeModel.getWhatsapp()));
                startActivity(intent);
            });
        }

        if(storeModel.getFacebook() == null) vb.storePageStoreFacebook.setVisibility(View.GONE);
        else{
            vb.storePageStoreFacebook.setOnClickListener(button ->{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeModel.getFacebook().replace(".com/",".com/n/?")));
                startActivity(intent);
            });
        }

        if(storeModel.getInstagram() == null) vb.storePageStoreInstagram.setVisibility(View.GONE);
        else{
            vb.storePageStoreInstagram.setOnClickListener(button ->{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeModel.getInstagram()));
                startActivity(intent);
            });
        }

    }

    void renderInitialProducts(){
        ArrayList<ProductModel> products = viewModel.getStorePageModel().getProducts();
        if(products.size()==0){
            endOfProducts = true;
            vb.storePageNoProducts.setVisibility(View.VISIBLE);

            if(viewModel.getStoreId() == UserAccountManager.getUser(getContext()).getStoreId()){
                vb.storePageNoProductsTitle.setText("You didn't add products yet !");
                vb.storePageAddProducts.setOnClickListener(button ->{
                    Toast.makeText(getContext(),"Dashboard Navigation",Toast.LENGTH_SHORT).show();
                });
            }
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
}