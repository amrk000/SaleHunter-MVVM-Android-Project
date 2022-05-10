package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class SocialAuthResponseModel extends BaseResponseModel {

    @SerializedName("user")
    private SocialUserModel user;

    public SocialUserModel getUser() {
        return user;
    }

    public void setUser(SocialUserModel user) {
        this.user = user;
    }

}
