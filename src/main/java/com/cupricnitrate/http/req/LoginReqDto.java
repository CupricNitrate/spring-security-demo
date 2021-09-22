package com.cupricnitrate.http.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
public class LoginReqDto implements Serializable {
    private static final long serialVersionUID = -6412703242022146246L;

    /**
     * 账号
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    @NotBlank
    private String password;
}
