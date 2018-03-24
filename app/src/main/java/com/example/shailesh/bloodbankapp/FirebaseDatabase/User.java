package com.example.shailesh.bloodbankapp.FirebaseDatabase;

/**
 * Created by shailesh on 11/3/18.
 */

public class User {

    public String name;
    public String gender;
    public String donorAddress;
    public String phoneNo;
    public String bloodType;
    public String additionalData;


    public User() {
    }


    public User(String name, String gender, String donorAddress, String bloodType, String additionalData, String phoneNo) {
        this.name = name;
        this.gender = gender;
        this.donorAddress = donorAddress;
        this.bloodType = bloodType;
        this.additionalData = additionalData;
        this.phoneNo = phoneNo;
    }

    public String getBloodType(){
        return bloodType;
    }

}
