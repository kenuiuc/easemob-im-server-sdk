package com.easemob.im.server.api.token.allocate;

import com.easemob.im.server.api.token.Token;
import com.easemob.im.server.api.token.agora.AccessToken2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

public class AgoraTokenProvider implements TokenProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultTokenProvider.class);
    private static final int EXPIRE_IN_SECONDS = 600;

    private final String appId;
    private final String appCert;
    private final Mono<Token> appToken;

    public AgoraTokenProvider(String appId, String appCert) {
        this.appId = appId;
        this.appCert = appCert;
        this.appToken = Mono.fromCallable(() -> {
            final String appTokenValue = AccessToken2Utils.buildAppToken(appId, appCert, EXPIRE_IN_SECONDS);
            final Instant expireAt = Instant.now().plusSeconds(EXPIRE_IN_SECONDS);
            return new Token(appTokenValue, expireAt);
        }).cache(token -> Duration.ofSeconds(EXPIRE_IN_SECONDS).dividedBy(2),
                error -> Duration.ofSeconds(10),
                () -> Duration.ofSeconds(10)
        );
    }

    @Override
    public Mono<Token> fetchAppToken() {
        return this.appToken;
    }


    @Override
    public Mono<Token> buildUserToken(String userId, int expireInSeconds,
            Consumer<AccessToken2> tokenConfigurer) throws Exception {
        String token2Value = AccessToken2Utils
                .buildUserCustomizedToken(appId, appCert, userId, expireInSeconds, tokenConfigurer);
        final Instant expireAt = Instant.now().plusSeconds(expireInSeconds);
        return Mono.just(new Token(token2Value, expireAt));
    }
}
