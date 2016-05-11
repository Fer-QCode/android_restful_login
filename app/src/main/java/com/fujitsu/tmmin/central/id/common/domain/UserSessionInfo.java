package com.fujitsu.tmmin.central.id.common.domain;

/**
 * Created by yusufya on 4/25/2016.
 */
public class UserSessionInfo {
    public static final String USER_INFO  = "userInfo";
    private String userName;
    private String terminalCode;
    private String terminalDesc;
    private String shift;
    private String roleID;
    private String content;
    private boolean status;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public String getTerminalDesc () {
        return terminalDesc;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

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
}
