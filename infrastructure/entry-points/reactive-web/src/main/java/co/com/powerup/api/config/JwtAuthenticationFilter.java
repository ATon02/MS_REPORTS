package co.com.powerup.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import co.com.powerup.api.exceptions.ForbiddenException;
import co.com.powerup.api.exceptions.UnauthorizedException;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${SPRING_SECRET_KEY}")
    private String secretKey;

    @SuppressWarnings({ "null", "deprecation" })
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/webjars/swagger-ui") || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui.html") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.error(new UnauthorizedException("Token de authorizacion invalido"));
        }
        String token = authHeader.substring(7);
        return Mono.fromCallable(() -> {
                    return Jwts.parser()
                            .setSigningKey(secretKey.getBytes())
                            .parseClaimsJws(token)
                            .getBody();
                })
                .map(claims -> {
                    exchange.getAttributes().put("claims", claims);
                    return claims;
                })
                .flatMap(claims -> chain.filter(exchange));
    }

    public Mono<Boolean> hasRole(ServerRequest request, List<String> allowedRoles) {
        Object claimsAttr = request.exchange().getAttribute("claims");

        if (!(claimsAttr instanceof Claims claims)) {
            return Mono.just(false);
        }
        String userRole = claims.get("role", String.class);
        return Mono.just(userRole != null && allowedRoles.contains(userRole));
    }


    public HandlerFilterFunction<ServerResponse, ServerResponse> requireRole(List<String> allowedRoles) {
        return (request, next) ->
        hasRole(request, allowedRoles)
            .flatMap(hasRole -> hasRole
                ? next.handle(request)
                : Mono.error(new ForbiddenException( "No tienes permiso para acceder a este recurso"))
            );
    }
}
