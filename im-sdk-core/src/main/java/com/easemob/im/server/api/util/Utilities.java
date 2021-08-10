package com.easemob.im.server.api.util;

import java.time.Instant;

public class Utilities {
    public static int toExpireOnSeconds(int expireInSeconds) {
        return (int) (Instant.now().plusSeconds(expireInSeconds).toEpochMilli() / 1000);
    }
}
