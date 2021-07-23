package com.easemob.im.server.api;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.api.token.Token;
import com.easemob.im.server.api.token.agora.AccessToken2;
import com.easemob.im.server.api.token.agora.AccessToken2.PrivilegeChat;
import com.easemob.im.server.exception.EMNotFoundException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class ChatTokenUserIT {

    private static final String APP_KEY = System.getenv("IM_APPKEY");
    private static final String APP_ID = System.getenv("IM_APP_ID");
    private static final String APP_CERTIFICATE = System.getenv("IM_APP_CERTIFICATE");

    private static final String CHANNEL_NAME = "dummyChannelName";
    private static final String UID = "dummyUID";

    private static final int EXPIRE_SECONDS = 3601;
    private static final String PASSWORD = "password";
    private static final String USER_NAME = "ken-0";
    private static final String USER_ID = "da9287a0-ecf9-11eb-9af3-296ff79acb67";

    private static final String BASE_URI = "http://hsb-didi-guangzhou-mesos-slave4:31032";


    private static final Logger log = LoggerFactory.getLogger(ChatTokenUserIT.class);

    private EMService service;

    ChatTokenUserIT() {
        EMProperties properties = EMProperties.builder(EMProperties.Realm.AGORA_REALM)
                .setBaseUri(BASE_URI)
                .setAppkey(APP_KEY)
                .setAppId(APP_ID)
                .setAppCertificate(APP_CERTIFICATE)
                .setHttpConnectionPoolSize(10)
                .setServerTimezone("+8")
                .build();

        this.service = new EMService(properties);
    }

    @Test
    public void userCrud() {
        String randomUsername = String.format("im-sdk-it-ken-%08d",
                ThreadLocalRandom.current().nextInt(100000000));
        assertDoesNotThrow(() -> this.service.user().create(randomUsername, PASSWORD)
                .block());
        assertDoesNotThrow(
                () -> this.service.user().get(randomUsername).block());
        assertDoesNotThrow(
                () -> this.service.user().delete(randomUsername).block());
        assertThrows(EMNotFoundException.class,
                () -> this.service.user().get(randomUsername).block());
    }

    @Test
    public void buildChatUserToken() {
        Token userToken = service.user().buildChatToken(USER_ID, EXPIRE_SECONDS).block();
        assertNotNull(userToken);
        String userTokenValue = userToken.getValue();
        AccessToken2 token2 = new AccessToken2();
        token2.parse(userTokenValue);
        Map<Short, AccessToken2.Service> services = token2.services;
        assertEquals(1, services.size());
        AccessToken2.Service service = services.get(AccessToken2.SERVICE_TYPE_CHAT);
        AccessToken2.ServiceChat serviceChat = (AccessToken2.ServiceChat) service;
        String userIdInToken = serviceChat.getUserId();
        assertEquals(USER_ID, userIdInToken);
        Map<Short, Integer> privileges = service.getPrivileges();
        assertEquals(1, privileges.size());
        int expireInToken = privileges.get(PrivilegeChat.PRIVILEGE_CHAT_USER.intValue);
        assertEquals(EXPIRE_SECONDS, expireInToken);
    }

    @Test
    public void buildIdentityToken() throws Exception {
        Token customizedToken = service.user().buildCustomizedToken(USER_ID, EXPIRE_SECONDS,
                token ->{}).block();
        String customizedTokenValue = customizedToken.getValue();

        AccessToken2 customizedToken2 = new AccessToken2();
        customizedToken2.parse(customizedTokenValue);

        Map<Short, AccessToken2.Service> services = customizedToken2.services;
        assertEquals(1, services.size());
        AccessToken2.Service service = services.get(AccessToken2.SERVICE_TYPE_CHAT);
        AccessToken2.ServiceChat serviceChat = (AccessToken2.ServiceChat) service;
        String userIdInToken = serviceChat.getUserId();
        assertEquals(USER_ID, userIdInToken);
        Map<Short, Integer> privileges = service.getPrivileges();
        assertEquals(1, privileges.size());
        int expireInToken = privileges.get(PrivilegeChat.PRIVILEGE_CHAT_USER.intValue);
        assertEquals(EXPIRE_SECONDS, expireInToken);
    }

    @Test
    public void buildChatRtcToken() throws Exception {
        Token customizedToken = service.user().buildCustomizedToken(
                USER_ID, EXPIRE_SECONDS, token ->{
                    AccessToken2.ServiceRtc serviceRtc =
                            new AccessToken2.ServiceRtc(CHANNEL_NAME, UID);
                    serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, EXPIRE_SECONDS);
                    token.addService(serviceRtc);
                }).block();
        String customizedTokenValue = customizedToken.getValue();
        AccessToken2 chatRtcToken = new AccessToken2();
        chatRtcToken.parse(customizedTokenValue);

        Map<Short, AccessToken2.Service> services = chatRtcToken.services;
        assertEquals(2, services.size());

        AccessToken2.Service service1 = services.get(AccessToken2.SERVICE_TYPE_CHAT);
        AccessToken2.ServiceChat serviceChat = (AccessToken2.ServiceChat) service1;
        String userIdInToken = serviceChat.getUserId();
        assertEquals(USER_ID, userIdInToken);
        Map<Short, Integer> privilegesChat = service1.getPrivileges();
        assertEquals(1, privilegesChat.size());
        int expireInTokenChat = privilegesChat.get(PrivilegeChat.PRIVILEGE_CHAT_USER.intValue);
        assertEquals(EXPIRE_SECONDS, expireInTokenChat);

        AccessToken2.Service service2 = services.get(AccessToken2.SERVICE_TYPE_RTC);
        AccessToken2.ServiceRtc serviceRtc = (AccessToken2.ServiceRtc) service2;
        String channelName = serviceRtc.getChannelName();
        String uid = serviceRtc.getUid();
        assertEquals(CHANNEL_NAME, channelName);
        assertEquals(UID, uid);
        Map<Short, Integer> privilegesRtc = service2.getPrivileges();
        assertEquals(1, privilegesRtc.size());
        int expireInTokenRtc = privilegesRtc.get(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL.intValue);
        assertEquals(EXPIRE_SECONDS, expireInTokenRtc);
    }

}

