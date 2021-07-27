package com.easemob.im.server.api.token.allocate;

import com.easemob.im.server.api.token.agora.AccessToken2;
import com.easemob.im.server.exception.EMForbiddenException;
import com.easemob.im.server.exception.EMInvalidStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

public class AccessToken2Utils {

    private static final Logger log = LoggerFactory.getLogger(AccessToken2Utils.class);
    private static final String ERROR_MSG = "failed to build AccessToken2";

    public static String buildAppToken(String appId, String appCertificate, int expire) {
        AccessToken2 accessToken = new AccessToken2(appId, appCertificate, expire);
        AccessToken2.Service serviceChat = new AccessToken2.ServiceChat();
        serviceChat.addPrivilegeChat(AccessToken2.PrivilegeChat.PRIVILEGE_CHAT_APP, expire);
        accessToken.addService(serviceChat);
        try {
            return accessToken.build();
        } catch (Exception e) {
            log.error(ERROR_MSG, e);
            throw new EMInvalidStateException(ERROR_MSG);
        }
    }

    public static String buildUserChatToken(String appId, String appCertificate,
            String userId, int expire) {

        AccessToken2 accessToken = new AccessToken2(appId, appCertificate, expire);
        AccessToken2.Service serviceChat = new AccessToken2.ServiceChat(userId);
        serviceChat.addPrivilegeChat(AccessToken2.PrivilegeChat.PRIVILEGE_CHAT_USER, expire);
        accessToken.addService(serviceChat);

        try {
            return accessToken.build();
        } catch (Exception e) {
            log.error(ERROR_MSG, e);
            throw new EMInvalidStateException(ERROR_MSG);
        }
    }

    public static String buildCustomizedToken(String appId, String appCertificate, String userId,
            int expire, Consumer<AccessToken2> tokenConfigurer) {

        AccessToken2 accessToken = new AccessToken2(appId, appCertificate, expire);
        AccessToken2.Service serviceChat = new AccessToken2.ServiceChat(userId);
        serviceChat.addPrivilegeChat(AccessToken2.PrivilegeChat.PRIVILEGE_CHAT_USER, expire);
        accessToken.addService(serviceChat);

        tokenConfigurer.accept(accessToken);
        validateAccessToken2(accessToken);

        try {
            return accessToken.build();
        } catch (Exception e) {
            log.error(ERROR_MSG, e);
            throw new EMInvalidStateException(ERROR_MSG);
        }
    }

    private static void validateAccessToken2(AccessToken2 token) {
        AccessToken2.Service service = token.getService(AccessToken2.SERVICE_TYPE_CHAT);
        if (service == null) {
            return;
        }
        AccessToken2.ServiceChat serviceChat = (AccessToken2.ServiceChat) service;
        String userId = serviceChat.getUserId();
        Map<Short, Integer> chatPrivileges = serviceChat.getPrivileges();
        boolean hasUserId = userId != null;
        boolean hasAppPrivilege = chatPrivileges.get(AccessToken2.PrivilegeChat.PRIVILEGE_CHAT_APP.intValue) != null;
        boolean hasUserPrivilege = chatPrivileges.get(AccessToken2.PrivilegeChat.PRIVILEGE_CHAT_USER.intValue) != null;
        if (hasAppPrivilege && hasUserPrivilege) {
            throw new EMForbiddenException("accessToken cannot include both chatApp and chatUser privileges at the same time");
        }
        if (hasAppPrivilege && hasUserId) {
            throw new EMForbiddenException("accessToken cannot include both chatApp privilege and userId at the same time");
        }
        if (hasUserPrivilege && !hasUserId) {
            throw new EMForbiddenException("accessToken with a chatUser privilege must include an userId");
        }
    }
}
