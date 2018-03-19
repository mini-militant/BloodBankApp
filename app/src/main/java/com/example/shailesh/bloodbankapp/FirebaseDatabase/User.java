package com.example.shailesh.bloodbankapp.FirebaseDatabase;

/**
 * Created by shailesh on 11/3/18.
 */

public class User {

    public String name;
    public String surname;
    public String gender;
    public String address;
    //public String rhFactor;
    public String bloodType;
    public String additionalData;


    public User(){
    }

    public User(String name,String surname,String gender,String address,String bloodType,String additionalData){
        this.name=name;
        this.surname=surname;
        this.gender=gender;
        this.address=address;

        this.bloodType=bloodType;
        this.additionalData=additionalData;
    }
}
