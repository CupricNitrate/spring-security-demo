package com.cupricnitrate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cupricnitrate.model.Authority;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
public interface AuthorityMapper extends BaseMapper<Authority> {

    /**
     * 根据 用户id 查询权限
     * @param uid
     * @return
     */
    @Select("SELECT a.*\n" +
            "FROM ss_user u LEFT JOIN ss_user_role_rel ur ON u.id = ur.uid\n" +
            "LEFT JOIN ss_role r ON ur.rid = r.id\n" +
            "LEFT JOIN ss_authority_role_rel ar ON r.id = ar.role_id\n" +
            "LEFT JOIN ss_authority a ON ar.authority_id = a.id\n" +
            "WHERE\n" +
            "u.id = #{uid}\n" +
            "and a.type = 1\n" +
            "GROUP BY a.id")
    List<Authority> findByUid(Integer uid);
}
