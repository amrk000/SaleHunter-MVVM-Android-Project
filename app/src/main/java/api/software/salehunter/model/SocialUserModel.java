package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class SocialUserModel {

    @SerializedName("id")
    private String id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("profile_img")
    private String image;

    @SerializedName("thirdParty_id")
    private String thirdPartId;

    public SocialUserModel(){
        id = "id";
        fullName = "fullName";
        email = "";
        image = "";
        thirdPartId = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getImageLink() {
        if(image.contains("http:")) image = image.replace("http:","https:");
        return image;
    }

    public String getEncodedImage() {
        if(image.contains("http")) return "";
        return image;
    }

    public void setEncodedImage(String image) {
        this.image = "data:image/webp;base64,"+image;
    }

    public void setImageUrl(String imageUrl) {
        this.image = imageUrl;
    }

    public String getThirdPartId() {
        return thirdPartId;
    }

    public void setThirdPartId(String thirdPartId) {
        this.thirdPartId = thirdPartId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
