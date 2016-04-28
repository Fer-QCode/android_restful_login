package com.fujitsu.tmmin.central.domain;

/**
 * Created by Ibnu on 25/04/2016.
 */
public class SparePart {
    private String PartID ;
    private String StatusCD;
    private String StatusName;

    public String getPartID() {
        return PartID;
    }

    public void setPartID(String partID) {
        PartID = partID;
    }

    public String getStatusCD() {
        return StatusCD;
    }

    public void setStatusCD(String statusCD) {
        StatusCD = statusCD;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }
}
