package api.software.salehunter.view.fragment.main.home;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import api.software.salehunter.R;
import api.software.salehunter.adapter.ProductsListAdapter;
import api.software.salehunter.databinding.FragmentFavBinding;
import api.software.salehunter.databinding.FragmentOnSaleBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.viewmodel.fragment.main.home.FavViewModel;
import api.software.salehunter.viewmodel.fragment.main.home.OnSaleViewModel;

public class OnSaleFragment extends Fragment {
    private FragmentOnSaleBinding vb;
    private NavController navController;
    private OnSaleViewModel viewModel;

    private ProductsListAdapter adapter;

    public OnSaleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(vb==null) vb = FragmentOnSaleBinding.inflate(inflater,container,false);
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

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });
        viewModel = new ViewModelProvider(this).get(OnSaleViewModel.class);

        adapter = new ProductsListAdapter(getContext(),vb.onSaleRecyclerVeiw);
        vb.onSaleRecyclerVeiw.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.onSaleRecyclerVeiw.setAdapter(adapter);

        adapter.setItemInteractionListener(new ProductsListAdapter.ItemInteractionListener() {
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

        loadProducts();
    }

    void loadProducts(){
        vb.onSaleLoading.setVisibility(View.VISIBLE);

        viewModel.getOnSaleProducts().observe(getViewLifecycleOwner(),  response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    vb.onSaleLoading.setVisibility(View.GONE);

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()){
                        DialogsProvider.get(getActivity()).messageDialog("There are no products on sale !","Check this page later.");
                        return;
                    }

                    ArrayList<ProductModel> products = response.body().getProducts();

                    adapter.addProducts(products);

                    viewModel.removeObserverOfProducts(getViewLifecycleOwner());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog("Loading Failed","Please check your connection"+ response.code());
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