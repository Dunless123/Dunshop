package com.dundunteam.pojo.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class goods implements Serializable {

    //新旧
    private int abradability;
    //分类Id
    private int categoryId;
    //简洁
    private String description;
    private int id;
    private  String images;
    private String name;
    private int price;
    private String savePlace;
    private String saveTime;

    private Integer status;
    private String create_Time;

    public void setAbradability(int abradability) {
        this.abradability = abradability;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSavePlace(String savePlace) {
        this.savePlace = savePlace;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCreate_Time(String create_Time) {
        this.create_Time = create_Time;
    }

    public int getAbradability() {
        return abradability;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getImages() {
        return images;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getSavePlace() {
        return savePlace;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public Integer getStatus() {
        return status;
    }

    public String getCreate_Time() {
        return create_Time;
    }
}
