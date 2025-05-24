package com.bizhan.auarai.models;

public class Category {
    private String name;
    private String id;
    private boolean selected;
    private int iconRes;
    private int count;

    public Category() {}

    public Category(String name, String id, boolean selected) {
        this.name = name;
        this.id = id;
        this.selected = selected;
        this.count = 0;
    }

    public Category(String name, String id, boolean selected, int iconRes) {
        this.name = name;
        this.id = id;
        this.selected = selected;
        this.iconRes = iconRes;
        this.count = 0;
    }

    public Category(String name, String id, boolean selected, int iconRes, int count) {
        this.name = name;
        this.id = id;
        this.selected = selected;
        this.iconRes = iconRes;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return id != null ? id.equals(category.id) : category.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", selected=" + selected +
                ", count=" + count +
                '}';
    }
}