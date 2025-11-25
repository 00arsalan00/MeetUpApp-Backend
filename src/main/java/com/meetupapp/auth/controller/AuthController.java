package com.meetupapp.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meetupapp.auth.dto.LoginRequest;
import com.meetupapp.auth.dto.LoginResponse;
import com.meetupapp.auth.dto.MeResponse;
import com.meetupapp.auth.dto.RegisterRequest;
import com.meetupapp.auth.dto.RegisterResponse;
import com.meetupapp.auth.service.AuthService;
import com.meetupapp.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.experimental.var;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
	    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
		return ResponseEntity.ok(authService.login(request));
	}
	
	@GetMapping("/me")
	public ResponseEntity<MeResponse> me(){
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
											.getContext()
											.getAuthentication()
											.getPrincipal();
		var user = userDetails.getUser();
		
		return ResponseEntity.ok(
				new MeResponse(
						user.getUserId(),
						user.getEmail(),
						user.getDisplayName(),
						user.isEmailVerified()
						
						)
				);
	}

}
