package com.bizhan.auarai.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.ArrayList;

public class ClothingItem implements Parcelable {
    private String _id;
    private String name;
    private String brand;
    private String category;
    private String gender;
    private List<String> color;
    private List<String> size;
    private String material;
    private String description;
    private String imageURL;
    private String storeName;
    private String storeURL;
    private String productURL;
    private double price;
    private List<String> tags;
    private List<String> occasions;
    private List<String> weatherSuitability;
    private List<Double> aiGeneratedStyleEmbedding;
    private boolean available;
    private String updatedAt;

    public ClothingItem() {}

    public ClothingItem(String _id, String name, String brand, String category, String gender,
                        List<String> color, List<String> size, String material, String description,
                        String imageURL, String storeName, String storeURL, String productURL,
                        double price, List<String> tags, List<String> occasions,
                        List<String> weatherSuitability, List<Double> aiGeneratedStyleEmbedding,
                        boolean available, String updatedAt) {
        this._id = _id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.gender = gender;
        this.color = color;
        this.size = size;
        this.material = material;
        this.description = description;
        this.imageURL = imageURL;
        this.storeName = storeName;
        this.storeURL = storeURL;
        this.productURL = productURL;
        this.price = price;
        this.tags = tags;
        this.occasions = occasions;
        this.weatherSuitability = weatherSuitability;
        this.aiGeneratedStyleEmbedding = aiGeneratedStyleEmbedding;
        this.available = available;
        this.updatedAt = updatedAt;
    }

    protected ClothingItem(Parcel in) {
        _id = in.readString();
        name = in.readString();
        brand = in.readString();
        category = in.readString();
        gender = in.readString();
        color = in.createStringArrayList();
        size = in.createStringArrayList();
        material = in.readString();
        description = in.readString();
        imageURL = in.readString();
        storeName = in.readString();
        storeURL = in.readString();
        productURL = in.readString();
        price = in.readDouble();
        tags = in.createStringArrayList();
        occasions = in.createStringArrayList();
        weatherSuitability = in.createStringArrayList();
        aiGeneratedStyleEmbedding = new ArrayList<>();
        in.readList(aiGeneratedStyleEmbedding, Double.class.getClassLoader());
        available = in.readByte() != 0;
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(category);
        dest.writeString(gender);
        dest.writeStringList(color);
        dest.writeStringList(size);
        dest.writeString(material);
        dest.writeString(description);
        dest.writeString(imageURL);
        dest.writeString(storeName);
        dest.writeString(storeURL);
        dest.writeString(productURL);
        dest.writeDouble(price);
        dest.writeStringList(tags);
        dest.writeStringList(occasions);
        dest.writeStringList(weatherSuitability);
        dest.writeList(aiGeneratedStyleEmbedding);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClothingItem> CREATOR = new Creator<ClothingItem>() {
        @Override
        public ClothingItem createFromParcel(Parcel in) {
            return new ClothingItem(in);
        }

        @Override
        public ClothingItem[] newArray(int size) {
            return new ClothingItem[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
    }

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreURL() {
        return storeURL;
    }

    public void setStoreURL(String storeURL) {
        this.storeURL = storeURL;
    }

    public String getProductURL() {
        return productURL;
    }

    public void setProductURL(String productURL) {
        this.productURL = productURL;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getOccasions() {
        return occasions;
    }

    public void setOccasions(List<String> occasions) {
        this.occasions = occasions;
    }

    public List<String> getWeatherSuitability() {
        return weatherSuitability;
    }

    public void setWeatherSuitability(List<String> weatherSuitability) {
        this.weatherSuitability = weatherSuitability;
    }

    public List<Double> getAiGeneratedStyleEmbedding() {
        return aiGeneratedStyleEmbedding;
    }

    public void setAiGeneratedStyleEmbedding(List<Double> aiGeneratedStyleEmbedding) {
        this.aiGeneratedStyleEmbedding = aiGeneratedStyleEmbedding;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getColorDisplay() {
        if (color == null || color.isEmpty()) return "";
        return String.join(", ", color);
    }

    public String getSizeDisplay() {
        if (size == null || size.isEmpty()) return "";
        return String.join(", ", size);
    }

    public String getFirstColor() {
        if (color == null || color.isEmpty()) return "";
        return color.get(0);
    }

    public String getFirstSize() {
        if (size == null || size.isEmpty()) return "";
        return size.get(0);
    }
}