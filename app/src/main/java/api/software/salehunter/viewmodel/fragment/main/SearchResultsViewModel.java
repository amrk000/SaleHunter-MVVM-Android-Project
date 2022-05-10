package api.software.salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;

import api.software.salehunter.data.Repository;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.ProductsResponseModel;
import api.software.salehunter.model.SortAndFilterModel;
import api.software.salehunter.model.UserModel;
import api.software.salehunter.model.UserResponseModel;
import api.software.salehunter.util.SharedPrefManager;
import api.software.salehunter.util.UserAccountManager;
import retrofit2.Response;

public class SearchResultsViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Response<ProductsResponseModel>> initialLoadedProducts;
    private LiveData<Response<ProductsResponseModel>> onlinePaginatedProducts;
    private LiveData<Response<ProductsResponseModel>> localPaginatedProducts;

    private String token;
    private String language;
    private String keyword;
    private LatLng userLocation;
    private SortAndFilterModel sortAndFilterModel;
    private HashSet<String> categories, brands;
    private long cursorLastOnlineItem, cursorLastLocalItem;
    private int productsCountPerPage = 10; //Min 10

    public SearchResultsViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();

        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
        language = "en";
        userLocation = new LatLng(0,0);
        sortAndFilterModel = new SortAndFilterModel();
        categories = new HashSet<>();
        brands = new HashSet<>();
    }

    public LiveData<Response<ProductsResponseModel>> loadResults(){
        initialLoadedProducts = repository.searchProducts(token,
                language,
                userLocation.latitude,
                userLocation.longitude,
                keyword,
                "all",
                sortAndFilterModel.getSortBy(),
                sortAndFilterModel.getMinPrice(),
                sortAndFilterModel.getMaxPrice(),
                sortAndFilterModel.getCategory(),
                sortAndFilterModel.getBrand(),
                0,
                productsCountPerPage);

        return initialLoadedProducts;
    }

    public void removeObserverInitialLoadedProducts(LifecycleOwner lifecycleOwner){
        initialLoadedProducts.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<ProductsResponseModel>> loadMoreOnlineResults(){
        onlinePaginatedProducts = repository.searchProducts(token,
                language,
                userLocation.latitude,
                userLocation.longitude,
                keyword,
                "online",
                sortAndFilterModel.getSortBy(),
                sortAndFilterModel.getMinPrice(),
                sortAndFilterModel.getMaxPrice(),
                sortAndFilterModel.getCategory(),
                sortAndFilterModel.getBrand(),
                cursorLastOnlineItem,
                productsCountPerPage);

        return onlinePaginatedProducts;
    }

    public void removeObserverOnlineLoadedProducts(LifecycleOwner lifecycleOwner){
        onlinePaginatedProducts.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<ProductsResponseModel>> loadMoreLocalResults(){
        localPaginatedProducts = repository.searchProducts(token,
                language,
                userLocation.latitude,
                userLocation.longitude,
                keyword,
                "offline",
                sortAndFilterModel.getSortBy(),
                sortAndFilterModel.getMinPrice(),
                sortAndFilterModel.getMaxPrice(),
                sortAndFilterModel.getCategory(),
                sortAndFilterModel.getBrand(),
                cursorLastOnlineItem,
                productsCountPerPage);

        return localPaginatedProducts;
    }

    public void removeObserverLocalLoadedProducts(LifecycleOwner lifecycleOwner){
        onlinePaginatedProducts.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel>> addFavourite(long productId){
        return repository.addFavourite(token,productId);
    }

    public LiveData<Response<BaseResponseModel>> removeFavourite(long productId){
        return repository.removeFavourite(token,productId);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    public SortAndFilterModel getSortAndFilterModel() {
        return sortAndFilterModel;
    }

    public void setSortAndFilterModel(SortAndFilterModel sortAndFilterModel) {
        this.sortAndFilterModel = sortAndFilterModel;
    }

    public long getCursorLastOnlineItem() {
        return cursorLastOnlineItem;
    }

    public void setCursorLastOnlineItem(long cursorLastOnlineItem) {
        this.cursorLastOnlineItem = cursorLastOnlineItem;
    }

    public long getCursorLastLocalItem() {
        return cursorLastLocalItem;
    }

    public void setCursorLastLocalItem(long cursorLastLocalItem) {
        this.cursorLastLocalItem = cursorLastLocalItem;
    }

    public HashSet<String> getCategories() {
        return categories;
    }

    public void clearCategories() {
        categories.clear();
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public HashSet<String> getBrands() {
        return brands;
    }

    public void clearBrands() {
        brands.clear();
    }

    public void addBrand(String brand) {
        brands.add(brand);
    }
}
