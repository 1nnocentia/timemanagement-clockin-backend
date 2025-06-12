package com.clockin.clockin.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Menangani pengecualian MethodArgumentNotValidException yang dilempar saat
     * validasi @Valid pada argumen metode controller gagal.
     * Mengembalikan peta berisi detail kesalahan validasi.
     *
     * @param ex MethodArgumentNotValidException yang dilempar
     * @return ResponseEntity dengan status BAD_REQUEST dan detail kesalahan validasi
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Menangani RuntimeException kustom yang dilempar oleh layanan.
     * Mengembalikan pesan kesalahan dengan status HTTP yang sesuai.
     *
     * @param ex RuntimeException yang dilempar
     * @return ResponseEntity dengan status BAD_REQUEST (default) dan pesan kesalahan
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Anda bisa memperluas ini untuk menangani berbagai jenis RuntimeException
        // dengan status HTTP yang berbeda. Untuk saat ini, kita akan mengembalikan BAD_REQUEST.
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Menangani semua pengecualian lainnya yang tidak secara spesifik ditangani.
     *
     * @param ex Exception yang tidak terduga
     * @return ResponseEntity dengan status INTERNAL_SERVER_ERROR dan pesan kesalahan generik
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("Terjadi kesalahan internal server: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

