package amrk000.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class EmailVerificationModel {

    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
