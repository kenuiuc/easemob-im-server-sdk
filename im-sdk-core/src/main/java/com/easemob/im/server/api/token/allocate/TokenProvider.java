package com.easemob.im.server.api.token.allocate;

import com.easemob.im.server.api.token.Token;
import com.easemob.im.server.exception.EMNotImplementedException;
import reactor.core.publisher.Mono;

public interface TokenProvider {

    Mono<Token> fetchAppToken();

    default Mono<Token> fetchUserToken(String username, String password) {
        throw new EMNotImplementedException("not implemented");
    }

    default Mono<Token> buildUserToken(String userId, int expireSeconds) {
        throw new EMNotImplementedException("not implemented");
    }
}
