package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordModel {
    public static final int STATUS_SUCCESSFUL = 200;
    public static final int STATUS_FAILED_NO_USER_FOUND = 404;

    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
