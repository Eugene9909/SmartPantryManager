package com.example.smartpantrymanager.model;

public class PantryItem {

    private long id;
    private String name; // Normalized, e.g. "tomato"
    private String displayName; // As typed by the user e.g "tomatoes"
    private double quantity;
    private String unit; // E.g "g, ml, unit"
    private String expiryDate; // ISO 8601 (yyyy-MM-dd), nullable

    public PantryItem(){

    }
    public PantryItem(long id, String name, String displayName, double quantity, String unit, String expiryDate){
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
    // Convenience constructor creating a new item before it has an id
    public PantryItem(String name, String displayName, double quantity, String unit, String expiryDate){
        this(-1, name, displayName, quantity, unit, expiryDate);

    }
    public long id(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String name (){
        return name;
    }
    public void setName (String name){
        this.name = name;
    }
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
@Override
    public String toString(){
        return displayName + " (" +quantity + " " + unit + ")";
}

}
