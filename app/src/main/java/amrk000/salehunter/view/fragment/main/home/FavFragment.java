package amrk000.salehunter.view.fragment.main.home;

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

import amrk000.salehunter.R;
import amrk000.salehunter.adapter.ProductsListAdapter;
import amrk000.salehunter.databinding.FragmentFavBinding;
import amrk000.salehunter.databinding.FragmentHistoryBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.ProductModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.view.activity.MainActivity;
import amrk000.salehunter.viewmodel.fragment.main.home.FavViewModel;

public class FavFragment extends Fragment {
    private FragmentFavBinding vb;
    private NavController navController;
    private FavViewModel viewModel;

    private ProductsListAdapter adapter;

    public FavFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(vb==null) vb = FragmentFavBinding.inflate(inflater,container,false);
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
        viewModel = new ViewModelProvider(this).get(FavViewModel.class);

        adapter = new ProductsListAdapter(getContext(),vb.favRecyclerVeiw);
        vb.favRecyclerVeiw.setLayoutManager(new LinearLayoutManager(getContext()));
        vb.favRecyclerVeiw.setAdapter(adapter);

        adapter.setItemInteractionListener(new ProductsListAdapter.ItemInteractionListener() {
            @Override
            public void onProductClicked(ProductModel product) {
                Bundle bundle = new Bundle();
                bundle.putLong("productId",product.getId());
                navController.navigate(R.id.action_homeFragment_to_productPageFragment,bundle);
            }

            @Override
            public void onProductAddedToFav(long productId, boolean favChecked) {
                if(!favChecked){
                    removeFavourite(productId);
                    adapter.removeProductById(productId);
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeFavourite(adapter.getData().get(viewHolder.getAdapterPosition()).getId());
                adapter.removeProductByIndex(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);

                    if(dX>0) {
                        Drawable icon = DrawableCompat.wrap(getContext().getDrawable(R.drawable.delete_icon)).getCurrent();
                        int leftMargin = 10;
                        float scale = 1.4f;

                        int left = viewHolder.itemView.getLeft() + leftMargin;
                        int right = (int) (left + icon.getIntrinsicWidth()*scale);

                        int vcenter = viewHolder.itemView.getTop() + viewHolder.itemView.getHeight()/2;
                        int top = (int) (vcenter - icon.getIntrinsicHeight()*scale/2);
                        int bottom = (int) (vcenter + icon.getIntrinsicHeight()*scale/2);

                        icon.setBounds(left, top, right, bottom);
                        icon.draw(c);
                    } else if (dX<0){
                        Drawable icon = DrawableCompat.wrap(getContext().getDrawable(R.drawable.delete_icon)).getCurrent();
                        int rightMargin = 10;
                        float scale = 1.4f;

                        int right = viewHolder.itemView.getRight() - rightMargin;
                        int left = (int) (right - icon.getIntrinsicWidth()*scale);

                        int vcenter = viewHolder.itemView.getTop() + viewHolder.itemView.getHeight()/2;
                        int top = (int) (vcenter - icon.getIntrinsicHeight()*scale/2);
                        int bottom = (int) (vcenter + icon.getIntrinsicHeight()*scale/2);

                        icon.setBounds(left, top, right, bottom);
                        icon.draw(c);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        });
        itemTouchHelper.attachToRecyclerView(vb.favRecyclerVeiw);

        vb.favEmptyList.setVisibility(View.GONE);
        if(adapter.getItemCount()>0) adapter.clearProducts();
        loadProducts();
    }

    void loadProducts(){
        vb.favLoading.setVisibility(View.VISIBLE);

        viewModel.getFavoriteProducts().observe(getViewLifecycleOwner(),  response ->{

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    vb.favLoading.setVisibility(View.GONE);

                    if(response.body().getProducts() == null || response.body().getProducts().isEmpty()){
                        vb.favEmptyList.setVisibility(View.VISIBLE);
                        return;
                    }

                    ArrayList<ProductModel> products = response.body().getProducts();

                    for(ProductModel product : products) product.setFavorite(true);

                    adapter.addProducts(products);

                    viewModel.removeObserverOfProducts(getViewLifecycleOwner());
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Loading_Failed),getString(R.string.please_check_your_internet_connection)+ response.code());
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }
        });

    }

    void removeFavourite(long productId){
        viewModel.removeFavourite(productId).observe(getViewLifecycleOwner(), response ->{
            if(adapter.getItemCount()==0) vb.favEmptyList.setVisibility(View.VISIBLE);

            if (response.code() != BaseResponseModel.SUCCESSFUL_DELETED)
                Toast.makeText(getContext(), "Error" + response.code(), Toast.LENGTH_SHORT).show();
        });
    }
}