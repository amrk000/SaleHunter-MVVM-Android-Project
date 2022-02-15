package api.software.salehunter.model;

public class ProductModel {
    private String name;
    private String imageUrl;
    private String price;
    private String store;
    private boolean favourite;
    private String brand;
    private float rate;

    //Setters
    public ProductModel setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public ProductModel setFavourite(boolean favourite) {
        this.favourite = favourite;
        return this;
    }

    public ProductModel setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public ProductModel setName(String name) {
        this.name = name;
        return this;
    }

    public ProductModel setPrice(String price) {
        this.price = price;
        return this;
    }

    public ProductModel setRate(float rate) {
        this.rate = rate;
        return this;
    }

    public ProductModel setStore(String store) {
        this.store = store;
        return this;
    }

    //Getters
    public float getRate() {
        return rate;
    }

    public String getBrand() {
        return brand;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getStore() {
        return store;
    }

    public boolean isFavourite() {
        return favourite;
    }
}
