package com.bizhan.auarai.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.ArrayList;

public class UserClothingItem implements Parcelable {
    private String id;
    private String name;
    private String category;
    private List<String> color;
    private List<String> material;
    private List<String> tags;
    private List<String> occasions;
    private String imageBase64;
    private String description;
    private List<String> size;

    public UserClothingItem() {}

    public UserClothingItem(String id, String name, String category,
                            List<String> color, List<String> material,
                            List<String> tags, List<String> occasions,
                            String imageBase64, String description,
                            List<String> size) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.color = color;
        this.material = material;
        this.tags = tags;
        this.occasions = occasions;
        this.imageBase64 = imageBase64;
        this.description = description;
        this.size = size;
    }

    protected UserClothingItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        category = in.readString();
        color = in.createStringArrayList();
        material = in.createStringArrayList();
        tags = in.createStringArrayList();
        occasions = in.createStringArrayList();
        imageBase64 = in.readString();
        description = in.readString();
        size = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeStringList(color);
        dest.writeStringList(material);
        dest.writeStringList(tags);
        dest.writeStringList(occasions);
        dest.writeString(imageBase64);
        dest.writeString(description);
        dest.writeStringList(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserClothingItem> CREATOR = new Creator<UserClothingItem>() {
        @Override
        public UserClothingItem createFromParcel(Parcel in) {
            return new UserClothingItem(in);
        }

        @Override
        public UserClothingItem[] newArray(int size) {
            return new UserClothingItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
    }

    public List<String> getMaterial() {
        return material;
    }

    public void setMaterial(List<String> material) {
        this.material = material;
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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    public String getImageData() {
        return imageBase64;
    }

    public String getColorDisplay() {
        if (color == null || color.isEmpty()) return "";
        return String.join(", ", color);
    }

    public String getSizeDisplay() {
        if (size == null || size.isEmpty()) return "";
        return String.join(", ", size);
    }

    public String getMaterialDisplay() {
        if (material == null || material.isEmpty()) return "";
        return String.join(", ", material);
    }

    @Override
    public String toString() {
        return "UserClothingItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", color=" + color +
                ", material=" + material +
                ", tags=" + tags +
                ", occasions=" + occasions +
                ", description='" + description + '\'' +
                ", size=" + size +
                '}';
    }
}
