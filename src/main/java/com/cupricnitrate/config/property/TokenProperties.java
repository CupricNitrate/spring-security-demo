package com.cupricnitrate.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * token配置属性类
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
@Component
@ConfigurationProperties(prefix = TokenProperties.PREFIX)
public class TokenProperties {
    public static final String PREFIX = "token";

    /**
     * Http报头中令牌自定义标识，默认：Authorization
     */
    private String header = "Authorization";

    /**
     * Http报头中令牌自定义标识中的开头,默认：Bearer
     */
    private String prefix = "Bearer ";

    /**
     * 访问令牌相关属性
     */
    private AccessToken access;

    /**
     * 刷新令牌相关属性
     */
    private RefreshToken refresh;


    @Data
    public static class AccessToken{
        /**
         * 访问令牌过期时间，单位ms，默认60s
         */
        private Long expireTime = 60 * 1000L;

        /**
         * 访问令牌私钥文件访问路径，比如/user/auth_key/rsa_key
         */
        private String privateKey;

        /**
         * 访问令牌公钥文件访问路径，比如/user/auth_key/rsa_key.pub
         */
        private String publicKey;

    }

    @Data
    public static class RefreshToken{
        /**
         * 刷新令牌过期时间，单位ms，默认30天
         */
        private Long expireTime = 30 * 24 * 60 * 60 * 1000L;

        /**
         * 访问令牌私钥文件访问路径，比如/user/auth_key/rsa_key
         */
        private String privateKey;

        /**
         * 访问令牌公钥文件访问路径，比如/user/auth_key/rsa_key.pub
         */
        private String publicKey;
    }
}
