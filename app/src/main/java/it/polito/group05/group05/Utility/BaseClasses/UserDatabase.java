package it.polito.group05.group05.Utility.BaseClasses;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDatabase {

    public String id;
    public String name;
    public String authKey;
    public String telNumber;
    public String email;
    public String profileImmage;
    public Balance balance;
    public Map<String, Object> userGroups;

    public UserDatabase(String id, String name, String authKey, String telNumber, String email) {
        this.id = id;
        this.name = name;
        this.authKey = authKey;
        this.telNumber = telNumber;
        this.email = email;
    }





    public UserDatabase() {
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

    public String getProfileImmage() {
        return profileImmage;
    }

    public void setProfileImmage(String profileImmage) {
        this.profileImmage = profileImmage;
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

    public void setUserGroups(Map<String, Object> userGroups) {
        this.userGroups = userGroups;
    }
}