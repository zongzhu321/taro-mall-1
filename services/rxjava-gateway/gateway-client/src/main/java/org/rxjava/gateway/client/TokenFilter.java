package org.rxjava.gateway.client;

import org.rxjava.common.core.entity.LoginInfo;
import org.rxjava.common.core.utils.JsonUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author happy 2019-05-12 22:07
 */
public class TokenFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZATION = "authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(AUTHORIZATION);

        ServerHttpResponse response = exchange.getResponse();
        if (token == null || token.isEmpty()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserId("lkasldklwerer");
        String loginInfoJson;

        try {
            loginInfoJson = URLEncoder.encode(JsonUtils.serialize(loginInfo), "utf8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        ServerHttpRequest host = request.mutate().header("loginInfo", loginInfoJson).build();
        ServerWebExchange build = exchange.mutate().request(host).build();
        return chain.filter(build);
    }


    @Override
    public int getOrder() {
        return 0;
    }
}