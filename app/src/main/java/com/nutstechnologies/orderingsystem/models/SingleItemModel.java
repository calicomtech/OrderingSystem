package com.nutstechnologies.orderingsystem.models;

/**
 * Created by Adrian on 6/1/2017.
 */
public class SingleItemModel {


    private String name;
//    private String url;
    private String description;
    private String category;
    private String img;


    public SingleItemModel(String desc) {
    }

    public SingleItemModel(String name, String category, String img) {
        this.name = name;
        this.category = category;
        this.img = img;
    }


//    public String getUrl() {
//        return url;
//    }
//    public void setUrl(String url) {
//        this.url = url;
//    }
    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }
    public String getimg() {
        return img;
    }

    public void setimg(String img) {
        this.img = img;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}