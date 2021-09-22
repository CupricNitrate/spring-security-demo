package com.cupricnitrate.http.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
public class RefreshTokenReqDto implements Serializable {
    private static final long serialVersionUID = 8410311036049755024L;

    /**
     * 刷新令牌
     */
    @NotBlank
    private String refreshToken;
}
