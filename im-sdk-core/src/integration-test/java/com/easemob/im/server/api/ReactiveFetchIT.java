package com.easemob.im.server.api;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ReactiveFetchIT {

   private static final String APP_KEY = System.getenv("IM_APPKEY");
   private static final String APP_ID = System.getenv("IM_APP_ID");
   private static final String APP_CERTIFICATE = System.getenv("IM_APP_CERTIFICATE");

   private static final String USER_NAME = "ken-0";
   private static final String GATEWAY_BASE_URI = "http://hsb-didi-guangzhou-mesos-slave4:31032";
   private static final Logger log = LoggerFactory.getLogger(ReactiveFetchIT.class);

   private final EMService service;

   private void sleep(int seconds) {
      try {
         Thread.sleep(seconds * 1000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public ReactiveFetchIT() {
      EMProperties agoraGateway = EMProperties.builder(EMProperties.Realm.AGORA_REALM)
              .setExpire(20)
              .setBaseUri(GATEWAY_BASE_URI)
              .setAppkey(APP_KEY)
              .setAppId(APP_ID)
              .setAppCertificate(APP_CERTIFICATE)
              .setHttpConnectionPoolSize(10)
              .setServerTimezone("+8")
              .build();
      this.service = new EMService(agoraGateway);
   }

   @Test
   public void getUser() {
      int successCount = 0;
      try {
         for (int i = 0; i < 30; i ++) {
            sleep(1);
            EMUser user = service.user().get(USER_NAME).block();
            log.info("user = {}", user.toString());
            String uuid = user.getUuid();
            log.info("uuid = {}", uuid);
            successCount ++;
         }
      } catch (Throwable th) {
         log.error("failed to GET user", th);
      } finally {
         log.info("successCount = {}", successCount);
      }
      Assertions.assertEquals(30, successCount);
   }


   @Disabled
   public void cacheMono() {

      Mono<Integer> monoInt = Mono.fromCallable(() -> {
         System.out.println("Go!");
         return 1;
      });

      Mono<Integer> cacheInt = monoInt.cache(Duration.ofSeconds(4));

      System.out.println("Mono w/o Cache, 1 second in the middle");
      sleep(1);
      System.out.println("first mono = " + monoInt.block());
      sleep(1);
      System.out.println("second mono = " + monoInt.block());
      sleep(1);
      System.out.println("third mono = " + monoInt.block());
      System.out.println();

      System.out.println("Mono with Cache with TTL = 4 seconds, 5 seconds in the middle");
      sleep(5);
      System.out.println("first mono = " + cacheInt.block());
      sleep(5);
      System.out.println("second mono = " + cacheInt.block());
      sleep(5);
      System.out.println("third mono = " + cacheInt.block());
      System.out.println();

      System.out.println("Mono with Cache with TTL = 4 seconds, 1 second in the middle");
      sleep(1);
      System.out.println("first mono = " + cacheInt.block());
      sleep(1);
      System.out.println("second mono = " + cacheInt.block());
      sleep(1);
      System.out.println("third mono = " + cacheInt.block());
      sleep(1);
      System.out.println("fourth mono = " + cacheInt.block());
      System.out.println("about to sleep 5 seconds");
      sleep(5);
      System.out.println("fifth mono = " + cacheInt.block());
      System.out.println();
   }
}
