# 使用 Server SDK 获取用户 token

注册环信开发者账户后，您需要为您的应用内用户创建环信用户 ID 并与您的应用内用户 ID 一一对应，这样用户在 App 端登陆后获取 token 使用 IM 服务。

## **技术原理**

### 

- 开发者在用户注时候可以通过 Server SDK 为用户创建环信用户与环信关联.
- 注册环信用户需要提供用户名和密码，环信后端会为每用户生成一个 UUID.
- 开发者可以通过 Server SDK 利用声网 appId、声网 appCert、环信用户 UUID 为用户生成 token.

## 实现步骤

### 1.准备

在获取声网 token 之前，需要准备环信 AppKey、声网 AppId（AppId）、声网 APP证书（AppCert）

* 获取环信 AppKey：
    - 1.如果您有环信管理后台账号并创建过应用，请先登录环信管理后台，点击[这里](https://console.easemob.com/user/login)，然后到"应用列表" -> 点击"查看"即可获取到appkey。
    - 2.如果您没有环信管理后台账号，请先注册账号，点击[这里](https://console.easemob.com/user/register)，注册成功后请登录，然后点击"添加应用"，添加成功后点击"查看"即可获取到appkey。

* 获取 AppId、AppCert：
    - 1.如果您有声网 Console 后台账号并创建过项目，请先登录声网 Console  后台，点击[这里](https://sso.agora.io/cn/login/)，然后到"项目列表" -> 找到自己的项目点击"编辑"图标后，即可看到 App ID、APP 证书。
    - 2.如果您没有声网Console后台账号，请先注册账号，点击[这里](https://sso.agora.io/cn/v4/signup)，注册成功后按照步骤1操作。

* 您需要自己来实现用户登录时的认证、授权，可以在 AppServer 的 "SecurityConfig" 中进行认证、授权。

### 2.配置
配置文件中需要的参数来源于"准备"中获取到的环信 appkey、声网 AppId（AppId）、声网 APP证书（AppCert）。

* 服务配置文件参考：[application.properties](./agora-app-server/src/main/resources/application.properties)
    ```
        ## 环信console 获取自己的appkey
        application.appkey=xxx
        
        ## 声网console获取appid
        application.agoraAppId=xxx
        ## 声网console获取appcert
        application.agoraCert=xxx
        ## 声网token过期时间(自已定义，不能超过1天)
        agora.token.expire.period.seconds=86400
        
        ## 本地redis
        spring.redis.host=localhost
        spring.redis.port=6379
        spring.redis.password=123456
        spring.redis.timeout=10000
        spring.redis.get.token.limit.expireTime.seconds=60
        spring.redis.get.token.limit.count=3
        
        ## data source
        spring.datasource.driver-class-name=com.mysql.jdbc.Driver
        spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxx?useSSL=false&useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true
        spring.datasource.username=root
        spring.datasource.password=123456
        spring.datasource.hikari.maximum-pool-size=50
        spring.datasource.hikari.minimum-idle=20
    
        ## jpa
        spring.jpa.show_sql=false
        spring.jpa.properties.hibernate.format_sql=true
        spring.jpa.properties.hibernate.generate_statistics=false
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
        spring.jpa.hibernate.ddl-auto=validate
        
    ```
    
* 对 Server SDK 的配置请参考 AppServer 中的 "ApplicationConfig"

### 3.使用

上述准备好，启动服务即可使用。



## 参考信息



## API 参考

## 用户注册。
TODO: