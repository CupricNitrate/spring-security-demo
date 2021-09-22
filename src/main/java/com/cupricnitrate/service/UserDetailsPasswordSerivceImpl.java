package com.cupricnitrate.service;

import com.cupricnitrate.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author 硝酸铜
 * @date 2021/6/8
 */
@Service
public class UserDetailsPasswordSerivceImpl implements UserDetailsPasswordService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPasswordHash) {
        return userMapper.findByUsername(userDetails.getUsername())
                .map(user -> {
                    //是密码经过默认的编码器编码后的哈希值
                    userMapper.updateById(user.withPassword(newPasswordHash));
                    return (UserDetails) user;
                })
                .orElse(userDetails);
    }
}
