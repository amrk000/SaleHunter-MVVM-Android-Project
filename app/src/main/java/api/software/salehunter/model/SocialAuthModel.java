package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class SocialAuthModel {
    public static final int STATUS_SUCCESSFUL_SIGN_IN = 200;
    public static final int STATUS_SUCCESSFUL_SIGN_UP = 201;
    public static final int STATUS_FAILED_SIGN_UP = 400;
    public static final int STATUS_FAILED_SIGN_IN = 401;

    @SerializedName("fullname")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("access_token")
    private String accessToken;


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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
