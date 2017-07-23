package com.shtibel.truckies.generalClasses;

/**
 * Created by Shtibel on 21/08/2016.
 */
public class TruckType {

    long id;
    String name;

    public TruckType(long id, String name){
        this.id=id;
        this.name=name;
    }
    public TruckType(){

    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
