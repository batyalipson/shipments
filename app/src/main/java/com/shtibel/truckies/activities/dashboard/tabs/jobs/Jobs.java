package com.shtibel.truckies.activities.dashboard.tabs.jobs;

import com.shtibel.truckies.generalClasses.SpecialRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shtibel on 04/07/2016.
 */
public class Jobs implements Serializable{

    long id;
    int statusId;
    String statusName;

    String originalPickupDate;
    String pickupDate;
    String pickupFromTime;
    String pickupTillTime;


    String dropoffDate;
    String dropoffFromTime;
    String dropoffTillTime;

    String originAddress;
    String destinationAddress;

    String originSpecialSiteInstructions;
    String destinationSpecialSiteInstructions;

    long totalLoadWeightText;
    String totalWeightType;
    String shipmentShipperPaymentText;
    String totalDrivingDistance;

    String circleColor;

    double originLat;
    double originLng;
    double destinationLat;
    double destinationLng;

    int numberBoxes;
    int numberPallets;
    int numberTruckload;

    //List<String> specialRequest=new ArrayList<>();
    List<SpecialRequest> specials=new ArrayList<>();

    String latLngPoints="";

    String comments;
    List<String>loads=new ArrayList<>();

    String originContactName;
    String destinationContactName;
    String originContactPhone;
    String destinationContactPhone;

    String pickupInTime;
    String dropoffInTime;

    String completedDate;

    String signatureUrlPickup;
    String receiverNamePickup;
    int totalWeightPickup;
    String confPhonePickup;
    String confDatePickup;
    String driverNamePickup;
    List<Integer> loadTypesPickup=new ArrayList<>();
    List<Integer> loadTypesQuantityPickup=new ArrayList<>();

    String signatureUrl;
    String receiverName;
    int totalWeightDropoff;
    String confPhoneDropoff;
    String confDateDropoff;
    String driverNameDropoff;
    List<Integer> loadTypesDropoff=new ArrayList<>();
    List<Integer> loadTypesQuantityDropoff=new ArrayList<>();

    String bolUrl;

    boolean isLessThreeKm=false;
    boolean isLessHundredMeters=false;

    String truckloadName;
    int truckloadMaxWeight;

    String shipperName;
    String shipperPhone;

    String driverNotes;

    float rating;
    String feedback;

    boolean isPopupShow=false;

    public Jobs(){

    }

    public int getStatusId() {
        return statusId;
    }

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

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

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

    public String getCircleColor() {
        return circleColor;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setCircleColor(String circleColor) {
        this.circleColor = circleColor;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getNumberBoxes() {
        return numberBoxes;
    }

    public int getNumberPallets() {
        return numberPallets;
    }

    public int getNumberTruckload() {
        return numberTruckload;
    }

//    public List<String> getSpecialRequest() {
//        return specialRequest;
//    }

    public void setNumberBoxes(int numberBoxes) {
        this.numberBoxes = numberBoxes;
    }

    public void setNumberPallets(int numberPallets) {
        this.numberPallets = numberPallets;
    }

    public void setNumberTruckload(int numberTruckload) {
        this.numberTruckload = numberTruckload;
    }

//    public void setSpecialRequest(List<String> specialRequest) {
//        this.specialRequest = specialRequest;
//    }

    public String getLatLngPoints() {
        return latLngPoints;
    }

    public void setLatLngPoints(String latLngPoints) {
        this.latLngPoints = latLngPoints;
    }

    public List<String> getLoads() {
        return loads;
    }

    public void setLoads(List<String> loads) {
        this.loads = loads;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDestinationContactName() {
        return destinationContactName;
    }

    public String getDestinationContactPhone() {
        return destinationContactPhone;
    }

    public String getOriginContactName() {
        return originContactName;
    }

    public String getOriginContactPhone() {
        return originContactPhone;
    }

    public void setDestinationContactName(String destinationContactName) {
        this.destinationContactName = destinationContactName;
    }

    public void setDestinationContactPhone(String destinationContactPhone) {
        this.destinationContactPhone = destinationContactPhone;
    }

    public void setOriginContactName(String originContactName) {
        this.originContactName = originContactName;
    }

    public void setOriginContactPhone(String originContactPhone) {
        this.originContactPhone = originContactPhone;
    }

    public String getDropoffInTime() {
        return dropoffInTime;
    }

    public String getPickupInTime() {
        return pickupInTime;
    }

    public void setDropoffInTime(String dropoffInTime) {
        this.dropoffInTime = dropoffInTime;
    }

    public void setPickupInTime(String pickupInTime) {
        this.pickupInTime = pickupInTime;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public String getBolUrl() {
        return bolUrl;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setBolUrl(String bolUrl) {
        this.bolUrl = bolUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public boolean isLessHundredMeters() {
        return isLessHundredMeters;
    }

    public boolean isLessThreeKm() {
        return isLessThreeKm;
    }

    public void setIsLessHundredMeters(boolean isLessHundredMeters) {
        this.isLessHundredMeters = isLessHundredMeters;
    }

    public void setIsLessThreeKm(boolean isLessThreeKm) {
        this.isLessThreeKm = isLessThreeKm;
    }

    public void setSpecials(List<SpecialRequest> specials) {
        this.specials = specials;
    }

    public List<SpecialRequest> getSpecials() {
        return specials;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getTruckloadName() {
        return truckloadName;
    }

    public void setTruckloadName(String truckloadName) {
        this.truckloadName = truckloadName;
    }

    public String getOriginalPickupDate() {
        return originalPickupDate;
    }

    public void setOriginalPickupDate(String originalPickupDate) {
        this.originalPickupDate = originalPickupDate;
    }

    public String getShipperName() {
        return shipperName;
    }

    public String getShipperPhone() {
        return shipperPhone;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public void setShipperPhone(String shipperPhone) {
        this.shipperPhone = shipperPhone;
    }

    public String getDestinationSpecialSiteInstructions() {
        return destinationSpecialSiteInstructions;
    }

    public void setDestinationSpecialSiteInstructions(String destinationSpecialSiteInstructions) {
        this.destinationSpecialSiteInstructions = destinationSpecialSiteInstructions;
    }

    public String getOriginSpecialSiteInstructions() {
        return originSpecialSiteInstructions;
    }

    public void setOriginSpecialSiteInstructions(String originSpecialSiteInstructions) {
        this.originSpecialSiteInstructions = originSpecialSiteInstructions;
    }

    public String getTotalWeightType() {
        return totalWeightType;
    }

    public void setTotalWeightType(String totalWeightType) {
        this.totalWeightType = totalWeightType;
    }

    public String getDriverNotes() {
        return driverNotes;
    }

    public void setDriverNotes(String driverNotes) {
        this.driverNotes = driverNotes;
    }

    public int getTruckloadMaxWeight() {
        return truckloadMaxWeight;
    }

    public void setTruckloadMaxWeight(int truckloadMaxWeight) {
        this.truckloadMaxWeight = truckloadMaxWeight;
    }

    public int getTotalWeightDropoff() {
        return totalWeightDropoff;
    }

    public int getTotalWeightPickup() {
        return totalWeightPickup;
    }

    public String getConfDateDropoff() {
        return confDateDropoff;
    }

    public String getConfDatePickup() {
        return confDatePickup;
    }

    public String getConfPhoneDropoff() {
        return confPhoneDropoff;
    }

    public String getConfPhonePickup() {
        return confPhonePickup;
    }

    public String getDriverNameDropoff() {
        return driverNameDropoff;
    }

    public String getDriverNamePickup() {
        return driverNamePickup;
    }

    public String getReceiverNamePickup() {
        return receiverNamePickup;
    }

    public String getSignatureUrlPickup() {
        return signatureUrlPickup;
    }

    public void setConfDateDropoff(String confDateDropoff) {
        this.confDateDropoff = confDateDropoff;
    }

    public void setConfDatePickup(String confDatePickup) {
        this.confDatePickup = confDatePickup;
    }

    public void setConfPhoneDropoff(String confPhoneDropoff) {
        this.confPhoneDropoff = confPhoneDropoff;
    }

    public void setConfPhonePickup(String confPhonePickup) {
        this.confPhonePickup = confPhonePickup;
    }

    public void setDriverNameDropoff(String driverNameDropoff) {
        this.driverNameDropoff = driverNameDropoff;
    }

    public void setDriverNamePickup(String driverNamePickup) {
        this.driverNamePickup = driverNamePickup;
    }

    public void setReceiverNamePickup(String receiverNamePickup) {
        this.receiverNamePickup = receiverNamePickup;
    }

    public void setSignatureUrlPickup(String signatureUrlPickup) {
        this.signatureUrlPickup = signatureUrlPickup;
    }

    public void setTotalWeightDropoff(int totalWeightDropoff) {
        this.totalWeightDropoff = totalWeightDropoff;
    }

    public void setTotalWeightPickup(int totalWeightPickup) {
        this.totalWeightPickup = totalWeightPickup;
    }

    public List<Integer> getLoadTypesDropoff() {
        return loadTypesDropoff;
    }

    public List<Integer> getLoadTypesPickup() {
        return loadTypesPickup;
    }

    public List<Integer> getLoadTypesQuantityDropoff() {
        return loadTypesQuantityDropoff;
    }

    public List<Integer> getLoadTypesQuantityPickup() {
        return loadTypesQuantityPickup;
    }

    public void setLoadTypesDropoff(List<Integer> loadTypesDropoff) {
        this.loadTypesDropoff = loadTypesDropoff;
    }

    public void setLoadTypesPickup(List<Integer> loadTypesPickup) {
        this.loadTypesPickup = loadTypesPickup;
    }

    public void setLoadTypesQuantityDropoff(List<Integer> loadTypesQuantityDropoff) {
        this.loadTypesQuantityDropoff = loadTypesQuantityDropoff;
    }

    public void setLoadTypesQuantityPickup(List<Integer> loadTypesQuantityPickup) {
        this.loadTypesQuantityPickup = loadTypesQuantityPickup;
    }

    public float getRating() {
        return rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isPopupShow() {
        return isPopupShow;
    }

    public void setIsPopupShow(boolean isPopupShow) {
        this.isPopupShow = isPopupShow;
    }
}

