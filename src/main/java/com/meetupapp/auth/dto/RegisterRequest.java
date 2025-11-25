package com.meetupapp.auth.dto;

public record RegisterRequest(String email,String password,String displayName) {
}
