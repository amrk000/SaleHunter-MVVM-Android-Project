package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordModel {
    public static final int STATUS_SUCCESSFUL = 200;
    public static final int STATUS_FAILED_TOKEN_EXPIRED = 400;
    public static final int STATUS_FAILED_TOKEN_INVALID = 404;

    @SerializedName("password")
    private String password;

    @SerializedName("passwordConfirm")
    private String passwordConfirm;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
