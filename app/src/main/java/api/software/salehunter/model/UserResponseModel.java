package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class UserResponseModel extends ResponseModel {
    @SerializedName("user")
    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
