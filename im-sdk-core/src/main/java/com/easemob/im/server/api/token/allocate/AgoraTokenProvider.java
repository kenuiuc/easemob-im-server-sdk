package com.easemob.im.server.api.token.allocate;

import com.easemob.im.server.api.token.Token;
import com.easemob.im.server.api.token.agora.ChatTokenBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

public class AgoraTokenProvider implements TokenProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultTokenProvider.class);
    private final ChatTokenBuilder tokenBuilder = new ChatTokenBuilder();

    private final String appId;
    private final String appCertificate;
    private final int expireSeconds;
    private final Mono<Token> appToken;

    public AgoraTokenProvider(String appId, String appCertificate, int expireSeconds) {
        this.appId = appId;
        this.appCertificate = appCertificate;
        this.expireSeconds = expireSeconds;
        this.appToken = Mono.fromCallable(() -> {
            final String appTokenValue = tokenBuilder.buildAppToken(appId, appCertificate,
                    expireSeconds);
            final Instant expireAt = Instant.now().plusSeconds(expireSeconds);
            Token appToken = new Token(appTokenValue, expireAt);
            log.info("KEN_LOG: just created a new appToken = {}", appToken);
            return appToken;
        }).cache(token -> Duration.ofSeconds(expireSeconds).dividedBy(2),
                error -> Duration.ofSeconds(10),
                () -> Duration.ofSeconds(10)
        );
    }

    @Override
    public Mono<Token> fetchAppToken() {
        return this.appToken;
    }

    @Override
    public Mono<Token> buildUserToken(String userId, int expireSeconds) {
        final String userTokenValue = tokenBuilder.buildUserToken(appId, appCertificate, userId, expireSeconds);
        final Instant expireAt = Instant.now().plusSeconds(expireSeconds);
        final Token userToken = new Token(userTokenValue, expireAt);
        return Mono.just(userToken);
    }
}
