package com.example.biddutkumar.firebaseimagestore;

import com.google.firebase.database.Exclude;

public class Upload {

    private String imageName;
    private String imageUri;

    private String key;

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public Upload()
    {

    }

    public Upload(String imageName, String imageUri) {

        this.imageName=imageName;
        this.imageUri=imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
