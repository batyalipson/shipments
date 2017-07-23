package com.shtibel.truckies.generalClasses;

import java.io.Serializable;

/**
 * Created by Shtibel on 15/09/2016.
 */
public class SpecialRequest implements Serializable{

    String imageResource;
    String imageLoafFile;

    public SpecialRequest(String imageResource,String imageLoafFile){
        this.imageResource=imageResource;
        this.imageLoafFile=imageLoafFile;
    }

    public SpecialRequest(){

    }
    public String getImageLoafFile() {
        return imageLoafFile;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageLoafFile(String imageLoafFile) {
        this.imageLoafFile = imageLoafFile;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }
}
