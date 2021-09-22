package com.cupricnitrate.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupricnitrate.http.req.UserDto;
import com.cupricnitrate.mapper.UserMapper;
import com.cupricnitrate.mapper.UserRoleRelMapper;
import com.cupricnitrate.model.User;
import com.cupricnitrate.model.UserRoleRel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Service
public class UserDetailsServiceImpl extends ServiceImpl<UserMapper, User> implements UserDetailsService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserRoleRelMapper userRoleRelMapper;



    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return baseMapper.findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户名为 " + s + "的用户"));
    }

    /**
     * 注册
     * @param req 请求体
     * @return 返回体
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean register(UserDto req){
        //检查Username 是否唯一
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername,req.getUsername());
        boolean flag = baseMapper.selectCount(queryWrapper) > 0;

        if(flag){
            throw new IllegalArgumentException("用户名已存在，注册失败");
        }

        //将请求体转化为实体类，保存记录
        User user = new User();
        user.setStatus(1);
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        //插入数据库，插入成功后，被 @TableId(type = IdType.AUTO) 注释的id字段，会被设置为数据库自增的id
        baseMapper.insert(user);


        //添加一个默认角色
        UserRoleRel userRoleRel = new UserRoleRel();
        userRoleRel.setRid(2).setUid(user.getId());
        userRoleRelMapper.insert(userRoleRel);

        return true;
    }
}
