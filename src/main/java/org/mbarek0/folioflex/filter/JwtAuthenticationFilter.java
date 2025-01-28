package org.mbarek0.folioflex.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.mbarek0.folioflex.service.user.CustomUserDetailsService;
import org.mbarek0.folioflex.service.authentication.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            String refreshHeader = request.getHeader("X-Refresh-Token");

            if (refreshHeader != null) {
                handleRefreshToken(refreshHeader, response);
                return;
            }

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (!jwtUtil.isTokenExpired(token) && jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else if (jwtUtil.isTokenExpired(token)) {
                        throw new ExpiredJwtException(null, null, "Access token has expired");
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            // Handle expired access token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access token expired\"}");
            response.getWriter().flush();
            return;
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        chain.doFilter(request, response);
    }

    private void handleRefreshToken(String refreshToken, HttpServletResponse response) throws IOException {
        try {
            String username = jwtUtil.extractUsername(refreshToken);
            if (jwtUtil.isTokenValid(refreshToken, username)) {
                // Generate new tokens and send them in response
                String newAccessToken = jwtUtil.generateToken(username);
                String newRefreshToken = jwtUtil.generateRefreshToken(username);

                response.setContentType("application/json");
                response.getWriter().write("{\"accessToken\": \"" + newAccessToken + "\", \"refreshToken\": \"" + newRefreshToken + "\"}");
                response.getWriter().flush();
            } else {
                throw new ExpiredJwtException(null, null, "Refresh token is invalid or expired");
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Refresh token expired\"}");
            response.getWriter().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid refresh token\"}");
            response.getWriter().flush();
        }
    }
}
