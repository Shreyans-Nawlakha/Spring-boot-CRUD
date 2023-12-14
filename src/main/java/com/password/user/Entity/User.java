package com.password.user.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.codehaus.jettison.json.JSONObject;

import java.util.Base64;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String email;

    private String password;

//    private String newPassword;


    public User(){

    }
    public User(int id, String email, String oldPassword) {
        this.id = id;
        this.email = email;
        this.password = oldPassword;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decryptedByte = decoder.decode(password);
        return new String(decryptedByte);
    }

    public void setPassword(String password) {
        byte[] encryptedByte = password.getBytes();
        Base64.Encoder encoder = Base64.getEncoder();
        this.password = encoder.encodeToString(encryptedByte);
    }

    public String toJsonString(){
        return toJsonObject().toString();
    }
    public JSONObject toJsonObject() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("Id",id);
            jsonObject.put("Email",email);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

//    public String getNewPassword() {
//        return newPassword;
//    }
//
//    public void setNewPassword(String newPassword) {
//        this.newPassword = newPassword;
//    }
}
