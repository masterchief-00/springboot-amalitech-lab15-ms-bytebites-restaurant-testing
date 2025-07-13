package com.kwizera.restaurantservice.security;

import com.kwizera.restaurantservice.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Component
public class GatewayAuthFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(GatewayAuthFilter.class);
    private final JwtUtil jwtUtil;
    private final String sharedSecret = System.getenv("SHARED_SECRET");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {

            // get data from the http headers
            String receivedPayload = request.getHeader("X-Internal-Payload");
            String receivedHmac = request.getHeader("X-Internal-Auth");
            String userIdHeader = request.getHeader("X-User-Id");
            String userRoleHeader = request.getHeader("X-User-Role");

            // verify if the request is from the gateway
            if (receivedPayload == null || receivedHmac == null || !isValidHmac(receivedPayload, receivedHmac)) {
                logger.error("Invalid gateway auth");
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid gateway auth");
                return;
            }

            if (userIdHeader == null || userRoleHeader == null) {
                logger.error("Missing headers");
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid gateway auth");
                return;
            }

            // validate the jwt token
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtUtil.isTokenValid(jwt)) {
                Long userId = jwtUtil.getUserIdFromToken(jwt);
                String role = jwtUtil.getUserRoleFromToken(jwt);

                if (userId != Long.parseLong(userIdHeader) || !role.equals(userRoleHeader)) {
                    logger.error("Token claims do not match headers");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid headers");
                    return;
                }

                // initialize spring security context
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userIdHeader,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + userRoleHeader))
                        );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private boolean isValidHmac(String payload, String receivedHmax) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(sharedSecret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] expectedHmacBytes = mac.doFinal(payload.getBytes());
            String expectedHmac = Base64.getEncoder().encodeToString(expectedHmacBytes);

            return MessageDigest.isEqual(expectedHmac.getBytes(), receivedHmax.getBytes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
