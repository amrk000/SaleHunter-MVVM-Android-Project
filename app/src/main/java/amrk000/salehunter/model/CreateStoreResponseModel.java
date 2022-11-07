package amrk000.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class CreateStoreResponseModel extends BaseResponseModel {
    @SerializedName("store")
    private StoreModel store;

    public StoreModel getStore() {
        return store;
    }

    public void setStore(StoreModel store) {
        this.store = store;
    }
}
