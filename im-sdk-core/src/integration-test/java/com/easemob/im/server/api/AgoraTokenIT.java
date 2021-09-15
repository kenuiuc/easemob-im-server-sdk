package com.easemob.im.server.api;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.api.token.allocate.AgoraTokenProvider;
import com.easemob.im.server.model.EMUser;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easemob.im.server.api.util.Utilities;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import static com.easemob.im.server.api.util.Utilities.IT_TIMEOUT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AgoraTokenIT {

    private static final int USER_TOKEN_EXPIRE_IN_SECONDS = 86400;
    private static final int APP_TOKEN_EXPIRE_IN_SECONDS = 86400;

    private static final Logger log = LoggerFactory.getLogger(AgoraTokenProvider.class);
    protected EMService service;

    String realm = System.getenv("IM_REALM");
    String appkey = System.getenv("IM_APPKEY");
    String baseUri = System.getenv("IM_BASE_URI");
    String appId = System.getenv("IM_APP_ID");
    String appCert = System.getenv("IM_APP_CERT");

    @BeforeAll
    public void init() {
        Assumptions.assumeTrue(EMProperties.Realm.AGORA_REALM.name().equals(realm));
        EMProperties properties = EMProperties.builder()
                .setHttpLogFormat(AdvancedByteBufFormat.TEXTUAL)
                .setAgoraTokenExpireInSeconds(APP_TOKEN_EXPIRE_IN_SECONDS)
                .setRealm(EMProperties.Realm.AGORA_REALM)
                .setBaseUri(baseUri)
                .setAppkey(appkey)
                .setAppId(appId)
                .setAppCert(appCert)
                .build();
        this.service = new EMService(properties);
    }

    @Test
    public void getUserToken(){
        String userName = "agora-ken-0";
        String password = "1234567890";
        service.user().create(userName, password).block(IT_TIMEOUT);
        EMUser kenUser = service.user().get(userName).block(Utilities.IT_TIMEOUT);
        String kenAgoraToken = service.token().getUserToken(kenUser,
                USER_TOKEN_EXPIRE_IN_SECONDS, null, null
        );
        log.info("kenAgoraToken = {}", kenAgoraToken);
    }
}
