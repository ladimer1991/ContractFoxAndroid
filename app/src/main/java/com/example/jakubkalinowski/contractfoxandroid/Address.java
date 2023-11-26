package com.example.jakubkalinowski.contractfoxandroid;
//hi
public class Address {
    private String mStreetAddress;
    private String mCity;
    private String mState;
    private String mZipCode;
    private String mUnit_Apt_no;


    public Address(){}

    public Address(String streetAddress, String city, String state, String zipCode, String unit_Apt_no) {
        mStreetAddress = streetAddress;
        mCity = city;
        mState = state;
        mZipCode = zipCode;
        mUnit_Apt_no = unit_Apt_no;
    }

    //change the abstract methods to public

    public String getStreetAddress() {
        return mStreetAddress;
    }
    public void setStreetAddress(String streetAddress) {
        mStreetAddress = streetAddress;
    }
    public String getCity() {
        return mCity;
    }
    public void setCity(String city) {
        mCity = city;
    }
    public String getState() {
        return mState;
    }
    public void setState(String state) {
        mState = state;
    }
    public String getZipCode() {
        return mZipCode;
    }
    public void setZipCode(String zipCode) {
        mZipCode = zipCode;
    }
    public String getUnit_Apt_no() {
        return mUnit_Apt_no;
    }
    public void setUnit_Apt_no(String unit_Apt_no) {
        mUnit_Apt_no = unit_Apt_no;
    }

    @Override
    public String toString() {
        return mStreetAddress+", "+mUnit_Apt_no+", "+
                mCity+", "+mState+", "+mZipCode;
    }
}

