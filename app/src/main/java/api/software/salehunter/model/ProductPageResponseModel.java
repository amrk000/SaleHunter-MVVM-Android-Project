package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductPageResponseModel extends BaseResponseModel {

    @SerializedName("product")
    private ProductPageModel product;

    public ProductPageModel getProduct() {
        return product;
    }

    public void setProduct(ProductPageModel product) {
        this.product = product;
    }
}
