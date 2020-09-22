package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class IPAddressModel implements Serializable {

    @SerializedName("as")
    private String as = "";
    @SerializedName("city")
    private String city = "";
    @SerializedName("continent")
    private String continent = "";
    @SerializedName("country")
    private String country = "";
    @SerializedName("countryCode")
    private String countryCode = "";
    @SerializedName("ipName")
    private String ipName = "";
    @SerializedName("ipType")
    private String ipType = "";
    @SerializedName("isp")
    private String isp = "";
    @SerializedName("lat")
    private double lat;
    @SerializedName("lon")
    private double lon;
    @SerializedName("org")
    private String org = "";
    @SerializedName("query")
    private String query = "";
    @SerializedName("region")
    private String region = "";
    @SerializedName("regionName")
    private String regionName = "";
    @SerializedName("status")
    private String status = "";
    @SerializedName("timezone")
    private String timezone = "";
    @SerializedName("zip")
    private String zip = "";
    @SerializedName("businessName")
    private String businessName = "";
    @SerializedName("businessWebsite")
    private String businessWebsite = "";

    public String getAs() {
        return as;
    }

    public String getCity() {
        return city;
    }

    public String getContinent() {
        return continent;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getIpName() {
        return ipName;
    }

    public String getIpType() {
        return ipType;
    }

    public String getIsp() {
        return isp;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getOrg() {
        return org;
    }

    public String getQuery() {
        return query;
    }

    public String getRegion() {
        return region;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getStatus() {
        return status;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getZip() {
        return zip;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getBusinessWebsite() {
        return businessWebsite;
    }
}
