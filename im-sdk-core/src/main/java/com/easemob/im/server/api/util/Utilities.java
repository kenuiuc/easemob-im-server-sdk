package com.easemob.im.server.api.util;

import org.apache.logging.log4j.util.Strings;

import java.time.Duration;
import java.time.Instant;

public class Utilities {

    public static final Duration IT_TIMEOUT = Duration.ofSeconds(60);
    public static final Duration UT_TIMEOUT = Duration.ofSeconds(3);

    public static int toExpireOnSeconds(int expireInSeconds) {
        return (int) (Instant.now().plusSeconds(expireInSeconds).toEpochMilli() / 1000);
    }

    public static String mask(String text) {
        if (Strings.isBlank(text)) {
            return text;
        } else {
            return text.replaceAll(".", "*");
        }
    }
}
