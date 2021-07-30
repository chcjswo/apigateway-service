package com.mocadev.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author chcjswo
 * @version 1.0.0
 * @blog https://mocadev.tistory.com
 * @github https://github.com/chcjswo
 * @since 2021-07-31
 **/
@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	private final Environment env;

	public AuthorizationHeaderFilter(Environment env) {
		super(Config.class);
		this.env = env;
	}

	public static class Config {

	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}

			String authorizationHeader = Objects.requireNonNull(
				request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
			String jwt = authorizationHeader.replace("Bearer", "");

			if (!isJwtValid(jwt)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}

			return chain.filter(exchange);
		};
	}

	private boolean isJwtValid(String jwt) {
		boolean isValid = true;

		String subject = null;
		try {
			subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
				.parseClaimsJws(jwt)
				.getBody()
				.getSubject();
		} catch (Exception e) {
			log.error(e.toString());
			isValid = false;
		}

		if (subject == null || subject.isEmpty()) {
			isValid = false;
		}

		return isValid;
	}

	private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus unauthorized) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(unauthorized);
		log.error(message);
		return response.setComplete();
	}

}
