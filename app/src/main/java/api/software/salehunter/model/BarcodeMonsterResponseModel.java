package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class BarcodeMonsterResponseModel {
    @SerializedName("code")
    private String code;

    @SerializedName("description")
    private String productName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
