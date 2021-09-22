package com.cupricnitrate.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色表
 * @author 硝酸铜
 * @date 2021/9/22
 */
@TableName(value = "ss_role")
@Accessors(chain = true)
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = 8444473027670783298L;

    @TableId
    private Integer id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色描述
     */
    private String desc;

    /**
     * 审计字段，创建时间
     */
    private LocalDateTime createAt;
}
