package com.example.shoppe.models;

public class Users {
    private String email;
    private String name;
    private String dob;
    private String password;
    private String userType;
    private String uid;
    private String phoneCode;
    private String phone;
    private String phoneNumber;
    private String profileImageUrl;
    private boolean onlineStatus;

    public Users() {
    }

    public Users(String email, String name, String dob, String password, String userType, String uid, String phoneCode, String phone, String phoneNumber, String profileImageUrl, boolean onlineStatus) {
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.password = password;
        this.userType = userType;
        this.uid = uid;
        this.phoneCode = phoneCode;
        this.phone = phone;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.onlineStatus = onlineStatus;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
