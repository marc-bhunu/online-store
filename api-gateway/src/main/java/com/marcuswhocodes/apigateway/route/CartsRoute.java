package com.marcuswhocodes.apigateway.route;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
@Configuration
public class CartsRoute {

    @Bean
    public RouterFunction<ServerResponse> cartRoute() {
        return route("cart-route")
                .route(RequestPredicates.path("/api/v1/cart/**"), http())
                .before(uri("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "cartServiceCircuitBreaker",
                        URI.create("forward:/cartServiceFallbackRoute")
                ))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> cartServiceFallBackRoute() {
        return  route("cartServiceFallBackRoute")
                .route(RequestPredicates.path("/cartServiceFallBackRoute"),
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Cart Service Is Unavailable"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> cartServiceApiDocs() {
        return GatewayRouterFunctions.route("cart-service-api-docs")
                .route(RequestPredicates.path("/docs/cart-service/v3/api-docs"), http())
                .before(uri("http://localhost:8081"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }

}
