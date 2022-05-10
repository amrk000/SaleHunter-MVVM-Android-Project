package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("id")
    private String id;

    @SerializedName("fullname")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("profile_img")
    private String image;

    @SerializedName("last_seen")
    private String lastSeen;

    @SerializedName("accountType")
    private int accountType;

    public static final int ACCOUNT_TYPE_EMAIL = 0;
    public static final int ACCOUNT_TYPE_GOOGLE = 1;
    public static final int ACCOUNT_TYPE_FACEBOOK = 2;

    public UserModel(){
        id = "id";
        fullName = "fullName";
        email = "email@email.com";
        image = "";
        lastSeen = "";
        accountType = ACCOUNT_TYPE_EMAIL;
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

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }
}
