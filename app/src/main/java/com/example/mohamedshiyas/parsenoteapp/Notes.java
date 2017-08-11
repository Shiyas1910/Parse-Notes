package com.example.mohamedshiyas.parsenoteapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mohamedshiyas on 08/08/17.
 */

@ParseClassName("Notes")
public class Notes extends ParseObject {

    public Notes() {

    }

    public String Id() {
        return getString("objectId");
    }

    public void setID(String id) {
        put("objectId", id);
    }

    public String Title() {
        return getString("Title");
    }

    public void setTitle(String title) {
        put("Title", title);
    }

    public String Description() {
        return getString("Description");
    }

    public void setDescription(String description) {
        put("Description", description);
    }

    public String Image() {
        return getString("Image");
    }

    public void setImage(String image) {
        put("Image", image);
    }
}
