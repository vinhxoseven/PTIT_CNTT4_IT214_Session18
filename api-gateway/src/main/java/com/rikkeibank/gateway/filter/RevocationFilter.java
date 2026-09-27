package com.rikkeibank.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RevocationFilter implements GlobalFilter, Ordered {
    private final ReactiveStringRedisTemplate redis;
    public RevocationFilter(ReactiveStringRedisTemplate redis){ this.redis = redis; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/api/auth/login") || path.equals("/api/auth/refresh") || path.equals("/actuator/health")) {
            return chain.filter(exchange);
        }
        return exchange.getPrincipal().cast(Authentication.class)
                .flatMap(auth -> redis.hasKey("revoked:user:" + auth.getName())
                    .flatMap(revoked -> {
                        if (Boolean.TRUE.equals(revoked)) {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                        return chain.filter(exchange);
                    }))
                .switchIfEmpty(chain.filter(exchange));
    }
    @Override public int getOrder(){ return -1; }
}
