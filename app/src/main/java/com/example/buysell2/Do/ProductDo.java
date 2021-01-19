package com.example.buysell2.Do;

import java.io.Serializable;

public class ProductDo implements Serializable {

    String name;
    int image;

    public ProductDo(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
