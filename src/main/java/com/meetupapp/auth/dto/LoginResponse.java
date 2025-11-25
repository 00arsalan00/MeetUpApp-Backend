package com.meetupapp.auth.dto;

import java.util.UUID;

public record LoginResponse(String accessToken,int expiredIn,UUID userId,String email) {

}
