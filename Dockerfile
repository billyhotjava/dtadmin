# 基础镜像
FROM  openjdk:25-ea-17-jdk-slim-bullseye


WORKDIR /home

RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list
RUN apt update && apt install openssl && openssl s_client -connect sso.yuzhicloud.com:443 \
                     -servername sso.yuzhicloud.com \
                     </dev/null 2>/dev/null | \
    openssl x509 -outform PEM > /usr/local/share/ca-certificates/keycloak-ca.crt && \
    update-ca-certificates && \
    keytool -import -noprompt -trustcacerts \
            -alias internal-ca \
            -file "/usr/local/share/ca-certificates/keycloak-ca.crt" \
            -keystore $JAVA_HOME/lib/security/cacerts \
            -storepass changeit 

# 复制jar文件到路径
COPY ./target/*.jar /home
# 启动认证服务
ENTRYPOINT ["java","-jar","dtadmin-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]