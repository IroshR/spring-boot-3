package com.iroshnk.nftraffle.config.security;

import com.iroshnk.nftraffle.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Value("${jwt.token-prefix}")
    public String tokenPrefix;
    @Value("${jwt.header-string}")
    private String headerString;
    @Value("${jwt.update-token-in-each-request}")
    private boolean updateTokenInEachRequest;

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenHandler jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(headerString);
        String username = null;
        String authToken = null;
        LOGGER.info("JWT Authentication filter");
        if (header != null && header.startsWith(tokenPrefix)) {
            authToken = header.replace(tokenPrefix, "").trim();
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                LOGGER.error("An error occurred while fetching Username from Token", e);
            } catch (ExpiredJwtException e) {
                LOGGER.warn("The token has expired", e);
            } catch (Exception e) {
                LOGGER.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            LOGGER.warn("Couldn't find bearer string, header will be ignored");
        }
        LOGGER.info("Username is [{}]", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil
                        .getAuthenticationToken(
                                authToken,
                                userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                LOGGER.info("authenticated user [{}], setting security context", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                if (updateTokenInEachRequest) {
                    String oldToken = authToken;
                    authToken = jwtTokenUtil.generateToken(authentication);
                    userService.updateToken(username, oldToken, authToken);
                }
                response.setHeader("token", authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
