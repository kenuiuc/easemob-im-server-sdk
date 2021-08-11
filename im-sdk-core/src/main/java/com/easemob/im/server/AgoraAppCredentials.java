package com.easemob.im.server;

public class AgoraAppCredentials implements Credentials {
    private final String appKey;
    private final String appId;
    private final String appCert;

    public AgoraAppCredentials(String appKey, String appId, String appCert) {
        this.appKey = appKey;
        this.appId = appId;
        this.appCert = appCert;
    }

    @Override
    public String getAppKey() {
        return appKey;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppCert() {
        return appCert;
    }

}
