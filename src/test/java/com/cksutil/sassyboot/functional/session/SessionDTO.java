package com.cksutil.sassyboot.functional.session;


public class SessionDTO {
    private String username;
    private String password;

    public SessionDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
