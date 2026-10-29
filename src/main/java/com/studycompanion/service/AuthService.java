package com.studycompanion.service;

import com.studycompanion.dto.AuthResponse;
import com.studycompanion.dto.LoginRequest;
import com.studycompanion.dto.RegisterRequest;
import com.studycompanion.entity.AppUser;
import com.studycompanion.exception.DuplicateEmailException;
import com.studycompanion.exception.InvalidCredentialsException;
import com.studycompanion.exception.NotFoundException;
import com.studycompanion.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;

@Service
public class AuthService {

    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";

    private final AppUserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request == null || request.displayName() == null || request.displayName().isBlank()) {
            throw new IllegalArgumentException("displayName is required");
        }
        if (request.displayName().trim().length() > 100) {
            throw new IllegalArgumentException("displayName must be 100 characters or fewer");
        }
        String email = normalizeEmail(request.email());
        validateEmail(email);
        validatePassword(request.password());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("An account with this email already exists");
        }

        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        String saltText = Base64.getEncoder().encodeToString(salt);
        String hashText = hashPassword(request.password(), salt);

        AppUser user = userRepository.save(new AppUser(request.displayName().trim(), email, saltText, hashText));
        return toResponse(user, false);
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        if (request == null || request.email() == null || request.password() == null) {
            throw new InvalidCredentialsException();
        }

        AppUser user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(InvalidCredentialsException::new);
        byte[] salt = Base64.getDecoder().decode(user.getPasswordSalt());
        byte[] expectedHash = Base64.getDecoder().decode(user.getPasswordHash());
        byte[] submittedHash = Base64.getDecoder().decode(hashPassword(request.password(), salt));
        if (!MessageDigest.isEqual(expectedHash, submittedHash)) {
            throw new InvalidCredentialsException();
        }

        HttpSession session = servletRequest.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("displayName", user.getDisplayName());
        session.setAttribute("email", user.getEmail());
        servletRequest.changeSessionId();
        return toResponse(user, true);
    }

    public AuthResponse getCurrentUser(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Logged-in user no longer exists"));
        return toResponse(user, true);
    }

    private AuthResponse toResponse(AppUser user, boolean authenticated) {
        return new AuthResponse(user.getId(), user.getDisplayName(), user.getEmail(), authenticated);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validateEmail(String email) {
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("password must be at least 8 characters long");
        }
    }

    private String hashPassword(String password, byte[] salt) {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        try {
            byte[] hash = SecretKeyFactory.getInstance(HASH_ALGORITHM).generateSecret(keySpec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Password hashing is unavailable", exception);
        } finally {
            keySpec.clearPassword();
        }
    }
}
