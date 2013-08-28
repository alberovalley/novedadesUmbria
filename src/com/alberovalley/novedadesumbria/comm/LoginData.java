package com.alberovalley.novedadesumbria.comm;

public class LoginData {

    public static final String userNameTAG = "ACCESO";
    public static final String passwordTAG = "CLAVE";

    public String userName = "";
    public String password = "";

    public LoginData(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
