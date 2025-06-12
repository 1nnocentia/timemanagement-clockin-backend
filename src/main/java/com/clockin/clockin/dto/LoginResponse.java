package com.clockin.clockin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private boolean success;
    // Anda bisa menambahkan token JWT di sini jika sudah mengimplementasikan security penuh
    // private String token;
}