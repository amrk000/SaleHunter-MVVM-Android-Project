package amrk000.salehunter.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookSocialAuthModel {

    @SerializedName("fullname")
    private String fullName;

    @Expose
    private transient String email; //Ignore in serialization

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
