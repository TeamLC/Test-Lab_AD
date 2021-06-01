package com.example.test_lab_ad;

import com.google.gson.annotations.SerializedName;

public class Name {

    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    public Name(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Name(String name) {
        this.name = name;
    }
}
