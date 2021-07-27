package com.easemob.im.server.api.token.allocate;

import com.easemob.im.server.api.token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

public class AgoraTokenProvider implements TokenProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultTokenProvider.class);

    private final String appId;
    private final String appCertificate;
    private final Mono<Token> appToken;

    public AgoraTokenProvider(String appId, String appCertificate, int expireSeconds) {
        this.appId = appId;
        this.appCertificate = appCertificate;
        this.appToken = Mono.fromCallable(() -> {
            final String appTokenValue =
                    AccessToken2Utils.buildAppToken(appId, appCertificate, expireSeconds);
            final Instant expireAt = Instant.now().plusSeconds(expireSeconds);
            return new Token(appTokenValue, expireAt);
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
        final String userTokenValue;
        userTokenValue = AccessToken2Utils
                .buildUserChatToken(appId, appCertificate, userId, expireSeconds);
        final Instant expireAt = Instant.now().plusSeconds(expireSeconds);
        final Token userToken = new Token(userTokenValue, expireAt);
        return Mono.just(userToken);
    }
}
