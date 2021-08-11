package com.easemob.im.server;

import com.easemob.im.server.exception.EMInvalidArgumentException;
import com.easemob.im.server.exception.EMInvalidStateException;
import com.easemob.im.server.exception.EMUnsupportedEncodingException;
import org.apache.logging.log4j.util.Strings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EMProperties {

    private final Realm realm;
    private final Credentials credentials;
    private final String baseUri;
    private final EMProxy proxy;
    private final int httpConnectionPoolSize;
    private final String serverTimezone;

    public enum Realm {
        AGORA_REALM(1),
        EASEMOB_REALM(2),
        ;
        public short intValue;
        Realm(int value) {
            intValue = (short) value;
        }
    }

    private EMProperties(Realm realm, Credentials credentials, String baseUri, EMProxy proxy, int httpConnectionPoolSize, String serverTimezone) {
        this.realm = realm;
        this.credentials = credentials;
        this.baseUri = baseUri;
        this.proxy = proxy;
        this.httpConnectionPoolSize = httpConnectionPoolSize;
        this.serverTimezone = serverTimezone;
    }

    @Deprecated
    public EMProperties(String baseUri, String appKey, EMProxy proxy, String clientId,
            String clientSecret, int httpConnectionPoolSize, String serverTimezone) {
        this.realm = Realm.EASEMOB_REALM;
        this.credentials = new EasemobAppCredentials(appKey, clientId, clientSecret);
        this.baseUri = baseUri;
        this.proxy = proxy;
        this.httpConnectionPoolSize = httpConnectionPoolSize;
        this.serverTimezone = serverTimezone;
    }

    public String getAppkey() {
        return this.credentials.getAppKey();
    }
    public String getClientId() {
        return this.credentials.getClientId();
    }
    public String getClientSecret() {
        return this.credentials.getClientSecret();
    }


    // easemob realm by default
    public static Builder builder() {
        return new Builder().setRealm(Realm.EASEMOB_REALM);
    }

    public String getBaseUri() {
        return baseUri;
    }

    public EMProxy getProxy() {
        return this.proxy;
    }

    public String getAppkeyUrlEncoded() {
        try {
            return URLEncoder.encode(this.credentials.getAppKey(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EMUnsupportedEncodingException(e.getMessage());
        }
    }

    public String getAppkeySlashDelimited() {
        return this.credentials.getAppKey().replace('#', '/');
    }

    public int getHttpConnectionPoolSize() {
        return this.httpConnectionPoolSize;
    }

    public String getServerTimezone() {
        return this.serverTimezone;
    }

    public static class Builder {
        private Realm realm;
        private String appkey;
        private String clientId;
        private String clientSecret;
        private String appId;
        private String appSecret;

        private String baseUri;
        private EMProxy proxy;
        private int httpConnectionPoolSize = 10;
        private String serverTimezone = "+8";

        public Builder setRealm(Realm realm) {
            this.realm = realm;
            return this;
        }

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setAppSecret(String appSecret) {
            this.appSecret = appSecret;
            return this;
        }

        public Builder setBaseUri(String baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public Builder setAppkey(String appkey) {
            if (Strings.isBlank(appkey)) {
                throw new EMInvalidArgumentException("appkey must not be null or blank");
            }

            String[] tokens = appkey.split("#");
            if (tokens.length != 2) {
                throw new EMInvalidArgumentException("appkey must contains #");
            }

            if (tokens[0].isEmpty()) {
                throw new EMInvalidArgumentException("appkey must contains {org}");
            }

            if (tokens[1].isEmpty()) {
                throw new EMInvalidArgumentException("appkey must contains {app}");
            }

            this.appkey = appkey;
            return this;
        }

        public Builder setProxy(EMProxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder setClientId(String clientId) {
            if (Strings.isBlank(clientId)) {
                throw new EMInvalidArgumentException("clientId must not be null or blank");
            }

            this.clientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            if (Strings.isBlank(clientSecret)) {
                throw new EMInvalidArgumentException("clientSecret must not be null or blank");
            }

            this.clientSecret = clientSecret;
            return this;
        }

        public Builder setHttpConnectionPoolSize(int httpConnectionPoolSize) {
            if (httpConnectionPoolSize < 0) {
                throw new EMInvalidArgumentException("httpConnectionPoolSize must not be negative");
            }

            this.httpConnectionPoolSize = httpConnectionPoolSize;
            return this;
        }

        public Builder setServerTimezone(String timezone) {
            this.serverTimezone = timezone;
            return this;
        }

        public EMProperties build() {
            if (this.appkey == null) {
                throw new EMInvalidStateException("appkey not set");
            }
            if (this.clientId == null) {
                throw new EMInvalidStateException("clientId not set");
            }
            if (this.clientSecret == null) {
                throw new EMInvalidStateException("clientSecret not set");
            }

            return new EMProperties(this.baseUri, this.appkey, this.proxy, this.clientId,
                    this.clientSecret,
                    this.httpConnectionPoolSize, this.serverTimezone);
        }
    }
}
