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
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class OrdersRoute {


    @Bean
    public RouterFunction<ServerResponse> orderRoute() {
        return route("order-service")
                .route(RequestPredicates.path("/api/v1/orders/**"), http())
                .before(uri("http://localhost:8084"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "orderServiceCircuitBreaker",
                        URI.create("forward:/orderServiceFallbackRoute")
                ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceFallBackRoute() {
        return  route("orderServiceFallBackRoute")
                .route(RequestPredicates.path("/orderServiceFallBackRoute"),
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Order Service Is Unavailable"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> orderServiceApiDocs() {
        return GatewayRouterFunctions.route("order-service-api-docs")
                .route(RequestPredicates.path("/docs/order-service/v3/api-docs"), http())
                .before(uri("http://localhost:8084"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }


}
