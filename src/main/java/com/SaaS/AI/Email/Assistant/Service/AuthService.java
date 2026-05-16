package com.SaaS.AI.Email.Assistant.Service;

import com.SaaS.AI.Email.Assistant.Entity.User;
import com.SaaS.AI.Email.Assistant.Repository.UserRepo;
import com.SaaS.AI.Email.Assistant.dto.LoginRequest;
import com.SaaS.AI.Email.Assistant.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtService jwtUtil;

    public AuthService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, JwtService jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User Already Exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepo.save(user);

        return jwtUtil.generateToken(user.getEmail());
    }


    public String login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return jwtUtil.generateToken(user.getEmail());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

}
