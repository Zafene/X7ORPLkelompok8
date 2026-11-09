package com.bikerent.model;

public class Admin {
    private int id;
    private String username;
    private String passwordHash;

    public int getId()                     { return id; }
    public void setId(int v)               { this.id = v; }
    public String getUsername()            { return username; }
    public void setUsername(String v)      { this.username = v; }
    public String getPasswordHash()        { return passwordHash; }
    public void setPasswordHash(String v)  { this.passwordHash = v; }
}
