package com.kwizera.apigateway.security;

import com.kwizera.apigateway.utils.HmacUtil;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final String jwtSecret = System.getenv("JWT_SECRET");

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (path.contains("/auth")) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = request.getHeaders()
                    .getOrEmpty(HttpHeaders.AUTHORIZATION)
                    .get(0)
                    .replace("Bearer", "")
                    .trim();

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.getSubject();
                String role = claims.get("role", String.class);

                Optional<String> match = roleAccessMap.keySet().stream()
                        .filter(path::startsWith)
                        .findFirst();

                if (match.isPresent()) {
                    List<String> allowed = roleAccessMap.get(match.get());
                    if (!allowed.contains(role)) {
                        return onError(exchange, "Forbidden: Role '" + role + "' not allowed", HttpStatus.FORBIDDEN);
                    }
                }

                String sharedSecret = System.getenv("SHARED_SECRET");
                Instant timestamp = Instant.now();
                String payload = userId + ":" + role + ":" + timestamp;
                String hmac = HmacUtil.generateHmacSha256(payload, sharedSecret);


                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Role", role)
                        .header("X-Internal-Auth", hmac)
                        .header("X-Internal-Payload", payload)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                logger.error(e.getMessage());
                return onError(exchange, "Invalid Token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String msg, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private static final Map<String, List<String>> roleAccessMap = Map.of(
            "/restaurant", List.of("OWNER", "CUSTOMER"),
            "/order", List.of("CUSTOMER", "OWNER"),
            "/admin", List.of("ADMIN"),
            "/users", List.of("CUSTOMER", "ADMIN"),
            "/foods", List.of("CUSTOMER", "OWNER")
    );
}

