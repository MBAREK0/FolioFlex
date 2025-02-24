package org.mbarek0.folioflex.web.vm.request.authentication;

import jakarta.validation.constraints.NotBlank;

public class LoginFormVM {


    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    private String password;

    public LoginFormVM() {
    }

    public LoginFormVM(String username, String password) {
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
