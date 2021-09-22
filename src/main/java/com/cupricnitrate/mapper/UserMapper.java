package com.cupricnitrate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cupricnitrate.model.User;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from ss_user where username=#{username}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "authorities", column = "id", javaType = List.class,
                    many = @Many(select = "com.cupricnitrate.mapper.AuthorityMapper.findByUid"))
    })
    Optional<User> findByUsername(String username);
}
