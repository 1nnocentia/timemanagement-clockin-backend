package com.clockin.clockin.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String usernameOrEmail; // Field untuk username atau email
    private String password;
}
