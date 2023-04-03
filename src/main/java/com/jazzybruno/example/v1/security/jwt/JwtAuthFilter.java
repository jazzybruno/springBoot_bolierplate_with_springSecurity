package com.jazzybruno.example.v1.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jazzybruno.example.v1.exceptions.JWTVerificationException;
import com.jazzybruno.example.v1.exceptions.TokenException;
import com.jazzybruno.example.v1.repositories.UserRepository;
import com.jazzybruno.example.v1.security.user.UserSecurityDetails;
import com.jazzybruno.example.v1.security.user.UserSecurityDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization");
             JwtUserInfo jwtUserInfo = null;
             String jwtToken = null;

            if(authHeader == null || !authHeader.startsWith("Bearer")){
                filterChain.doFilter(request , response);
                return;
            }

            jwtToken = authHeader.substring(7);

            try {
                jwtUserInfo = jwtUtils.decodeToken(jwtToken);
            }catch (JWTVerificationException e){
                TokenException exception =new TokenException(e.getMessage());

                // the repsonse type and status
                response.setStatus(exception.getResponseEntity().getStatusCodeValue());
                response.setContentType("application/json");

                // set a new response object as a json one
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(exception.getResponseEntity().getBody()));

                // exit the filter chain
                filterChain.doFilter(request , response);
                return;
            }

            if(jwtUserInfo.getEmail() != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserSecurityDetails userSecurityDetails = (UserSecurityDetails) userSecurityDetailsService.loadUserByUsername(jwtUserInfo.getEmail());

                if(jwtUtils.isTokenValid(jwtToken , userSecurityDetails)){
                    System.out.println("I have reached the username anf password authentication filter");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userSecurityDetails , jwtToken , userSecurityDetails.getGrantedAuthorities()
                    );
                    System.out.println("I have exited the username anf password authentication filter");
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }else{
                    System.out.println("The jwt token is not valid!!!");
                }
            }
            filterChain.doFilter(request , response);

    }
}
