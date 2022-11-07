package amrk000.salehunter.model;

import com.google.gson.annotations.SerializedName;

public class ProductModel {
    @SerializedName(value = "product_id",alternate = {"id"})
    private long id;

    @SerializedName(value = "product_title",alternate = {"title"})
    private String name;

    @SerializedName(value = "product_title_ar",alternate = {"title_ar"})
    private String nameArabic;

    @SerializedName(value = "product_price",alternate = {"price"})
    private double originalPrice;

    @SerializedName(value = "product_rating",alternate = {"rating"})
    private double rate;

    @SerializedName("sale")
    private int salePercent;

    @SerializedName(value = "product_brand",alternate = {"brand"})
    private String brand;

    @SerializedName(value = "product_category",alternate = {"category"})
    private String category;

    @SerializedName(value = "product_category_ar",alternate = {"category_ar"})
    private String categoryArabic;

    @SerializedName(value = "image_url",alternate = {"image"})
    private String image;

    @SerializedName("is_favourite")
    private int favorite;

    @SerializedName("store_id")
    private long storeId;

    @SerializedName("store_name")
    private String storeName;

    @SerializedName("store_type")
    private String storeType;

    @SerializedName("store_logo")
    private String storeLogo;

    @SerializedName("store_latitude")
    private Double storeLatitude;

    @SerializedName("store_longitude")
    private Double storeLongitude;

    @SerializedName(value = "viewed_at",alternate = {"favourite_date"})
    private String date;

    @SerializedName("description")
    private String description;

    @SerializedName("description_ar")
    private String descriptionArabic;

    public static final String ONLINE_STORE = "online";

    public static final String LOCAL_STORE = "local";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public double getPrice() {
        return Double.parseDouble(String.format("%.2f",originalPrice-(originalPrice*salePercent/100)));
    }

    public void setPrice(double price) {
        this.originalPrice = Double.parseDouble(String.format("%.2f",price));
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public int getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(int salePercent) {
        this.salePercent = salePercent;
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

    public String getImage() {
        if(image == null) return "";
        return image.replace("http:","https:");
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setFavorite(boolean favorite) {
        if(favorite) this.favorite = 1;
        else this.favorite = 0;
    }

    public boolean isFavorite() {
        return favorite == 1;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreLogo() {
        if(storeLogo==null) return "";
        return storeLogo.replace("http:","https:");
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public Double getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(Double storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public Double getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(Double storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
