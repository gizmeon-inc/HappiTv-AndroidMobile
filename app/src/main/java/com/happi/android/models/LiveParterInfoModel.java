package com.happi.android.models;

import java.io.Serializable;

public class LiveParterInfoModel implements Serializable {

    private String partnerName = "";
    private String partnerDescription = "";


    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerDescription() {
        return partnerDescription;
    }

    public void setPartnerDescription(String partnerDescription) {
        this.partnerDescription = partnerDescription;
    }
}
