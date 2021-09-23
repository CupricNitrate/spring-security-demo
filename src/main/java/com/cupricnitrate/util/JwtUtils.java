package com.cupricnitrate.util;

import com.cupricnitrate.model.Authority;
import com.cupricnitrate.model.ClaimInfo;
import com.cupricnitrate.model.Payload;
import com.cupricnitrate.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * 生成token以及校验token相关方法
 *
 * @author 硝酸铜
 * @date 2021/9/22
 */
public class JwtUtils {

    private static final String JWT_PAYLOAD_USER_KEY = "user";

    // 用于HS512加密 签名的key
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);


    /**
     * 私钥加密token
     *
     * @param claimInfo 载荷中的数据
     * @param key       key
     * @param expire    过期时间，单位ms
     * @return JWT
     */
    public static String generateTokenExpire(Object claimInfo,
                                             Key key,
                                             long expire,
                                             String id) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claim(JWT_PAYLOAD_USER_KEY, claimInfo)
                .setId(id)
                .setExpiration(new Date(now + expire))
                .setIssuedAt(new Date(now))
                //RS256加密
                .signWith(key, SignatureAlgorithm.RS256)
                //如果使用HS512加密则使用这个
                //.signWith(key, SignatureAlgorithm.HS512).compact();
                .compact();
    }

    /**
     * 解析token
     *
     * @param token 用户请求中的token
     * @param key   key
     * @return Jws<Claims>
     */
    public static Jws<Claims> parserToken(String token, Key key) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public static String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

    /**
     * 获取token中的用户信息
     *
     * @param token 用户请求中的令牌
     * @param key   key
     * @return 用户信息
     */
    public static <T> Payload<T> getInfoFromToken(String token, Key key, Class<T> userType) {
        Jws<Claims> claimsJws = parserToken(token, key);
        Claims body = claimsJws.getBody();
        Payload<T> claims = new Payload<>();
        claims.setId(body.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        claims.setUserInfo(objectMapper.convertValue(body.get(JWT_PAYLOAD_USER_KEY),userType));
        claims.setExpiration(body.getExpiration());
        claims.setIssuedAt(body.getIssuedAt());
        return claims;
    }

    /**
     * 获取token中的载荷信息
     *
     * @param token 用户请求中的令牌
     * @param key   key
     * @return 用户信息
     */
    public static <T> Payload<T> getInfoFromToken(String token, Key key) {
        Jws<Claims> claimsJws = parserToken(token, key);
        Claims body = claimsJws.getBody();
        Payload<T> claims = new Payload<>();
        claims.setId(body.getId());
        claims.setExpiration(body.getExpiration());
        claims.setIssuedAt(body.getIssuedAt());
        return claims;
    }

    /**
     * 验证 token，忽略过期
     *
     * @param jwtToken token
     * @param key      key
     * @return boolean
     */
    public static boolean validateWithoutExpiration(String jwtToken, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            if (e instanceof ExpiredJwtException) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证token
     *
     * @param jwtToken token
     * @param key      key
     * @return boolean
     */
    public static boolean validateToken(String jwtToken, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        //生成访问令牌公钥和私钥文件
        String keyPublicFilePath = "/Users/xiaoshengpeng/auth_key/key/rsa_key.pub";
        String keyPrivateFilePath = "/Users/xiaoshengpeng/auth_key/key/rsa_key";
        //RsaUtils.generateKey(keyPublicFilePath, keyPrivateFilePath, "CupricNitrate Key Token");

        //生成刷新令牌公钥和私钥文件
        String refreshPublicFilePath = "/Users/xiaoshengpeng/auth_key/refresh/rsa_key.pub";
        String refreshPrivateFilePath = "/Users/xiaoshengpeng/auth_key/refresh/rsa_key";
        //RsaUtils.generateKey(refreshPublicFilePath, refreshPrivateFilePath, "CupricNitrate Refresh Token");

        //模拟加密生成token
        PublicKey publicKey = RsaUtils.getPublicKey(keyPublicFilePath);
        PrivateKey privateKey = RsaUtils.getPrivateKey(keyPrivateFilePath);

        //权限设置
        List<ClaimInfo.ClaimAuthority> authorities = new ArrayList<>();
        ClaimInfo.ClaimAuthority authority = new ClaimInfo.ClaimAuthority();
        authority.setAuthority("ROLE_USER");
        authorities.add(authority);
        //荷载数据
        ClaimInfo claimInfo = ClaimInfo.builder()
                .username("user")
                .authorities(authorities)
                .build();

        //生成token
        String token = JwtUtils.generateTokenExpire(claimInfo, privateKey, 24 * 60 * 60 * 1000, createJTI());

        System.out.println("token: " + token);

        //模拟解密从token中获取用户信息
        ObjectMapper objectMapper = new ObjectMapper();
        //序列化时忽略值为null的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Payload<User> payload = JwtUtils.getInfoFromToken(token,
                publicKey, User.class);
        User user1 = payload.getUserInfo();
        System.out.println("user: " + objectMapper.writeValueAsString(user1));
    }
}
