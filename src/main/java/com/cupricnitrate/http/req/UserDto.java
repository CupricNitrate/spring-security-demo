package com.cupricnitrate.http.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
public class UserDto implements Serializable {
    private static final long serialVersionUID = 7066471874944469440L;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 50, message = "用户名长度必须在4到50个字符之间")
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String matchingPassword;

}
