package api.software.salehunter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductPageModel {
    @SerializedName("basic")
    private MainInfo mainInfo;

    @SerializedName("is_favourite")
    private int favorite;

    @SerializedName("user_rating")
    private int userRating;

    @SerializedName("images")
    private ArrayList<ProductImage> images;

    @SerializedName("prices")
    private ArrayList<ProductPrice> prices;

    @SerializedName("rating")
    private ProductRating productRating;

    @SerializedName("views")
    private ProductViews views;

    @SerializedName("store")
    private Store store;

    public static class MainInfo{
        private final String WEBSITE_URL = "https://sale-hunter.vercel.app/";

        @SerializedName("product_id")
        private long id;

        @SerializedName("product_title")
        private String name;

        @SerializedName("product_title_ar")
        private String nameArabic;

        @SerializedName("product_description")
        private String description;

        @SerializedName("product_description_ar")
        private String descriptionArabic;

        @SerializedName("product_sale")
        private int salePercent;

        @SerializedName("product_brand")
        private String brand;

        @SerializedName("product_category")
        private String category;

        @SerializedName("product_category_ar")
        private String categoryArabic;

        @SerializedName("product_url")
        private String url;

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

        public String getSourceUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getShareableUrl(){
            return WEBSITE_URL+"pid="+id;
        }
    }

    public static class ProductImage{
        @SerializedName("img_id")
        private long imageId;

        @SerializedName("img_url")
        private String imageUrl;

        public long getImageId() {
            return imageId;
        }

        public void setImageId(long imageId) {
            this.imageId = imageId;
        }

        public String getImageUrl() {
            return imageUrl.replace("http:","https:");
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class ProductPrice{
        @SerializedName("price")
        private double price;

        @SerializedName("created_at")
        private String creationDate;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }
    }

    public static class ProductViews{
        @SerializedName("number_of_views")
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class ProductRating{
        @SerializedName("number_of_ratings")
        private int count;

        @SerializedName("rating_average")
        private String rating;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getRating() {
            if(rating==null) rating = "0.0";
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }

    public static class Store{
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
    }

    public MainInfo getMainInfo() {
        return mainInfo;
    }

    public void setMainInfo(MainInfo mainInfo) {
        this.mainInfo = mainInfo;
    }

    public void setFavorite(boolean favorite) {
        if(favorite) this.favorite = 1;
        else this.favorite = 0;
    }

    public boolean isFavorite() {
        return favorite != 0;
    }

    public ArrayList<ProductImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<ProductImage> images) {
        this.images = images;
    }

    public ArrayList<ProductPrice> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<ProductPrice> prices) {
        this.prices = prices;
    }

    public ProductRating getProductRating() {
        return productRating;
    }

    public void setProductRating(ProductRating productRating) {
        this.productRating = productRating;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public ProductViews getViews() {
        return views;
    }

    public void setViews(ProductViews views) {
        this.views = views;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }
}
