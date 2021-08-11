package com.easemob.im.server;

public class EasemobAppCredentials implements Credentials {
    private final String appKey;
    private final String clientId;
    private final String clientSecret;

    public EasemobAppCredentials(String appKey, String clientId, String clientSecret) {
        this.appKey = appKey;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public String getAppKey() {
        return appKey;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
