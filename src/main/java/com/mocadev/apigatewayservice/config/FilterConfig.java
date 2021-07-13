package com.mocadev.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chcjswo
 * @version 1.0.0
 * @blog https://mocadev.tistory.com
 * @github https://github.com/chcjswo
 * @since 2021-07-13
 **/
public class FilterConfig {

	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route(r -> r.path("/first-service/**")
						.filters(f -> f.addRequestHeader("first-request", "first-request-header")
										.addResponseHeader("first-response", "first-response-header"))
						.uri("http://localhost:8081"))
			.route(r -> r.path("/second-service/**")
						.filters(f -> f.addRequestHeader("second-request", "second-request-header")
										.addResponseHeader("second-response", "second-response-header"))
						.uri("http://localhost:8082"))
			.build();
	}

}
