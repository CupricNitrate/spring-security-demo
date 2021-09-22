package com.cupricnitrate.service;

import com.cupricnitrate.config.property.TokenProperties;
import com.cupricnitrate.http.resp.LoginRespDto;
import com.cupricnitrate.util.JwtUtils;
import com.cupricnitrate.util.RsaUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Service
public class TokenService {

    @Resource
    private TokenProperties tokenProperties;

    /**
     * 使用刷新token创建访问token
     * @param token 访问token
     * @param refreshToken 刷新token
     * @return 访问token
     */
    public LoginRespDto refreshToken(String token, String refreshToken){
        LoginRespDto resp = new LoginRespDto();
        //获取公钥和私钥
        PublicKey accessPublicKey = null;
        PrivateKey accessPrivateKey = null;
        PublicKey refreshPublicKey = null;
        PrivateKey refreshPrivateKey = null;
        try {
            //访问令牌公钥
            accessPublicKey = RsaUtils.getPublicKey(tokenProperties.getAccess().getPublicKey());
            //访问令牌私钥
            accessPrivateKey = RsaUtils.getPrivateKey(tokenProperties.getAccess().getPrivateKey());
            //刷新令牌公钥
            refreshPublicKey = RsaUtils.getPublicKey(tokenProperties.getRefresh().getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //解析刷新令牌并生成新的访问令牌
        if(JwtUtils.validateWithoutExpiration(token,accessPublicKey) &&
                JwtUtils.validateToken(refreshToken,refreshPublicKey)){
            PrivateKey key = accessPrivateKey;

            //生成新的访问令牌
            String accessToken = Optional.ofNullable(JwtUtils.parserToken(refreshToken, refreshPublicKey))
                    .map(claims ->
                            JwtUtils.generateTokenExpire(claims.getBody(),
                                    key,
                                    tokenProperties.getAccess().getExpireTime(),
                                    JwtUtils.createJTI()))
                    .orElseThrow(() -> new AccessDeniedException("访问被拒绝"));

            resp.setAccessToken(accessToken);
        }
        return resp;
    }
}
