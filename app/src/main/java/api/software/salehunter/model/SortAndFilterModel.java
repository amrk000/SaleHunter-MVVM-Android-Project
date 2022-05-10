package api.software.salehunter.model;

import java.text.DecimalFormat;

public class SortAndFilterModel {
    //Sorting
    private String sortBy;

    public static final String SORT_POPULARITY = "popular";
    public static final String SORT_PRICE_ASC = "priceAsc";
    public static final String SORT_PRICE_DSC = "priceDsc";
    public static final String SORT_RATING = "rating";
    public static final String SORT_NEAREST_STORE = "nearest_store";
    public static final String SORT_BEST_DEAL = "best_deal";
    public static final String SORT_NEWEST = "newest";
    public static final String SORT_OLDEST = "oldest";

    //Filtering
    private long minPrice, maxPrice;
    private String category;
    private String brand;

    public static final long PRICE_MIN = 0;
    public static final long PRICE_MAX = 1000000;
    public static final String CATEGORY_ALL = "all";
    public static final String BRAND_ALL = "all";

    public SortAndFilterModel() {
        sortBy = SORT_POPULARITY;
        minPrice = PRICE_MIN;
        maxPrice = PRICE_MAX;
        category = CATEGORY_ALL;
        brand = BRAND_ALL;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(long minPrice) {
        this.minPrice = minPrice;
    }

    public long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
