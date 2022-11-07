package amrk000.salehunter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CreateProductRequestModel{
    @SerializedName("title")
    private String name;

    @SerializedName("title_ar")
    private String nameArabic;

    @SerializedName("sale")
    private int sale;

    @SerializedName("description")
    private String description;

    @SerializedName("description_ar")
    private String descriptionArabic;

    @SerializedName("price")
    private Double price;

    @SerializedName("brand")
    private String brand;

    @SerializedName("category")
    private String category;

    @SerializedName("category_ar")
    private String categoryArabic;

    @SerializedName("product_images")
    private ArrayList<String> images;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameArabic() {
        return nameArabic;
    }

    public void setNameArabic(String nameArabic) {
        this.nameArabic = nameArabic;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionArabic() {
        return descriptionArabic;
    }

    public void setDescriptionArabic(String descriptionArabic) {
        this.descriptionArabic = descriptionArabic;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryArabic() {
        return categoryArabic;
    }

    public void setCategoryArabic(String categoryArabic) {
        this.categoryArabic = categoryArabic;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
