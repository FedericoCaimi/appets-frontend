package com.example.appet.Model;

import java.util.UUID;

public class RegistrationModel {
    public String Id;
    public String UserName;
    public String Email;
    public String Password;
    public boolean IsDeleted;
    public String Phone;

    public RegistrationModel(String userName, String email, String phone, String password, boolean isDeleted) {
        this.Id = UUID.randomUUID().toString();
        this.UserName = userName;
        this.Email = email;
        this.Password = password;
        this.IsDeleted = isDeleted;
        this.Phone = phone;
    }
}
