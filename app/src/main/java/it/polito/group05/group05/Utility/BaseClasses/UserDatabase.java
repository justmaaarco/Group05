package it.polito.group05.group05.Utility.BaseClasses;


import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.group05.group05.Utility.Interfaces.Namable;

public class UserDatabase implements Namable{

    public String id;
    public String name;
    public String authKey;
    public String telNumber;
    public String email;
    public String iProfile;
    public Balance balance;
    public Map<String, Object> userGroups;

    public UserDatabase(String id, String name, String authKey, String telNumber, String email, String iProfile) {
        this.id = id;
        this.name = name;
        this.authKey = authKey;
        this.telNumber = telNumber;
        this.email = email;
        this.iProfile = iProfile;
    }

    public UserDatabase(String id, String name, String authKey, String telNumber, String email) {
        this.id = id;
        this.name = name;
        this.authKey = authKey;
        this.telNumber = telNumber;
        this.email = email;
    }

    public UserDatabase() {}

    public UserDatabase(UserDatabase ud) {
        this.id = ud.id;
        this.name = ud.name;
        this.authKey = ud.authKey;
        this.telNumber = ud.telNumber;
        this.email = ud.email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return iProfile;
    }

    public void setProfileImage(String profileImmage) {
        this.iProfile = profileImmage;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public Map<String, Object> getUserGroups() {
        return userGroups;
    }

    public List<String> UserGruopsList() {


        return new ArrayList<>(userGroups.keySet());

    }

    public String getiProfile() {
        return iProfile;
    }

    public void setiProfile(String iProfile) {
        this.iProfile = iProfile;
    }

    public void setUserGroups(Map<String, Object> userGroups) {
        this.userGroups = userGroups;
    }

}