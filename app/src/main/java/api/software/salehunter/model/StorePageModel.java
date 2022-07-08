package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StorePageModel extends BaseResponseModel {
    @SerializedName("store")
    private StoreModel store;
    @SerializedName("productsLength")
    private int productsCount;
    @SerializedName("products")
    private ArrayList<ProductModel> products;

    public StoreModel getStore() {
        return store;
    }

    public void setStore(StoreModel store) {
        this.store = store;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }

    public ArrayList<ProductModel> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductModel> products) {
        this.products = products;
    }
}
