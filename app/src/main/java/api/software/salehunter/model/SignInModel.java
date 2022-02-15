package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class SignInModel {
    public static final int STATUS_SUCCESSFUL = 200;
    public static final int STATUS_FAILED_NO_USER_FOUND = 404;
    public static final int STATUS_WRONG_PASSWORD = 400;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
