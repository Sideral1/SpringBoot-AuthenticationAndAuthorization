# SpringBoot AuthenticationAndAuthorization

Spring Boot application module responsible for managing application users, authentication, and roles. This module provides user registration, login, and OIDC integration while following secure coding practices.


## Features
 - User Management
   - Register and store user data
   - Login with email and password
    - Role-based access control (Admin/User)

  - Authentication
    - Spring Security integration
    - Support for OIDC (OpenID Connect) users
    - Password hashing recommended (not stored in plaintext)
      
  - Database
    - JPA entities for users, roles, and authentication methods
    - Repositories for easy data access
    - Auto-managed creation timestamps

## Security Notes
  - Passwords are stored in the database (ensure proper hashing in production).
  - No credentials or secrets are hardcoded in the codebase.
  - User passwords are only managed at runtime through DTOs and entities.

## Dependencies
  - Spring Boot
  - Spring Security
  - Lombok
  - Jakarta Persistence (JPA)
  - Spring OAuth 2.0 Login

## License
This project is licensed under the MIT License.
