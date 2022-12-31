package com.example.shlok_mehta_project2;

public class ProductData {
    private String ID;
    private String title;
    private String image;
    private String price;
    private String description;
    private int quantity;

    public ProductData(String ID, String title, String image, String price, String description, int quantity) {
        this.ID = ID;
        this.title = title;
        this.image = image;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }

    public ProductData() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
