package com.shtibel.truckies.generalClasses;

/**
 * Created by Shtibel on 14/07/2016.
 */
public class JobStatuses {

    long id;
    String name;

    public JobStatuses(long id, String name){
        this.id=id;
        this.name=name;
    }
    public JobStatuses(){

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
