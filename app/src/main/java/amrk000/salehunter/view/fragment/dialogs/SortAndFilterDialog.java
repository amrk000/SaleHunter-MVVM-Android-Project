package amrk000.salehunter.view.fragment.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;

import java.util.HashSet;

import amrk000.salehunter.R;
import amrk000.salehunter.databinding.FragmentSortAndFilterDialogBinding;
import amrk000.salehunter.model.SortAndFilterModel;

public class SortAndFilterDialog extends BottomSheetDialogFragment {
    private FragmentSortAndFilterDialogBinding vb;

    private SortAndFilterModel sortAndFilterModel;
    private HashSet<String> categories, brands;

    SortAndFilterDialog.DialogResultListener dialogResultListener;

    public SortAndFilterDialog() {
        // Required empty public constructor
        sortAndFilterModel = new SortAndFilterModel();
    }

    public interface DialogResultListener{
        void onApply(SortAndFilterModel sortAndFilterModel);
    }

    public void setDialogResultListener(SortAndFilterDialog.DialogResultListener dialogResultListener){
        this.dialogResultListener = dialogResultListener;
    }

    public void setSortAndFilterModel(SortAndFilterModel sortAndFilterModel) {
        this.sortAndFilterModel = sortAndFilterModel;
    }

    public void setCategories(HashSet<String> categories){
        this.categories = categories;
    }

    public void setBrands(HashSet<String> brands){
        this.brands = brands;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentSortAndFilterDialogBinding.inflate(inflater,container,false);
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

        renderData(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vb.sortAndFilterDialogReset.setOnClickListener(button ->{
            sortAndFilterModel = new SortAndFilterModel();
            renderData(true);
        });

        vb.sortAndFilterDialogApplyFilter.setOnClickListener(button ->{
            updateSortAndFilterModel();
            dialogResultListener.onApply(sortAndFilterModel);
            dismiss();
        });

    }

    void renderData(boolean reset){
        //Sorting List
       switch (sortAndFilterModel.getSortBy()){

           case SortAndFilterModel.SORT_PRICE_ASC:
               vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_priceAsc);
               break;

           case SortAndFilterModel.SORT_PRICE_DSC:
               vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_priceDsc);
               break;

           case SortAndFilterModel.SORT_RATING:
               vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_rating);
               break;

           case SortAndFilterModel.SORT_NEWEST:
               vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_updateDateNewest);
               break;

           case SortAndFilterModel.SORT_BEST_DEAL:
               vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_bestDeal);
               break;

           case SortAndFilterModel.SORT_NEAREST_STORE:
               vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_nearestStore);
               break;

           default:
               vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_popularity);
       }

        //Price Section
        if(sortAndFilterModel.getMinPrice()>SortAndFilterModel.PRICE_MIN) vb.sortAndFilterDialogMinPrice.setText(String.valueOf(sortAndFilterModel.getMinPrice()));
        else vb.sortAndFilterDialogMinPrice.setText("");
        if(sortAndFilterModel.getMaxPrice()<SortAndFilterModel.PRICE_MAX) vb.sortAndFilterDialogMaxPrice.setText(String.valueOf(sortAndFilterModel.getMaxPrice()));
        else vb.sortAndFilterDialogMaxPrice.setText("");

        vb.sortAndFilterDialogMinPrice.clearFocus();
        vb.sortAndFilterDialogMaxPrice.clearFocus();

        //Category Group
        if(reset) {
            vb.sortAndFilterDialogCategoryGroup.removeViews(1, categories.size());
            vb.sortAndFilterDialogChipAllCategories.setChecked(true);
        }
        for(String category : categories){

            Chip chip = new Chip(getContext());
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null,0, R.style.Widget_MaterialComponents_Chip_Choice);
            chip.setChipDrawable(chipDrawable);
            chip.setCheckable(true);
            chip.setText(category);
            chip.setCheckedIconVisible(true);
            chip.setCheckedIconTintResource(R.color.lightModesecondary);

            vb.sortAndFilterDialogCategoryGroup.addView(chip);

            if(category.equals(sortAndFilterModel.getCategory())) chip.setChecked(true);
        }

        //Brand Group
        if(reset) {
            vb.sortAndFilterDialogBrandGroup.removeViews(1, brands.size());
            vb.sortAndFilterDialogChipAllBrands.setChecked(true);
        }
        for(String brand : brands){

            Chip chip = new Chip(getContext());
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null,0, R.style.Widget_MaterialComponents_Chip_Choice);
            chip.setChipDrawable(chipDrawable);
            chip.setCheckable(true);
            chip.setText(brand);
            chip.setCheckedIconVisible(true);
            chip.setCheckedIconTintResource(R.color.lightModesecondary);

            vb.sortAndFilterDialogBrandGroup.addView(chip);

            if(brand.equals(sortAndFilterModel.getBrand())) chip.setChecked(true);
        }
    }

    void updateSortAndFilterModel(){
        //Sorting
        switch (vb.sortAndFilterDialogSortGroup.getCheckedRadioButtonId()){
            case R.id.sortAndFilterDialog_sort_popularity:
                sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_POPULARITY);
                break;

            case R.id.sortAndFilterDialog_sort_priceAsc:
                sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_PRICE_ASC);
                break;

            case R.id.sortAndFilterDialog_sort_priceDsc:
                sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_PRICE_DSC);
                break;

            case R.id.sortAndFilterDialog_sort_rating:
                sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_RATING);
                break;

            case R.id.sortAndFilterDialog_sort_updateDateNewest:
                sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_NEWEST);
                break;

            case R.id.sortAndFilterDialog_sort_bestDeal:
                sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_BEST_DEAL);
                break;

            case R.id.sortAndFilterDialog_sort_nearestStore:
                sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_NEAREST_STORE);
                break;
        }

        //Price
        if(vb.sortAndFilterDialogMinPrice.getText().toString().isEmpty()) sortAndFilterModel.setMinPrice(SortAndFilterModel.PRICE_MIN);
        else sortAndFilterModel.setMinPrice(Long.valueOf(vb.sortAndFilterDialogMinPrice.getText().toString()));

        if(vb.sortAndFilterDialogMaxPrice.getText().toString().isEmpty()) sortAndFilterModel.setMaxPrice(SortAndFilterModel.PRICE_MAX);
        else sortAndFilterModel.setMaxPrice(Long.valueOf(vb.sortAndFilterDialogMaxPrice.getText().toString()));

        //Category
        Chip categoryChip = vb.sortAndFilterDialogCategoryGroup.findViewById(vb.sortAndFilterDialogCategoryGroup.getCheckedChipId());
        if(categoryChip.getText().equals(vb.sortAndFilterDialogChipAllCategories.getText())) sortAndFilterModel.setCategory(SortAndFilterModel.CATEGORY_ALL);
        else sortAndFilterModel.setCategory(categoryChip.getText().toString());

        //Brand
        Chip brandChip = vb.sortAndFilterDialogBrandGroup.findViewById(vb.sortAndFilterDialogBrandGroup.getCheckedChipId());
        if(brandChip.getText().equals(vb.sortAndFilterDialogChipAllBrands.getText())) sortAndFilterModel.setBrand(SortAndFilterModel.BRAND_ALL);
        else sortAndFilterModel.setBrand(brandChip.getText().toString());
    }
}