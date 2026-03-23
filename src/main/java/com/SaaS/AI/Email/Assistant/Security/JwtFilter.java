package com.SaaS.AI.Email.Assistant.Security;

import com.SaaS.AI.Email.Assistant.Service.JwtService;
import com.SaaS.AI.Email.Assistant.Repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
    public class JwtFilter extends OncePerRequestFilter {

        @Autowired
        private JwtService jwtService;

        @Autowired
        private UserRepo userRepo;

        public JwtFilter(JwtService jwtService, UserRepo userRepo) {
            this.jwtService=jwtService;
            this.userRepo=userRepo;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            System.out.println("JWT Filter Hit");

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);


            try {
                if (jwtService.isTokenValid(token)) {
                    String email = jwtService.extractEmail(token);
                    System.out.println("JWT email: " + email);

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            userRepo.findByEmail(email)
                                    .orElseThrow(() -> new RuntimeException("User not found"));

                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(email, null, List.of(() -> "ROLE_USER"));

                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    } else {
                        System.out.println("Invalid Token");
                    }
            }
            catch (Exception e) {
                System.out.println("JWT ERROR: " + e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
}

