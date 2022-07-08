package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class ProductRateModel {
    @SerializedName("rating")
    int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
