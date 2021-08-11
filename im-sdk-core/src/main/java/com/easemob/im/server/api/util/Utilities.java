package com.easemob.im.server.api.util;

import java.time.Duration;
import java.time.Instant;

public class Utilities {

    // TODO: put into system env
    public static final Duration IT_TIMEOUT = Duration.ofSeconds(60);
    public static final Duration UT_TIMEOUT = Duration.ofSeconds(3);

    public static int toExpireOnSeconds(int expireInSeconds) {
        return (int) (Instant.now().plusSeconds(expireInSeconds).toEpochMilli() / 1000);
    }
}
