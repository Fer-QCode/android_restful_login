package com.fujitsu.tmmin.central.id.common.domain;

/**
 * Created by Ibnu on 25/04/2016.
 */
public class Message {
    private boolean status;
    private String content;
    private String content_cd;
    private int amountPart;

    public int getAmountPart() {
        return amountPart;
    }

    public void setAmountPart(int amountPart) {
        this.amountPart = amountPart;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent_cd() {
        return content_cd;
    }

    public void setContent_cd(String content_cd) {
        this.content_cd = content_cd;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
