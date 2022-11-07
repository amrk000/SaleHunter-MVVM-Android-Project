package amrk000.salehunter.model;

import com.google.gson.annotations.SerializedName;

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
