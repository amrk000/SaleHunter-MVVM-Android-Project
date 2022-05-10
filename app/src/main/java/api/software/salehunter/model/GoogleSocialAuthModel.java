package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class GoogleSocialAuthModel {

    @SerializedName("fullname")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("thirdParty_id")
    private String clientId;

    @SerializedName("profile_img")
    private String image;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
