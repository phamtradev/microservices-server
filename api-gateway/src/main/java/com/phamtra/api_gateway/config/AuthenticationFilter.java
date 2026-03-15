package com.phamtra.api_gateway.config;

import com.phamtra.api_gateway.service.IdentityService;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final IdentityService identityService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter authentication filter....");

        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);

        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());

        String token = authHeader.getFirst().replace("Bearer ", "");
        log.info("Token: {}", token);

        identityService.introspect(token).subscribe(introspectResponseResponseEntity -> {

        });

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        String body = "Unauthenticated";
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
