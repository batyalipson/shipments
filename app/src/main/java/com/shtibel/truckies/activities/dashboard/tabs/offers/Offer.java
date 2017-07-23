package com.shtibel.truckies.activities.dashboard.tabs.offers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shtibel on 06/07/2016.
 */
public class Offer implements Serializable{

    long id;
    String statusName;

    String pickupDate;
    String pickupFromTime;
    String pickupTillTime;


    String dropoffDate;
    String dropoffFromTime;
    String dropoffTillTime;



    long totalLoadWeightText;
    String totalWeightType;
    String shipmentShipperPaymentText;
    String totalDrivingDistance;

    String circleColor;

    double originLat;
    double originLng;
    double destinationLat;
    double destinationLng;

    String originSpecialSiteInstructions;
    String destinationSpecialSiteInstructions;

    int numberBoxes;
    int numberPallets;
    int numberTruckload;

    List<String> specialRequest=new ArrayList<>();

    String latLngPoints="";
    String pickupIn;

    String originAddressName;
    String destinationAddressName;
    String originAddress;
    String destinationAddress;

    String comments;
    List<String>loads=new ArrayList<>();

    String truckloadName;

    public Offer(){

    }

//    public int getStatusId() {
//        return statusId;
//    }

    public long getId() {
        return id;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getDropoffDate() {
        return dropoffDate;
    }

    public String getDropoffFromTime() {
        return dropoffFromTime;
    }

    public String getDropoffTillTime() {
        return dropoffTillTime;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getPickupFromTime() {
        return pickupFromTime;
    }

    public String getPickupTillTime() {
        return pickupTillTime;
    }

    public String getShipmentShipperPaymentText() {
        return shipmentShipperPaymentText;
    }

    public long getTotalLoadWeightText() {
        return totalLoadWeightText;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setDropoffDate(String dropoffDate) {
        this.dropoffDate = dropoffDate;
    }

    public void setDropoffFromTime(String dropoffFromTime) {
        this.dropoffFromTime = dropoffFromTime;
    }

    public void setDropoffTillTime(String dropoffTillTime) {
        this.dropoffTillTime = dropoffTillTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setPickupFromTime(String pickupFromTime) {
        this.pickupFromTime = pickupFromTime;
    }

    public void setPickupTillTime(String pickupTillTime) {
        this.pickupTillTime = pickupTillTime;
    }

    public void setShipmentShipperPaymentText(String shipmentShipperPaymentText) {
        this.shipmentShipperPaymentText = shipmentShipperPaymentText;
    }

//    public void setStatusId(int statusId) {
//        this.statusId = statusId;
//    }

    public void setTotalDrivingDistance(String totalDrivingDistance) {
        this.totalDrivingDistance = totalDrivingDistance;
    }

    public void setTotalLoadWeightText(long totalLoadWeightText) {
        this.totalLoadWeightText = totalLoadWeightText;
    }

    public String getTotalDrivingDistance() {
        return totalDrivingDistance;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public double getOriginLat() {
        return originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public String getLatLngPoints() {
        return latLngPoints;
    }

    public List<String> getSpecialRequest() {
        return specialRequest;
    }

    public int getNumberTruckload() {
        return numberTruckload;
    }

    public int getNumberPallets() {
        return numberPallets;
    }

    public int getNumberBoxes() {
        return numberBoxes;
    }

    public String getCircleColor() {
        return circleColor;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setLatLngPoints(String latLngPoints) {
        this.latLngPoints = latLngPoints;
    }

    public void setSpecialRequest(List<String> specialRequest) {
        this.specialRequest = specialRequest;
    }

    public void setNumberTruckload(int numberTruckload) {
        this.numberTruckload = numberTruckload;
    }

    public void setNumberPallets(int numberPallets) {
        this.numberPallets = numberPallets;
    }

    public void setNumberBoxes(int numberBoxes) {
        this.numberBoxes = numberBoxes;
    }

    public void setCircleColor(String circleColor) {
        this.circleColor = circleColor;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPickupIn() {
        return pickupIn;
    }

    public void setPickupIn(String pickupIn) {
        this.pickupIn = pickupIn;
    }

    public String getDestinationAddressName() {
        return destinationAddressName;
    }

    public String getOriginAddressName() {
        return originAddressName;
    }

    public void setDestinationAddressName(String destinationAddressName) {
        this.destinationAddressName = destinationAddressName;
    }

    public void setOriginAddressName(String originAddressName) {
        this.originAddressName = originAddressName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<String> getLoads() {
        return loads;
    }

    public void setLoads(List<String> loads) {
        this.loads = loads;
    }

    public String getTruckloadName() {
        return truckloadName;
    }

    public void setTruckloadName(String truckloadName) {
        this.truckloadName = truckloadName;
    }

    public String getTotalWeightType() {
        return totalWeightType;
    }

    public void setTotalWeightType(String totalWeightType) {
        this.totalWeightType = totalWeightType;
    }

    public String getOriginSpecialSiteInstructions() {
        return originSpecialSiteInstructions;
    }

    public String getDestinationSpecialSiteInstructions() {
        return destinationSpecialSiteInstructions;
    }

    public void setOriginSpecialSiteInstructions(String originSpecialSiteInstructions) {
        this.originSpecialSiteInstructions = originSpecialSiteInstructions;
    }

    public void setDestinationSpecialSiteInstructions(String destinationSpecialSiteInstructions) {
        this.destinationSpecialSiteInstructions = destinationSpecialSiteInstructions;
    }
}
