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

    @SerializedName("signedInWith")
    private int signedInWith;

    @SerializedName("store_id")
    private int storeId;

    public static final int SIGNED_IN_WITH_EMAIL = 0;
    public static final int SIGNED_IN_WITH_GOOGLE = 1;
    public static final int SIGNED_IN_WITH_FACEBOOK = 2;

    public UserModel(){
        id = "id";
        fullName = "fullName";
        email = "email@email.com";
        image = "";
        lastSeen = "";
        signedInWith = SIGNED_IN_WITH_EMAIL;
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
        return image.replace("http:","https:");
    }

    public String getEncodedImage() {
        if(image.contains("http")) return "";
        return image;
    }

    public void setEncodedImage(String image) {
        this.image = image;
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

    public int getSignedInWith() {
        return signedInWith;
    }

    public void setSignedInWith(int signedInWith) {
        this.signedInWith = signedInWith;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public boolean hasStore(){
        return storeId != 0;
    }

    public String getAccountType(){
        if (hasStore()) return "Seller Account";
        else return "User Account";
    }
}
