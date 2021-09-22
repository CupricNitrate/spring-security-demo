package com.cupricnitrate.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户-角色中间表实体类
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
@Accessors(chain = true)
@TableName(value = "ss_user_role")
public class UserRoleRel {

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 角色id
     */
    private Integer rid;
}
