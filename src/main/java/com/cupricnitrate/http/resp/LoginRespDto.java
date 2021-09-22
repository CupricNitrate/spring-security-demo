package com.cupricnitrate.http.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 硝酸铜
 * @date 2021/6/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LoginRespDto implements Serializable {
    private static final long serialVersionUID = 3616751156914483492L;

    private String accessToken;
    private String refreshToken;
}
