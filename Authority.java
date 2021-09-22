package com.cupricnitrate.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限表实体类
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
@Accessors(chain = true)
@TableName(value = "ss_authority")
public class Authority implements GrantedAuthority {
    private static final long serialVersionUID = -8770868540836182134L;

    /**
     * 权限id
     */
    @TableId
    private Integer id;

    /**
     * 父权限id
     */
    private Integer parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限描述
     */
    private String desc;

    /**
     * 权限资源,当type为1时有值
     */
    @TableField(value = "resource")
    private String authority;

    /**
     * 权限类型:0 菜单；1 接口权限
     */
    private Integer type;

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
