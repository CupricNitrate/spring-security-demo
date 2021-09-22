package com.cupricnitrate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * 荷载中的数据
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimInfo {
    /**
     * 用户名
     */
    private String username;

    /**
     * 权限
     */
    private List<Authority> authorities;
}
