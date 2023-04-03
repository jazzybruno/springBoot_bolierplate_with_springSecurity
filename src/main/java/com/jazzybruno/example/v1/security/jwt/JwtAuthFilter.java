package com.jazzybruno.example.v1.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jazzybruno.example.v1.exceptions.JWTVerificationException;
import com.jazzybruno.example.v1.exceptions.TokenException;
import com.jazzybruno.example.v1.repositories.UserRepository;
import com.jazzybruno.example.v1.security.user.UserSecurityDetails;
import com.jazzybruno.example.v1.security.user.UserSecurityDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserSecurityDetailsService userSecurityDetailsService;
    private final JwtUtils jwtUtils;

    public void throwErrors(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain , Exception e) throws IOException, ServletException {
        TokenException exception = new TokenException(e.getMessage());

        // the repsonse type and status
        response.setStatus(exception.getResponseEntity().getStatusCodeValue());
        response.setContentType("application/json");

        // set a new response object as a json one
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(exception.getResponseEntity().getBody()));

        // exit the filter chain
        filterChain.doFilter(request, response);
        return;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        JwtUserInfo jwtUserInfo = null;
        String jwtToken = null;

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);

        try {
            jwtUserInfo = jwtUtils.decodeToken(jwtToken);
        } catch (JWTVerificationException e) {
            throwErrors(request , response , filterChain , e);
        }

        if (jwtUserInfo.getEmail() != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserSecurityDetails userSecurityDetails = (UserSecurityDetails) userSecurityDetailsService.loadUserByUsername(jwtUserInfo.getEmail());
                if (jwtUtils.isTokenValid(jwtToken, userSecurityDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userSecurityDetails, jwtToken, userSecurityDetails.getGrantedAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                filterChain.doFilter(request, response);
            } catch (UsernameNotFoundException e) {
                throwErrors(request , response , filterChain , e);
            }
        }
    }
}
