package api.software.salehunter.view.fragment.main.home;

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

import java.util.ArrayList;

import api.software.salehunter.R;
import api.software.salehunter.adapter.ProductsListAdapter;
import api.software.salehunter.databinding.FragmentHistoryBinding;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.view.activity.MainActivity;
import api.software.salehunter.viewmodel.fragment.main.home.HistoryViewModel;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding vb;
    private NavController navController;
    private HistoryViewModel viewModel;

    private ProductsListAdapter adapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(vb==null) vb = FragmentHistoryBinding.inflate(inflater,container,false);
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
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        adapter = new ProductsListAdapter(getContext(),vb.historyRecyclerVeiw);
        vb.historyRecyclerVeiw.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.historyRecyclerVeiw.setAdapter(adapter);
        adapter.setHideFavButton(true);
        adapter.setShowDate(true);

        adapter.setItemInteractionListener(new ProductsListAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(long productId, String storeType) {
                Bundle bundle = new Bundle();
                bundle.putLong("productId",productId);
                navController.navigate(R.id.action_homeFragment_to_productPageFragment,bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {

            }
        });

        vb.historyEmptyList.setVisibility(View.GONE);
        loadProducts();
    }

    void loadProducts(){
        vb.historyLoading.setVisibility(View.VISIBLE);

        viewModel.getViewedProducts().observe(getViewLifecycleOwner(),  response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    vb.historyLoading.setVisibility(View.GONE);

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()){
                        vb.historyEmptyList.setVisibility(View.VISIBLE);
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
}