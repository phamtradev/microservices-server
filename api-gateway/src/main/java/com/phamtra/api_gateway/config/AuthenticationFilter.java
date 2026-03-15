package com.phamtra.api_gateway.config;

import com.phamtra.api_gateway.dto.request.IntrospectRequest;
import com.phamtra.api_gateway.dto.response.IntrospectResponse;
import com.phamtra.api_gateway.repository.client.IdentityClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {

    private final IdentityClient identityClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        log.info("Enter authentication filter... path={}", path);

        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing Authorization header");

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        log.info("Token: {}", token);

        IntrospectRequest request = new IntrospectRequest(token);

        return identityClient.introspect(request)
                .flatMap(apiResponse -> {

                    if (apiResponse == null || apiResponse.getData() == null) {
                        log.error("Invalid token: response null");
                        return unauthorized(exchange);
                    }

                    IntrospectResponse result = apiResponse.getData();

                    if (!result.isActive()) {
                        log.error("Token inactive");
                        return unauthorized(exchange);
                    }

                    log.info("Token valid for userId={}", result.getId());

                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    log.error("Authentication error", e);
                    return unauthorized(exchange);
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}