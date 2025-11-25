# MeetUpApp Backend (Spring Boot)

A modular, scalable backend for a real-time meeting & video-conferencing platform inspired by Zoom and Google Meet.
The backend is built with a focus on:
  Clean architecture
  Security-first design (JWT-based)
  Extensibility for future modules like meetings, chat, WebRTC signalling, user roles, etc.

This repository currently contains all the foundational authentication and security components required for a production-ready system.

# Tech Stack

Backend Framework:
  Java 21  
  Spring Boot 3.5.x
  Spring Web
  Spring Data JPA
  Spring Security (Stateless JWT)

Database:
  MySQL 8
  Hibernate ORM

Build & Tools:
  Maven
  Lombok
  JSON Web Tokens (JJWT)

# Project Overview

This backend handles:

  User Registration
  User Login
  JWT Token Issuance
  Token Validation
  Stateless Authentication
  /api/auth/me — retrieve logged-in user's details using JWT
  Custom security configuration
  Custom user details system

Production-grade patterns are followed:
  No in-memory authentication
  No session state
  Passwords hashed using BCrypt
  Tokens verified on every request
  Filter-based authentication (JWT)
  Separation of controllers, services, entities, repository, and security modules

# Note:
If you see files such as:
com.ZoomAppApplicationTests.java
Or any class under com.zoomapp or similar naming
Do NOT get confused.
This project was originally initialized under the working name ZoomApp, and some auto-generated test files still contain the old prefix.
They are harmless, do not affect functionality, and can be renamed anytime.
You may update these to match the final name com.meetupapp whenever needed.
Also Create Your Own application.property because for security purpose I have remove it.

# Day 1 Documentation

Agenda: Establish Core User System + Basic Authentication

Day 1 focused on setting up a production-grade foundation:
  Goals
  Build a real User entity
  Enable Registration
  Enable Login
  Store passwords securely
  Connect MySQL database
  Build clean DTOs
  Return structured responses
Prepare for JWT integration (Day 2)

*Project Setup*

Created a clean Spring Boot application with:
  Spring Web
  Spring Data JPA
  Spring Security (temporary/basic)
  MySQL Driver
  Lombok

*Database & User Entity*
✔ We designed a real User entity:

User.java contains:
  UUID userId
  String email
  String passwordHash
  String displayName
  boolean emailVerified
  LocalDateTime createdAt
  A @PrePersist hook automatically sets createdAt.

*Repository Layer*

A clean JPA repository:
UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}

*DTOs for Clean API Communication*

User registration DTOs:
  RegisterRequest
  RegisterResponse

Login DTOs:
  LoginRequest
  LoginResponse

*Auth Service — Core Logic*

AuthService handles:

 Registration:
  Validate if email already exists
  BCrypt hash the password
  Save user
  Return response DTO

 Login:
  Verify email exists
  Compare raw password with hashed password
  Generate JWT (placeholder on Day 1)
  Return token + expiration + user info

*SecurityConfig (Temporary Basic Security)*

On Day 1, we used basic configuration:
  Disabled CSRF
  Allowed /register and /login without authentication
  Required authentication for all other endpoints
  Added a BCryptPasswordEncoder Bean
  This was ONLY for Day 1 to prepare for JWT.

*Controller Layer*
AuthController exposed:
  POST /api/auth/register
  POST /api/auth/login

*Testing with Postman*

Verified:
  User successfully registers
  User password stored as hash
  Login works
  Temporary security config allows login & register
  Database tables created automatically

  

# Day 2 — JWT Authentication & Stateless Security Layer

Agenda: Transform the backend from basic, session-based security (Day 1) into a modern, stateless, token-based authentication system used by real production systems like Zoom, Google Meet, and every scalable microservice/API today.

*What We Built on Day 2*

Implemented JWT (JSON Web Token) Authentication
  Users now receive a signed token after login.

Added Custom UserDetails + CustomUserDetailsService
  Spring Security now understands our User entity.

Added JwtAuthenticationFilter
  Every request is now checked for a valid token.

Replaced BasicAuth with Stateless Security
  Removed in-memory users; disabled form login; switched to REST security.

Added /api/auth/me endpoint
  Allows frontend to auto-login users and fetch profile securely.

# Day 2 Directory / Class Overview
com.meetupapp
 ├── auth
 │    ├── controller
 │    │      └── AuthController.java
 │    ├── dto
 │    ├── entity
 │    │      └── User.java
 │    ├── repository
 │    │      └── UserRepository.java
 │    └── service
 │           └── AuthService.java
 │           └── JwtService.java   ← (NEW)
 │
 └── security
       ├── SecurityConfig.java    ← (UPDATED)
       ├── JwtAuthenticationFilter.java  ← (NEW)
       ├── CustomUserDetails.java ← (NEW)
       └── CustomUserDetailsService.java ← (NEW)

*JwtService — Token Generator + Validator*

Purpose:
Generate JWT token after login
Extract email from JWT
Validate token (expiry & signature)

Core Logic:
Token contains "email" and "userId"
Token signed with secret key (HS256)
Expiry: configured via jwt.expiration

Validation:
signature must match
email must match database
token must not be expired

*CustomUserDetails — Bridge Between Your User Entity & Spring Security*

It requires a standard format:
getUsername()
getPassword()
getAuthorities()
isAccountNonLocked(), etc.

So we wrap entity inside this standardized adapter class.

*CustomUserDetailsService — Load User From DB*

On every request:
Extract email from token
Load user from DB
Build authentication object

So this service powers Spring Security’s authentication pipeline.

*JwtAuthenticationFilter — Core of Day 2*

Purpose: Executed on every incoming request before controller.

Flow:
1. Read Authorization header
2. If no "Bearer <token>" → skip
3. Extract JWT
4. Extract email from JWT
5. Load user details
6. Validate JWT signature + expiry
7. Create authentication object
8. Store it in SecurityContextHolder
9. Continue request

This replaces old session-based authentication 100%.

*SecurityConfig — Reconstructed for Stateless JWT*

Key Updates:
  Disabled CSRF
  Disabled Form Login
  Disabled HTTP Basic
  Enabled stateless mode
  Added JWT Filter
  Allowed only /register and /login without token

  */api/auth/me — Logged-in User Profile Endpoint*

Return:
  userId
  email
  displayName
  emailVerified

*Final Day 2 Flow — End-to-End*
User Login → AuthService validates → JwtService generates token → 
Frontend stores token → every API call sends Authorization: Bearer <token> → 
JwtFilter validates → Spring Security marks user authenticated → Controller executes.
