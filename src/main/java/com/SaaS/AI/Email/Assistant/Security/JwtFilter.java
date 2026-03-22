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
import java.util.Collections;

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
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);


            try {
                String email = jwtService.extractEmail(token);

                if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    if(jwtService.isTokenValid(token)){
                        userRepo.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return;
            }
            catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token");
                return;
            }

            filterChain.doFilter(request, response);
        }
}

