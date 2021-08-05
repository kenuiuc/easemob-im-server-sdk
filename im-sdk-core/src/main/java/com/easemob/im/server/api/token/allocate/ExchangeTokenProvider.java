package com.easemob.im.server.api.token.allocate;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.api.Codec;
import com.easemob.im.server.api.ErrorMapper;
import com.easemob.im.server.api.loadbalance.EndpointRegistry;
import com.easemob.im.server.api.loadbalance.LoadBalancer;
import com.easemob.im.server.api.token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

public class ExchangeTokenProvider implements TokenProvider {

    private static final Logger log = LoggerFactory.getLogger(ExchangeTokenProvider.class);
    private static final int EXPIRE_IN_SECONDS = 20;
    private static final String DEMO_CLIENT_ID = "YXA66CvMXzNmTZanwZLekX6isA";
    private static final String DEMO_CLIENT_SECRET = "YXA6WFIt5YLui0vy9GKUeX4PUMmoUgM";

    private final ExchangeTokenRequest exchangeTokenRequest = new ExchangeTokenRequest();

    private final EMProperties properties;

    private final HttpClient httpClient;

    private final EndpointRegistry endpointRegistry;

    private final LoadBalancer loadBalancer;

    private final Codec codec;

    private final ErrorMapper errorMapper;

    private final Mono<Token> appToken;

    public ExchangeTokenProvider(EMProperties properties, HttpClient httpClient,
            EndpointRegistry endpointRegistry, LoadBalancer loadBalancer, Codec codec,
            ErrorMapper errorMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.endpointRegistry = endpointRegistry;
        this.loadBalancer = loadBalancer;
        this.codec = codec;
        this.errorMapper = errorMapper;
        this.appToken = exchangeToken(properties.getAppId(), properties.getAppCert())
                .cache(token -> Duration.ofSeconds(EXPIRE_IN_SECONDS)
                                .dividedBy(2),
                        error -> Duration.ofSeconds(10),
                        () -> Duration.ofSeconds(10));
    }

    @Override
    public Mono<Token> fetchAppToken() {
        return this.appToken;
    }


    private Mono<Token> exchangeToken(String appId, String appCert) {
        String agoraAppTokenValue = AccessToken2Utils.buildAppToken(appId, appCert, EXPIRE_IN_SECONDS);
        log.info("agoraAppTokenValue = {}", agoraAppTokenValue);

        TokenRequest tokenRequest = AppTokenRequest.of(DEMO_CLIENT_ID, DEMO_CLIENT_SECRET);
        return endpointRegistry.endpoints()
                .map(this.loadBalancer::loadBalance)
                .flatMap(endpoint -> this.httpClient
                        .baseUrl(String.format("%s/%s", endpoint.getUri(),
                                this.properties.getAppkeySlashDelimited()))
                        .headers(headers -> headers.set("agoraAppTokenValue", agoraAppTokenValue))
                        .post()
                        .uri("/token")
                        .send(Mono.create(sink -> sink.success(this.codec.encode(tokenRequest))))
                        .responseSingle((rsp, buf) -> this.errorMapper.apply(rsp).then(buf)))
                .map(buf -> this.codec.decode(buf, TokenResponse.class))
                .map(TokenResponse::asToken);






//        return endpointRegistry.endpoints()
//                .map(this.loadBalancer::loadBalance)
//                .flatMap(endpoint -> this.httpClient
//                        .baseUrl(String.format("%s/%s/token", endpoint.getUri(),
//                                this.properties.getAppkeySlashDelimited()))
//                        .headers(headers -> headers.set("Authorization", String.format("Bearer %s", agoraAppTokenValue)))
//                        .post()
//                        .send(Mono.create(sink -> sink.success(this.codec.encode(exchangeTokenRequest))))
//                        .responseSingle((rsp, buf) -> this.errorMapper.apply(rsp).then(buf)))
//                .map(buf -> this.codec.decode(buf, TokenResponse.class))
//                .map(TokenResponse::asToken);
    }

}
