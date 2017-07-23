package com.shtibel.truckies.activities.job;

/**
 * Created by Shtibel on 19/02/2017.
 */
public class LoadType {

    long id;
    String name;
    int isDefault;
    int quantity=0;
    boolean isSelected=false;

    public LoadType(long id,String name,int isDefault){
        this.id=id;
        this.name=name;
        this.isDefault=isDefault;
    }
    public LoadType(long id,String name,int isDefault,int quantity){
        this.id=id;
        this.name=name;
        this.isDefault=isDefault;
        this.quantity=quantity;
    }

    public LoadType(){}

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Load type: "+name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
