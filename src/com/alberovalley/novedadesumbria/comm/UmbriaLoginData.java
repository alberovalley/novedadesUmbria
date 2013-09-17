package com.alberovalley.novedadesumbria.comm;

/**
 * POJO used to transfer the user's login data
 * 
 * @author frank
 * 
 */
public class UmbriaLoginData {
    // ////////////////////////////////////////////////////////////
    // Constants
    // ////////////////////////////////////////////////////////////
    public static final String USER_NAME_TAG = "ACCESO";
    public static final String PASSWORD_TAG = "CLAVE";
    // ////////////////////////////////////////////////////////////
    // Attributes
    // ////////////////////////////////////////////////////////////
    private String userName = "";
    private String password = "";

    // ////////////////////////////////////////////////////////////
    // Constructor
    // ////////////////////////////////////////////////////////////
    public UmbriaLoginData(String userName, String password) {
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
