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
public class ProductsRoute {

    @Bean
    public RouterFunction<ServerResponse> productRoute() {
        return route("product-route")
                .route(RequestPredicates.path("/api/v1/products/**"), http())
                .before(uri("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "productServiceCircuitBreaker",
                        URI.create("forward:/productServiceFallBackRoute")
                ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceFallBackRoute() {
        return  route("productServiceFallBackRoute")
                .route(RequestPredicates.path("/productServiceFallBackRoute"),
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Product Service Is Unavailable"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> productServiceApiDocs() {
        return GatewayRouterFunctions.route("product-service-api-docs")
                .route(RequestPredicates.path("/docs/product-service/v3/api-docs"), http())
                .before(uri("http://localhost:8082"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }
}
