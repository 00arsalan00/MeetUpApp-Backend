package com.meetupapp.auth.dto;

import java.util.UUID;

public record MeResponse(
		UUID userId,
		String email,
		String displayName,
		boolean emailVerified
		) {}
