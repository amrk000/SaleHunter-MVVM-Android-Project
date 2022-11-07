package amrk000.salehunter.view.fragment.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.util.ArrayList;

import amrk000.salehunter.R;
import amrk000.salehunter.databinding.FragmentCreateProductBinding;
import amrk000.salehunter.databinding.FragmentCreateStoreBinding;
import amrk000.salehunter.model.BaseResponseModel;
import amrk000.salehunter.model.ProductModel;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.util.ImageEncoder;
import amrk000.salehunter.util.TextFieldValidator;
import amrk000.salehunter.util.UserAccountManager;
import amrk000.salehunter.view.activity.MainActivity;
import amrk000.salehunter.viewmodel.fragment.main.CreateProductViewModel;

public class CreateProductFragment extends Fragment {
    private FragmentCreateProductBinding vb;
    private CreateProductViewModel viewModel;
    private NavController navController;

    private Uri image1, image2, image3;
    private ActivityResultLauncher<Intent> imagePicker1, imagePicker2, imagePicker3;

    private int action = 0;
    public static final String ACTION_KEY = "action";
    public static final int ACTION_CREATE_PRODUCT = 0;
    public static final int ACTION_EDIT_PRODUCT = 1;

    private ProductModel productData;
    public static final String PRODUCT_DATA_KEY = "productData";
    private long storeId;
    public static final String STORE_ID_KEY = "storeId";

    private Double productPrice = 0.0;

    public CreateProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePicker1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    image1 = result.getData().getData();

                    Glide.with(getContext()).load(image1)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .into(vb.createProductImage1);
                }

            }
        });

        imagePicker2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    image2 = result.getData().getData();

                    Glide.with(getContext()).load(image2)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .into(vb.createProductImage2);
                }

            }
        });

        imagePicker3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    image3 = result.getData().getData();

                    Glide.with(getContext()).load(image3)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .into(vb.createProductImage3);
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vb = FragmentCreateProductBinding.inflate(inflater, container, false);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.product));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null){
            action = getArguments().getInt(ACTION_KEY);
            if(action == ACTION_EDIT_PRODUCT) productData = new Gson().fromJson(getArguments().getString(PRODUCT_DATA_KEY),ProductModel.class);

            storeId = getArguments().getLong(STORE_ID_KEY);
        }

        viewModel = new ViewModelProvider(this).get(CreateProductViewModel.class);

        new Handler().post(()->{
            navController = ((MainActivity)getActivity()).getAppNavController();
        });

        vb.createProductScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                double scrollViewHeight = vb.createProductScroll.getChildAt(0).getBottom() - vb.createProductScroll.getHeight();
                double scrollPosition = (scrollY / scrollViewHeight) * 100;
                vb.createProductPhaseProgress.setProgress((int)scrollPosition);
            }
        });

        vb.createProductImage1.setOnClickListener(imageView -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePicker1.launch(intent);
        });

        vb.createProductImage2.setOnClickListener(imageView -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePicker2.launch(intent);
        });

        vb.createProductImage3.setOnClickListener(imageView -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePicker3.launch(intent);
        });

        vb.createProductSaleSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vb.createProductSalePercent.setText(progress+"%");
                if(progress<100) vb.createProductDiscountedPrice.setText((productPrice-(productPrice*progress/100))+"");
                else vb.createProductDiscountedPrice.setText(R.string.free);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vb.createProductPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    productPrice = Double.parseDouble(editable.toString());

                    if (productPrice > 0) vb.createProductPrice.setError(null);
                    else {
                        vb.createProductPrice.setError(getString(R.string.Not_a_valid_price));
                        productPrice = 1.0;
                    }
                } else{
                    vb.createProductPrice.setError(null);
                    vb.createProductDiscountedPrice.setText("0");
                }

                vb.createProductDiscountedPrice.setText((productPrice-(productPrice*vb.createProductSaleSlider.getProgress()/100)+""));

            }
        });


        vb.createProductName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    if (TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.PRODUCTNAME_MIN, TextFieldValidator.PRODUCTNAME_MAX))
                        vb.createProductName.setError(getString(R.string.Product_Name_length_should_be_between) + TextFieldValidator.PRODUCTNAME_MIN + " & " + TextFieldValidator.PRODUCTNAME_MAX);
                    else if (TextFieldValidator.isValidProductName(editable.toString()))
                        vb.createProductName.setError(null);
                    else vb.createProductName.setError(getString(R.string.Not_valid_name));
                } else vb.createProductName.setError(null);

            }
        });

        vb.createProductNameArabic.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    if (TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.PRODUCTNAME_MIN, TextFieldValidator.PRODUCTNAME_MAX))
                        vb.createProductNameArabic.setError(getString(R.string.Product_Name_length_should_be_between_8_and_64));
                    else if (TextFieldValidator.isValidProductName(editable.toString()))
                        vb.createProductNameArabic.setError(null);
                    else vb.createProductNameArabic.setError(getString(R.string.Not_valid_name));
                } else vb.createProductNameArabic.setError(null);

            }
        });

        vb.createProductCategory.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.CATEGORY_MIN, TextFieldValidator.CATEGORY_MAX))
                    vb.createProductCategory.setError(getString(R.string.Category_length_should_be_between) + TextFieldValidator.CATEGORY_MIN + " & " + TextFieldValidator.CATEGORY_MAX);
                else vb.createProductCategory.setError(null);

            }
        });

        vb.createProductCategoryArabic.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.CATEGORY_MIN, TextFieldValidator.CATEGORY_MAX))
                    vb.createProductCategoryArabic.setError(getString(R.string.Category_length_should_be_between) + TextFieldValidator.CATEGORY_MIN + " & " + TextFieldValidator.CATEGORY_MAX);
                else vb.createProductCategoryArabic.setError(null);

            }
        });

        vb.createProductBrand.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.BRAND_MIN, TextFieldValidator.BRAND_MAX))
                    vb.createProductBrand.setError(getString(R.string.Brand_length_should_be_between) + TextFieldValidator.BRAND_MIN + " & " + TextFieldValidator.BRAND_MAX);
                else vb.createProductBrand.setError(null);

            }
        });

        vb.createProductDescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.DESCRIPTION_MIN, TextFieldValidator.DESCRIPTION_MAX))
                    vb.createProductDescription.setError(getString(R.string.Description_length_should_be_between) + TextFieldValidator.DESCRIPTION_MIN + " & " + TextFieldValidator.DESCRIPTION_MAX);
                else vb.createProductDescription.setError(null);

            }
        });

        vb.createProductDescriptionArabic.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.DESCRIPTION_MIN, TextFieldValidator.DESCRIPTION_MAX))
                    vb.createProductDescriptionArabic.setError(getString(R.string.Description_length_should_be_between) + TextFieldValidator.DESCRIPTION_MIN + " & " + TextFieldValidator.DESCRIPTION_MAX);
                else vb.createProductDescriptionArabic.setError(null);

            }
        });

        if(action == ACTION_EDIT_PRODUCT){
            vb.createProductSubmitButton.setText(R.string.Update_Product);
            vb.createProductSubmitButton.setOnClickListener(button -> {
                if (isDataValid()) updateProduct();
            });

            vb.createProductDeleteProduct.setOnClickListener(button -> {

                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                materialAlertDialogBuilder.setTitle(R.string.Delete_Product);
                materialAlertDialogBuilder.setMessage(R.string.Are_you_sure_that_you_want_to_delete_this_product);
                materialAlertDialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProduct();
                    }
                });

                materialAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                materialAlertDialogBuilder.show();
            });

            renderProductData();
        }
        else {
            vb.createProductSubmitButton.setText(R.string.Create_Product);
            vb.createProductSubmitButton.setOnClickListener(button -> {
                if (isDataValid()) createProduct();
            });
        }
    }

    boolean isDataValid() {
        boolean validData = true;

        if (vb.createProductDescriptionArabic.getError() != null || vb.createProductDescriptionArabic.getEditText().getText().length() == 0) {
            vb.createProductDescriptionArabic.requestFocus();
            vb.createProductDescriptionArabic.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createProductDescription.getError() != null || vb.createProductDescription.getEditText().getText().length() == 0) {
            vb.createProductDescription.requestFocus();
            vb.createProductDescription.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if(action != ACTION_EDIT_PRODUCT) {
            if (vb.createProductBrand.getError() != null || vb.createProductBrand.getEditText().getText().length() == 0) {
                vb.createProductBrand.requestFocus();
                vb.createProductBrand.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                validData = false;
            }

            if (vb.createProductCategoryArabic.getError() != null || vb.createProductCategoryArabic.getEditText().getText().length() == 0) {
                vb.createProductCategoryArabic.requestFocus();
                vb.createProductCategoryArabic.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                validData = false;
            }

            if (vb.createProductCategory.getError() != null || vb.createProductCategory.getEditText().getText().length() == 0) {
                vb.createProductCategory.requestFocus();
                vb.createProductCategory.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                validData = false;
            }

            if (image3 == null) {
                vb.createProductScroll.smoothScrollTo(0, vb.createProductImage3.getTop());
                vb.createProductImage3.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                Toast.makeText(getContext(), getString(R.string.Image_is_Required), Toast.LENGTH_SHORT).show();
            }

            if (image2 == null) {
                vb.createProductScroll.smoothScrollTo(0, vb.createProductImage2.getTop());
                vb.createProductImage2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                Toast.makeText(getContext(), getString(R.string.Image_is_Required), Toast.LENGTH_SHORT).show();
            }

            if (image1 == null) {
                vb.createProductScroll.smoothScrollTo(0, vb.createProductImage1.getTop());
                vb.createProductImage1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                Toast.makeText(getContext(), getString(R.string.Image_is_Required), Toast.LENGTH_SHORT).show();
            }
        }

        if (vb.createProductPrice.getError() != null || vb.createProductPrice.getEditText().getText().length() == 0) {
            vb.createProductPrice.requestFocus();
            vb.createProductPrice.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createProductNameArabic.getError() != null || vb.createProductNameArabic.getEditText().getText().length() == 0) {
            vb.createProductNameArabic.requestFocus();
            vb.createProductNameArabic.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createProductName.getError() != null || vb.createProductName.getEditText().getText().length() == 0) {
            vb.createProductName.requestFocus();
            vb.createProductName.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void createProduct() {
        DialogsProvider.get(getActivity()).setLoading(true);

        ArrayList<String> images = new ArrayList<>();

        images.add(ImageEncoder.get().encode(getContext(),image1));
        images.add(ImageEncoder.get().encode(getContext(),image2));
        images.add(ImageEncoder.get().encode(getContext(),image3));

        viewModel.createProduct(
                storeId,
                vb.createProductName.getEditText().getText().toString(),
                vb.createProductNameArabic.getEditText().getText().toString(),
                Double.parseDouble(vb.createProductPrice.getEditText().getText().toString()),
                vb.createProductSaleSlider.getProgress(),
                vb.createProductCategory.getEditText().getText().toString(),
                vb.createProductCategoryArabic.getEditText().getText().toString(),
                vb.createProductBrand.getEditText().getText().toString(),
                vb.createProductDescription.getEditText().getText().toString(),
                vb.createProductDescriptionArabic.getEditText().getText().toString(),
                images
        ).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_CREATION:
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", productData.getStoreId());
                    navController.navigate(R.id.action_createProductFragment_to_dashboardFragment, bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

            viewModel.removeObserverCreateProduct(getViewLifecycleOwner());
        });

    }

    void updateProduct(){
        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.updateProduct(
                storeId,
                productData.getId(),
                vb.createProductName.getEditText().getText().toString(),
                vb.createProductNameArabic.getEditText().getText().toString(),
                Double.parseDouble(vb.createProductPrice.getEditText().getText().toString()),
                vb.createProductSaleSlider.getProgress(),
                vb.createProductDescription.getEditText().getText().toString(),
                vb.createProductDescriptionArabic.getEditText().getText().toString()
        ).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", productData.getStoreId());
                    navController.navigate(R.id.action_createProductFragment_to_dashboardFragment, bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

            viewModel.removeObserverUpdateProduct(getViewLifecycleOwner());
        });
    }

    void deleteProduct(){
        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.deleteProduct(storeId, productData.getId()).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()){
                case BaseResponseModel.SUCCESSFUL_DELETED:
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", productData.getStoreId());
                    navController.navigate(R.id.action_createProductFragment_to_dashboardFragment, bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error),getString(R.string.Code)+ response.code());
            }

            viewModel.removeObserverDeleteProduct(getViewLifecycleOwner());
        });
    }

    void renderProductData(){
        vb.createProductName.getEditText().setText(productData.getName());
        vb.createProductNameArabic.getEditText().setText(productData.getNameArabic());
        vb.createProductPrice.getEditText().setText(productData.getOriginalPrice()+"");
        vb.createProductSaleSlider.setProgress(productData.getSalePercent());
        vb.createProductDescription.getEditText().setText(productData.getDescription());
        vb.createProductNameArabic.getEditText().setText(productData.getDescriptionArabic());

        vb.createProductPhaseLayout.setVisibility(View.GONE);

        vb.createProductCategory.setVisibility(View.GONE);
        vb.createProductCategoryArabic.setVisibility(View.GONE);
        vb.createProductBrand.setVisibility(View.GONE);
        vb.createProductProductImagesTitle.setVisibility(View.GONE);
        vb.createProductImage1.setVisibility(View.GONE);
        vb.createProductImage2.setVisibility(View.GONE);
        vb.createProductImage3.setVisibility(View.GONE);

        vb.createProductDeleteProduct.setVisibility(View.VISIBLE);
    }
}