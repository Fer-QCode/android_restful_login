package com.fujitsu.tmmin.central.domain;

import java.util.List;

/**
 * Created by Ibnu on 25/04/2016.
 */
public class ConfirmedResultMap {
    private boolean status;
    private String content;
    private List<SparePart> result;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<SparePart> getResult() {
        return result;
    }

    public void setResult(List<SparePart> result) {
        this.result = result;
    }
}
