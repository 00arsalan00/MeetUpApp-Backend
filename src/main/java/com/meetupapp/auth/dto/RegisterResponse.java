package com.meetupapp.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterResponse(UUID userId,String email,String displayName,boolean emailVerified,LocalDateTime createdAt) {}
